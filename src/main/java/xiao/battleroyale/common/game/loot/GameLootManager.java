package xiao.battleroyale.common.game.loot;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GameLootManager extends AbstractGameManager {

    private static class GameLootManagerHolder {
        private static final GameLootManager INSTANCE = new GameLootManager();
    }

    public static GameLootManager get() {
        return GameLootManagerHolder.INSTANCE;
    }

    private GameLootManager() {
    }

    public static void init() {
        // 预计算
        cachedCenterOffset.clear();
        cachedCenterOffset.addAll(BfsCalculator.calculateCenterOffset(64));
    }

    public int getMaxLootChunkPerTick() { return MAX_LOOT_CHUNK_PER_TICK; }
    public int getMaxLootDistance() { return MAX_LOOT_DISTANCE; }
    public int getTolerantCenterDistance() { return TOLERANT_CENTER_DISTANCE; }
    public int getMaxCachedCenter() { return MAX_CACHED_CENTER; }
    public int getMaxQueuedChunk() { return MAX_QUEUED_CHUNK; }
    public int getBfsFrequency() { return BFS_FREQUENCY; }
    public boolean isInstantNextBfs() { return INSTANT_NEXT_BFS; }
    public int getMaxCachedLootChunk() { return MAX_CACHED_LOOT_CHUNK; }
    public int getCleanCachedChunk() { return CLEAN_CACHED_CHUNK; }

    public int getLastBfsTime() { return lastBfsTime; }
    public int getLastBfsProcessedLoot() { return lastBfsProcessedLoot; }
    public int queuedChunksRefSize() { return queuedChunksRef.get().size(); }
    public int processedChunkCacheSize() { return processedChunkCache.size(); }
    public int cachedPlayerCenterChunksSize() { return cachedPlayerCenterChunks.size(); }
    public int cachedCenterOffsetSize() { return cachedCenterOffset.size(); }

    private int MAX_LOOT_CHUNK_PER_TICK = 5; // 每Tick最多处理的区块数
    private int MAX_LOOT_DISTANCE = 16; // BFS广度
    private int TOLERANT_CENTER_DISTANCE = 3; // 将玩家中心周围一定距离的区块也算作中心区块
    private int MAX_CACHED_CENTER; // 缓存玩家中心区块
    private int MAX_QUEUED_CHUNK; // 最大待处理区块数
    private int BFS_FREQUENCY; // BFS频率
    private boolean INSTANT_NEXT_BFS;
    private int MAX_CACHED_LOOT_CHUNK; // 最大记录的处理过区块数
    private int CLEAN_CACHED_CHUNK; // 每次清理删除多少区块
    public void applyConfig(GeneratorEntry entry) {
        MAX_LOOT_CHUNK_PER_TICK = Math.min(Math.max(entry.maxGameTickLootChunk, 5), 500);
        MAX_LOOT_DISTANCE = Math.min(Math.max(entry.maxGameLootDistance, 3), 128);
        if (MAX_LOOT_DISTANCE >= cachedCenterOffset.size()) { // cachedCenterOffest第一项为0距离
            cachedCenterOffset.clear();
            cachedCenterOffset.addAll(BfsCalculator.calculateCenterOffset(MAX_LOOT_DISTANCE));
        }
        TOLERANT_CENTER_DISTANCE = Math.min(Math.max(entry.tolerantCenterDistance, 0), 10);
        MAX_CACHED_CENTER = Math.min(Math.max(entry.maxCachedCenter, 0), 50000);
        MAX_QUEUED_CHUNK = Math.min(Math.max(entry.maxQueuedChunk, 100), 200000);
        BFS_FREQUENCY = Math.max(entry.bfsFrequency, 100);
        INSTANT_NEXT_BFS = entry.instantNextBfs;
        MAX_CACHED_LOOT_CHUNK = Math.min(Math.max(entry.maxCachedLootChunk, 100), 300000);
        CLEAN_CACHED_CHUNK = Math.min(Math.max(entry.cleanCachedChunk, 10), 10000);
    }

    private int lastBfsTime = Integer.MIN_VALUE / 2;
    private int lastBfsProcessedLoot = 0;
    // 原子地引用当前待处理队列
    private final AtomicReference<Queue<ChunkPos>> queuedChunksRef = new AtomicReference<>(new ArrayDeque<>());
    private final ProcessedChunkCache processedChunkCache = new ProcessedChunkCache();
    private final ProcessedChunkCache cachedPlayerCenterChunks = new ProcessedChunkCache();
    private static final List<List<Offset2D>> cachedCenterOffset = new ArrayList<>();
    public record Offset2D(int x, int z) {}

    private ExecutorService bfsExecutor;
    private Future<?> bfsTaskFuture;

    /**
     * 封装已处理区块的Set和Queue，确保数据一致性。
     */
    private static class ProcessedChunkCache {
        private final Set<ChunkPos> set = new HashSet<>();
        private final Queue<ChunkPos> queue = new ArrayDeque<>();
        public boolean add(ChunkPos chunkPos) {
            if (set.add(chunkPos)) {
                queue.add(chunkPos);
                return true;
            }
            return false;
        }
        public boolean contains(ChunkPos chunkPos) {
            return set.contains(chunkPos);
        }
        public int size() {
            return queue.size();
        }
        public void removeOldest(int count) {
            int chunksToRemove = Math.min(count, size());
            for (int i = 0; i < chunksToRemove; i++) {
                ChunkPos chunkToRemove = queue.poll();
                if (chunkToRemove != null) {
                    set.remove(chunkToRemove);
                }
            }
        }
        public void clear() {
            set.clear();
            queue.clear();
        }
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        clear();

        if (bfsExecutor != null) {
            List<Runnable> unexecutedTasks = bfsExecutor.shutdownNow();
            if (!unexecutedTasks.isEmpty()) {
                BattleRoyale.LOGGER.warn("GameLootManager: {} BFS tasks were not executed before new game init.", unexecutedTasks.size());
            }
            try {
                if (!bfsExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    BattleRoyale.LOGGER.error("GameLootManager: BFS executor did not terminate in time during new game init.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                BattleRoyale.LOGGER.error("GameLootManager: Interrupted while waiting for BFS executor to terminate during new game init.", e);
            }
        }

        bfsExecutor = Executors.newSingleThreadExecutor();
        bfsTaskFuture = null;
        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        clear();
        this.ready = true;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame() || !prepared) {
            return false;
        }
        return ready;
    }

    /**
     * 获取BFS距离，取模拟距离和配置项的最小值
     * 无法获取游戏维度则使用配置项最大值
     */
    public int getSimulationDistance() {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            return Math.min(serverLevel.getServer().getPlayerList().getSimulationDistance(), MAX_LOOT_DISTANCE);
        }
        return MAX_LOOT_DISTANCE;
    }

    /**
     * 在每个游戏tick中调用，处理战利品刷新逻辑。
     * @param gameTime 当前游戏tick数
     */
    public void onGameTick(int gameTime) {
        // 检查是否需要强制执行新的BFS
        if (gameTime - lastBfsTime >= BFS_FREQUENCY) {
            submitNewBfsTask();
        }

        // 获取当前正在被处理的队列
        Queue<ChunkPos> currentQueue = queuedChunksRef.get();
        // 区块已经处理完，立即执行下一次BFS或不操作
        if (currentQueue.isEmpty()) {
            if (INSTANT_NEXT_BFS) {
                // 如果队列为空且配置允许，立即提交下一次BFS任务
                submitNewBfsTask();
            } else {
                return;
            }
        }

        // 区块刷新
        processLootGeneration();

        // 清理已处理区块缓存
        if (processedChunkCache.size() > MAX_CACHED_LOOT_CHUNK) {
            processedChunkCache.removeOldest(CLEAN_CACHED_CHUNK);
            BattleRoyale.LOGGER.debug("Cleaned {} processed chunks. Remaining: {}", CLEAN_CACHED_CHUNK, processedChunkCache.size());
        }

        // 清理 cachedPlayerCenterChunks，默认清理30%
        if (cachedPlayerCenterChunks.size() > MAX_CACHED_CENTER) {
            int chunksToRemove = (int) (cachedPlayerCenterChunks.size() * 0.3); // 清理30%
            chunksToRemove = Math.max(chunksToRemove, 10); // 至少清理10个
            cachedPlayerCenterChunks.removeOldest(chunksToRemove);
            BattleRoyale.LOGGER.debug("Cleaned {} cached center chunks. Remaining: {}", chunksToRemove, cachedPlayerCenterChunks.size());
        }
    }

    /**
     * 提交一个新的BFS任务到异步线程。
     * 解决了强制频率和立即触发的竞态问题。
     */
    private void submitNewBfsTask() {
        // 检查 future 是否为空或者已经完成，确保没有正在运行的任务
        if (bfsTaskFuture == null || bfsTaskFuture.isDone()) {
            // 如果上一个任务被取消，重新记录时间
            if (bfsTaskFuture != null && bfsTaskFuture.isCancelled()) {
                BattleRoyale.LOGGER.debug("Previous BFS task was cancelled, submitting a new one.");
            }
            lastBfsTime = GameManager.get().getGameTime(); // 确保主线程下一tick能识别到
            bfsTaskFuture = bfsExecutor.submit(this::bfsQueuedChunkAsync);
        } else {
            // BattleRoyale.LOGGER.info("Attempt to cancel BFS");
            // 如果有正在运行的任务，取消它并提交新的
            if (bfsTaskFuture.cancel(true)) {
                BattleRoyale.LOGGER.debug("A running BFS task was cancelled. Submitting a new one.");
                lastBfsTime = GameManager.get().getGameTime(); // 确保主线程下一tick能识别到
                bfsTaskFuture = bfsExecutor.submit(this::bfsQueuedChunkAsync);
            } else {
                BattleRoyale.LOGGER.warn("Tried to cancel a running BFS task, but it failed.");
            }
        }
    }

    /**
     * 遍历存活玩家最后位置，BFS计算待处理区块（异步版本）
     */
    private void bfsQueuedChunkAsync() {
        // BattleRoyale.LOGGER.debug("Last BFS processed loot:{}", lastBfsProcessedLoot);
        lastBfsProcessedLoot = 0;

        // 使用一个本地队列来存储计算结果，避免线程冲突
        Queue<ChunkPos> newChunkQueue = new ArrayDeque<>();
        Set<ChunkPos> visitedInBfs = new HashSet<>();

        List<GamePlayer> gamePlayers = GameManager.get().getStandingGamePlayers();

        // 如果玩家中心区块已在缓存中，则跳过对该玩家的BFS
        List<GamePlayer> playersToBFS = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.isActiveEntity()) {
                Vec3 lastPos = gamePlayer.getLastPos();
                ChunkPos playerChunkPos = new ChunkPos((int) (lastPos.x() / 16), (int) (lastPos.z() / 16));
                if (cachedPlayerCenterChunks.contains(playerChunkPos)) {
                    // BattleRoyale.LOGGER.debug("Skipping BFS for player at cached center chunk {}", playerChunkPos);
                    continue;
                }
                playersToBFS.add(gamePlayer);
            }
        }

        // 按照距离层级（圈）遍历，确保近距离区块优先
        int bfsDistance = getSimulationDistance();
        for (int i = 0; i <= bfsDistance; i++) {
            // 使用本地队列的尺寸来控制，避免线程冲突
            if (newChunkQueue.size() >= MAX_QUEUED_CHUNK) {
                break;
            }

            List<Offset2D> centerOffset = cachedCenterOffset.get(i);
            for (GamePlayer gamePlayer : playersToBFS) {
                Vec3 lastPos = gamePlayer.getLastPos();
                ChunkPos playerChunkPos = new ChunkPos((int) (lastPos.x() / 16), (int) (lastPos.z() / 16));

                for (Offset2D offset2D : centerOffset) {
                    ChunkPos newChunkPos = new ChunkPos(playerChunkPos.x + offset2D.x, playerChunkPos.z + offset2D.z);
                    if (!visitedInBfs.add(newChunkPos) || processedChunkCache.contains(newChunkPos)) {
                        continue;
                    }
                    newChunkQueue.add(newChunkPos);
                    // 在容忍距离内就当作中心区块记录
                    if (i < TOLERANT_CENTER_DISTANCE && cachedPlayerCenterChunks.size() < MAX_CACHED_CENTER) {
                        cachedPlayerCenterChunks.add(newChunkPos);
                    }
                    // 控制待处理队列长度
                    if (newChunkQueue.size() >= MAX_QUEUED_CHUNK) {
                        break;
                    }
                }
                if (newChunkQueue.size() >= MAX_QUEUED_CHUNK) {
                    break;
                }
            }
        }

        // 使用原子操作替换旧队列
        Queue<ChunkPos> oldQueue = queuedChunksRef.getAndSet(newChunkQueue);
        // 清空旧队列，方便GC
        oldQueue.clear();

        // BattleRoyale.LOGGER.debug("GameLootManager finished async BFS, added {} queued chunk. Old queue size was {}.", newChunkQueue.size(), oldQueue.size());
    }

    private void processLootGeneration() {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }

        int processedCount = 0;
        // 从原子引用的队列中获取
        Queue<ChunkPos> currentQueue = queuedChunksRef.get();
        while (!currentQueue.isEmpty() && processedCount < MAX_LOOT_CHUNK_PER_TICK) {
            ChunkPos chunkPos = currentQueue.poll();
            processedChunkCache.add(chunkPos);
            lastBfsProcessedLoot += LootGenerator.refreshLootInChunk(new LootContext(serverLevel, chunkPos, GameManager.get().getGameId()));
            processedCount++;
        }
        // ChatUtils.sendMessageToAllPlayers(serverLevel, "Chunk Processed this tick: " + processedCount);
        // BattleRoyale.LOGGER.info("Chunk Processed this tick: {}", processedCount);
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        // BattleRoyale.LOGGER.debug("GameLootManager stopped, last BFS processed loot:{}", lastBfsProcessedLoot);
        clear(); // 清理所有内部状态
        this.prepared = false;
        this.ready = false;
        if (bfsExecutor != null) {
            bfsExecutor.shutdown();
        }
    }

    /**
     * 清空所有内部数据结构和状态。
     */
    private void clear() {
        lastBfsTime = -BFS_FREQUENCY + 20; // 延迟1秒，等玩家传送到地图内开始提交BFS
        lastBfsProcessedLoot = 0;
        queuedChunksRef.get().clear();
        processedChunkCache.clear();
        cachedPlayerCenterChunks.clear();
        bfsTaskFuture = null;
    }
}