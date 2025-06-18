package xiao.battleroyale.common.game.loot;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.loot.LootGenerator;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameLootManager extends AbstractGameManager {

    private static class GameLootManagerHolder {
        private static final GameLootManager INSTANCE = new GameLootManager();
    }

    public static GameLootManager get() {
        return GameLootManagerHolder.INSTANCE;
    }

    private GameLootManager() {
        this.processedChunksTracker = Collections.synchronizedSet(new HashSet<>());
        this.processedChunkInfoMap = Collections.synchronizedMap(new HashMap<>());
        this.chunksToProcessQueue = new ConcurrentLinkedQueue<>();
        this.playerLastKnownChunk = Collections.synchronizedMap(new HashMap<>());
        this.playerLastKnownSimulationDistance = Collections.synchronizedMap(new HashMap<>());

        this.evictionIntervalTicks = 20 * 10; // 默认10秒清理一次
        this.lootAreaCalculator = new LootAreaCalculator();
    }

    public static void init() {
        ;
    }

    private static GameLootManager instance;

    // 配置参数
    private int maxChunksPerTick = 150; // 每tick最大处理的区块数
    private int processedChunkRetentionTimeSeconds = 600; // 已处理区块保留时间（秒）
    private int maxProcessedChunksCapacityFactor = 2; // 基于玩家模拟距离的容量因子

    // 全量BFS相关参数
    private long lastFullBFSTick; // 上次全量BFS的时间

    // 核心数据结构
    private final Set<ChunkPos> processedChunksTracker; // 追踪已处理的区块
    private final Map<ChunkPos, ProcessedChunkInfo> processedChunkInfoMap; // 存储已处理区块的详细信息
    private final Queue<ChunkPos> chunksToProcessQueue; // 待处理区块队列
    private final Map<UUID, ChunkPos> playerLastKnownChunk; // 玩家上次已知区块位置
    private final Map<UUID, Integer> playerLastKnownSimulationDistance; // 玩家上次已知模拟距离

    // 清理相关
    private long lastEvictionCheckTick; // 上次清理时间
    private int evictionIntervalTicks; // 清理间隔tick数

    // 战利品区域计算器
    private final LootAreaCalculator lootAreaCalculator;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        // TODO: 从配置文件加载所有参数，目前使用默认值。
        // this.maxChunksPerTick = ModConfigs.COMMON.MAX_CHUNKS_PER_TICK.get();
        // this.processedChunkRetentionTimeSeconds = ModConfigs.COMMON.PROCESSED_CHUNK_RETENTION_TIME_SECONDS.get();
        // this.maxProcessedChunksCapacityFactor = ModConfigs.COMMON.MAX_PROCESSED_CHUNKS_CAPACITY_FACTOR.get();
        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        clear(); // 确保每次游戏开始时清空所有状态
        int gameTime = GameManager.get().getGameTime();
        this.lastEvictionCheckTick = gameTime; // 初始化清理时间
        this.lastFullBFSTick = gameTime; // 初始化全量BFS时间

        // 游戏启动时，强制进行一次全量BFS初始化，确保玩家初始区域战利品刷新
        initializeLootForPlayers(serverLevel);
        this.ready = true;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame() || !prepared) {
            return false;
        }
        // startGame 应该在 initGame 之后调用，无需重复初始化逻辑
        return ready;
    }

    /**
     * 在每个游戏tick中调用，处理战利品刷新逻辑。
     * @param gameTime 当前游戏tick数
     */
    public void onGameTick(int gameTime) {
        // 确保游戏处于准备就绪且正在进行中
        if (!this.ready || !GameManager.get().isInGame()) {
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }

        // 1. 定期执行全量BFS战利品刷新
        int chunksForFullBFS = calculateTotalBFSChunks(); // 计算所有玩家区域需要BFS的区块总数
        // 估算完成一次全量BFS所需的时间（以tick计），至少为1tick
        int estimatedTicksForFullBFS = Math.max(1, (int) Math.ceil((double) chunksForFullBFS / maxChunksPerTick));
        // 下次全量BFS的间隔为完成本次全量BFS所需时间的2倍
        int nextFullBFSIntervalTicks = estimatedTicksForFullBFS * 2;

        if (gameTime - lastFullBFSTick >= nextFullBFSIntervalTicks) {
            initializeLootForPlayers(serverLevel); // 重新触发全量BFS逻辑
            lastFullBFSTick = gameTime;
        }

        // 2. 根据玩家移动动态添加待处理区块 (增量更新)
        updatePlayerLootAreasIncremental(serverLevel, gameTime);

        // 3. 处理待处理区块队列中的战利品刷新任务
        processChunksFromQueue(serverLevel);

        // 4. 定时清理过期或超出容量限制的已处理区块记录
        if (gameTime - lastEvictionCheckTick >= evictionIntervalTicks) {
            cleanUpProcessedChunks(gameTime);
            lastEvictionCheckTick = gameTime;
        }
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        clear(); // 清理所有内部状态
        this.prepared = false;
        this.ready = false;
    }

    /**
     * 清空所有内部数据结构和状态。
     */
    private void clear() {
        this.processedChunksTracker.clear();
        this.processedChunkInfoMap.clear();
        this.chunksToProcessQueue.clear();
        this.playerLastKnownChunk.clear();
        this.playerLastKnownSimulationDistance.clear();
        this.lastEvictionCheckTick = 0;
        this.lastFullBFSTick = 0;
        this.lootAreaCalculator.clearCache(null); // 清理所有缓存
    }

    /**
     * 游戏开始时或定期，对所有在线玩家的模拟距离区域进行全量BFS，确保战利品刷新。
     */
    private void initializeLootForPlayers(ServerLevel serverLevel) {
        Set<UUID> currentActivePlayerUUIDs = new HashSet<>();

        // 获取正在游戏中且未被淘汰的玩家列表
        List<GamePlayer> standingGamePlayers = GameManager.get().getStandingGamePlayers();
        if (standingGamePlayers.isEmpty()) {
            return;
        }

        for (GamePlayer gamePlayer : standingGamePlayers) {
            // 获取对应的 ServerPlayer 对象，确保玩家在线且在当前世界
            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(gamePlayer.getPlayerUUID());
            if (player != null && player.level() == serverLevel) {
                currentActivePlayerUUIDs.add(player.getUUID());
                ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                int simulationDistance = player.server.getPlayerList().getSimulationDistance();

                playerLastKnownChunk.put(player.getUUID(), playerChunk);
                playerLastKnownSimulationDistance.put(player.getUUID(), simulationDistance);

                // 对每个玩家的当前位置进行全量BFS扩散，并将相关区块加入待处理队列
                performFullBFSForPlayerArea(playerChunk, simulationDistance, GameManager.get().getGameTime());
            } else {
                BattleRoyale.LOGGER.debug("Skipping loot initialization for game player {} (offline or not in current level).", gamePlayer.getPlayerName());
            }
        }
        // 清理已下线或已淘汰玩家的旧记录
        playerLastKnownChunk.keySet().retainAll(currentActivePlayerUUIDs);
        playerLastKnownSimulationDistance.keySet().retainAll(currentActivePlayerUUIDs);
    }

    /**
     * 执行单个玩家模拟距离区域的完整BFS，将所有相关区块（新区块或模拟距离增加的区块）加入待处理队列。
     * @param centerChunk 玩家所在区块
     * @param simulationDistance 玩家的模拟距离
     * @param gameTime 当前游戏时间
     */
    private void performFullBFSForPlayerArea(ChunkPos centerChunk, int simulationDistance, long gameTime) {
        Queue<ChunkPos> bfsQueue = new ArrayDeque<>();
        Set<ChunkPos> visitedThisBFS = new HashSet<>(); // 记录本次BFS中已访问的区块，防止重复处理

        bfsQueue.offer(centerChunk);
        visitedThisBFS.add(centerChunk);

        // 优先将中心区块加入处理队列（如果需要）
        if (!processedChunksTracker.contains(centerChunk) ||
                (processedChunkInfoMap.get(centerChunk) != null && simulationDistance > processedChunkInfoMap.get(centerChunk).getSimulationDistanceAtProcessing())) {
            addChunkToProcessingQueue(centerChunk, simulationDistance, gameTime);
        }

        for (int i = 0; i < simulationDistance; i++) {
            int currentLevelSize = bfsQueue.size();
            if (currentLevelSize == 0) break; // 当前层级没有更多区块

            for (int j = 0; j < currentLevelSize; j++) {
                ChunkPos current = bfsQueue.poll();
                // 遍历当前区块的四个邻居
                ChunkPos[] neighbors = new ChunkPos[]{
                        new ChunkPos(current.x + 1, current.z),
                        new ChunkPos(current.x - 1, current.z),
                        new ChunkPos(current.x, current.z + 1),
                        new ChunkPos(current.x, current.z - 1)
                };

                for (ChunkPos neighbor : neighbors) {
                    if (visitedThisBFS.add(neighbor)) { // 如果是本次BFS中新发现的区块
                        // 只有当区块从未被处理过，或者它被处理时模拟距离小于当前模拟距离，才需要加入队列
                        if (!processedChunksTracker.contains(neighbor) ||
                                (processedChunkInfoMap.get(neighbor) != null && simulationDistance > processedChunkInfoMap.get(neighbor).getSimulationDistanceAtProcessing())) {
                            addChunkToProcessingQueue(neighbor, simulationDistance, gameTime);
                        }
                        bfsQueue.offer(neighbor);
                    }
                }
            }
        }
    }

    /**
     * 根据玩家移动动态计算并添加新的待处理区块（增量更新）。
     */
    private void updatePlayerLootAreasIncremental(ServerLevel serverLevel, long gameTime) {
        Set<UUID> currentActivePlayerUUIDs = new HashSet<>();
        List<GamePlayer> standingGamePlayers = GameManager.get().getStandingGamePlayers();

        for (GamePlayer gamePlayer : standingGamePlayers) {
            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(gamePlayer.getPlayerUUID());
            if (player != null && player.level() == serverLevel) { // 确保玩家在线且在当前世界
                currentActivePlayerUUIDs.add(player.getUUID());
                ChunkPos currentChunk = new ChunkPos(player.blockPosition());
                int currentSimulationDistance = player.server.getPlayerList().getSimulationDistance();

                ChunkPos lastChunk = playerLastKnownChunk.get(player.getUUID());
                Integer lastSimulationDistance = playerLastKnownSimulationDistance.get(player.getUUID());

                // 检查玩家模拟距离是否变化
                if (lastSimulationDistance == null || currentSimulationDistance != lastSimulationDistance) {
                    // 如果模拟距离变化，清理相关缓存并触发一次全量BFS
                    lootAreaCalculator.clearCache(currentSimulationDistance);
                    performFullBFSForPlayerArea(currentChunk, currentSimulationDistance, gameTime);
                    playerLastKnownSimulationDistance.put(player.getUUID(), currentSimulationDistance); // 更新模拟距离
                    playerLastKnownChunk.put(player.getUUID(), currentChunk); // 更新位置
                    continue; // 模拟距离变化已处理，跳过后续的增量逻辑
                }

                // 检查玩家是否移动了区块
                if (!currentChunk.equals(lastChunk) && lastChunk != null) {
                    // 计算玩家移动的相对偏移量
                    ChunkPos relativeMove = new ChunkPos(currentChunk.x - lastChunk.x, currentChunk.z - lastChunk.z);

                    // 如果玩家移动在8区块范围内 (x,z 都在 -1,0,1 之间，且不为 (0,0) )
                    if (Math.abs(relativeMove.x) <= 1 && Math.abs(relativeMove.z) <= 1 && (relativeMove.x != 0 || relativeMove.z != 0)) {
                        // 使用 LootAreaCalculator 获取需要新增的区块偏移量
                        Set<ChunkPos> incrementalOffsets = lootAreaCalculator.getIncrementalOffsets(currentSimulationDistance, new ChunkPos(0,0), relativeMove);

                        for (ChunkPos offset : incrementalOffsets) {
                            ChunkPos targetChunk = new ChunkPos(currentChunk.x + offset.x, currentChunk.z + offset.z);
                            // 只有当区块从未被处理过，或者被处理时模拟距离小于当前模拟距离，才需要加入队列
                            if (!processedChunksTracker.contains(targetChunk) ||
                                    (processedChunkInfoMap.get(targetChunk) != null && currentSimulationDistance > processedChunkInfoMap.get(targetChunk).getSimulationDistanceAtProcessing())) {
                                addChunkToProcessingQueue(targetChunk, currentSimulationDistance, gameTime);
                            }
                        }

                    } else {
                        // 玩家移动超出了8区块范围，视为一次“大范围移动”，触发一次全量BFS
                        performFullBFSForPlayerArea(currentChunk, currentSimulationDistance, gameTime);
                    }
                    playerLastKnownChunk.put(player.getUUID(), currentChunk); // 更新玩家最后已知位置
                }
            }
        }

        // 移除已下线或已淘汰玩家的旧记录
        playerLastKnownChunk.keySet().retainAll(currentActivePlayerUUIDs);
        playerLastKnownSimulationDistance.keySet().retainAll(currentActivePlayerUUIDs);
    }

    /**
     * 将指定区块添加到待处理队列和已处理追踪器。
     * @param chunkPos 要添加的区块位置
     * @param simulationDistance 玩家当前模拟距离，用于记录区块处理时的状态
     * @param gameTime 当前游戏时间
     */
    private void addChunkToProcessingQueue(ChunkPos chunkPos, int simulationDistance, long gameTime) {
        // 尝试将区块添加到 processedChunksTracker。如果成功，说明是新加入的。
        if (processedChunksTracker.add(chunkPos)) {
            chunksToProcessQueue.offer(chunkPos);
            processedChunkInfoMap.put(chunkPos, new ProcessedChunkInfo(chunkPos, gameTime, simulationDistance));
        } else {
            // 如果已存在，检查是否需要更新信息并重新入队（例如模拟距离增加）
            ProcessedChunkInfo existingInfo = processedChunkInfoMap.get(chunkPos);
            if (existingInfo != null && simulationDistance > existingInfo.getSimulationDistanceAtProcessing()) {
                // 模拟距离增加了，即使已经处理过，也可能需要重新刷新战利品
                processedChunkInfoMap.put(chunkPos, new ProcessedChunkInfo(chunkPos, gameTime, simulationDistance)); // 更新信息
                chunksToProcessQueue.offer(chunkPos); // 再次入队，让 LootGenerator 重新检查
            } else if (existingInfo != null) {
                // 只是更新访问时间，以便 LRU 淘汰机制更好地工作
                existingInfo.setProcessingTime(gameTime);
            }
        }
    }

    /**
     * 从待处理队列中取出区块并刷新其战利品。
     * @param serverLevel 当前的服务器世界
     */
    private void processChunksFromQueue(ServerLevel serverLevel) {
        int processedThisTick = 0;
        while (!chunksToProcessQueue.isEmpty() && processedThisTick < maxChunksPerTick) {
            ChunkPos chunkPos = chunksToProcessQueue.poll();
            if (chunkPos != null) {
                // 调用 LootGenerator 刷新区块内的战利品
                int refreshedCount = LootGenerator.refreshLootInChunk(serverLevel, chunkPos, GameManager.get().getGameId());
                processedThisTick++;
            }
        }
    }

    /**
     * 清理过期或超出容量限制的已处理区块记录。
     * @param currentGameTime 当前游戏时间
     */
    private void cleanUpProcessedChunks(long currentGameTime) {
        int initialTrackerSize = processedChunksTracker.size();
        int targetCapacity = calculateMaxProcessedChunksCapacity(); // 获取目标容量

        // 1. 基于时间过期的清理
        Iterator<Map.Entry<ChunkPos, ProcessedChunkInfo>> timeIterator = processedChunkInfoMap.entrySet().iterator();
        while (timeIterator.hasNext()) {
            Map.Entry<ChunkPos, ProcessedChunkInfo> entry = timeIterator.next();
            ChunkPos chunkPos = entry.getKey();
            ProcessedChunkInfo info = entry.getValue();

            long ticksToSeconds = (currentGameTime - info.getProcessingTime()) / 20; // 假设 20 ticks/second
            if (ticksToSeconds > processedChunkRetentionTimeSeconds) {
                timeIterator.remove();
                processedChunksTracker.remove(chunkPos);
            }
        }

        // 2. 基于容量限制的清理（LRU 简单实现：移除最旧的）
        while(processedChunksTracker.size() > targetCapacity) {
            ChunkPos oldestChunk = null;
            long oldestTime = Long.MAX_VALUE;

            // 遍历找到最老的区块
            for(Map.Entry<ChunkPos, ProcessedChunkInfo> entry : processedChunkInfoMap.entrySet()) {
                if(entry.getValue().getProcessingTime() < oldestTime) {
                    oldestTime = entry.getValue().getProcessingTime();
                    oldestChunk = entry.getKey();
                }
            }

            if(oldestChunk != null) {
                processedChunksTracker.remove(oldestChunk);
                processedChunkInfoMap.remove(oldestChunk);
            } else {
                // 理论上不应发生：processedChunksTracker.size() > targetCapacity 但 processedChunkInfoMap 为空
                break;
            }
        }
        if (processedChunksTracker.size() < initialTrackerSize) {
            BattleRoyale.LOGGER.info("Cleaned up processed chunks. Removed {} chunks. Current processed chunk count: {}. Target capacity: {}", initialTrackerSize - processedChunksTracker.size(), processedChunksTracker.size(), targetCapacity);
        } else {
            BattleRoyale.LOGGER.debug("No processed chunks needed cleaning this tick. Current processed chunk count: {}. Target capacity: {}", processedChunksTracker.size(), targetCapacity);
        }
    }

    /**
     * 计算已处理区块的最大容量，基于所有在线玩家的模拟距离。
     * @return 已处理区块的最大容量。
     */
    private int calculateMaxProcessedChunksCapacity() {
        int totalSimulationAreaChunks = 0;
        // 获取所有未淘汰的玩家
        for (GamePlayer gamePlayer : GameManager.get().getStandingGamePlayers()) {
            ServerPlayer player = GameManager.get().getServerLevel().getServer().getPlayerList().getPlayer(gamePlayer.getPlayerUUID());
            if (player != null) { // 确保玩家在线且可以获取模拟距离
                int simDist = player.server.getPlayerList().getSimulationDistance();
                // 一个模拟距离为 N 的区域，区块数量大致为 (2*N + 1)^2
                totalSimulationAreaChunks += (2 * simDist + 1) * (2 * simDist + 1);
            }
        }
        // 如果没有在线玩家，提供一个基础容量，避免为零
        if (totalSimulationAreaChunks == 0) {
            return 100 * maxProcessedChunksCapacityFactor; // 假设一个默认的模拟距离对应的基础区块容量
        }
        return totalSimulationAreaChunks * maxProcessedChunksCapacityFactor;
    }

    /**
     * 计算当前所有在线玩家模拟距离范围内需要 BFS 遍历的区块总数（包括已处理和未处理的）。
     * 用于动态调整全量 BFS 的间隔。
     * @return 需要 BFS 遍历的区块总数。
     */
    private int calculateTotalBFSChunks() {
        Set<ChunkPos> allChunksToVisit = new HashSet<>();
        // 获取所有未淘汰的玩家
        for (GamePlayer gamePlayer : GameManager.get().getStandingGamePlayers()) {
            ServerPlayer player = GameManager.get().getServerLevel().getServer().getPlayerList().getPlayer(gamePlayer.getPlayerUUID());
            if (player != null && player.level() == GameManager.get().getServerLevel()) { // 确保玩家在线且在当前世界
                ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                int simulationDistance = player.server.getPlayerList().getSimulationDistance();
                // 使用 LootAreaCalculator 获取某个玩家模拟距离内的所有区块偏移量，并转换为实际区块位置
                Set<ChunkPos> playerAreaOffsets = lootAreaCalculator.calculateAreaOffsets(new ChunkPos(0,0), simulationDistance);
                for (ChunkPos offset : playerAreaOffsets) {
                    allChunksToVisit.add(new ChunkPos(playerChunk.x + offset.x, playerChunk.z + offset.z));
                }
            }
        }
        return allChunksToVisit.size();
    }

    /**
     * 内部类，用于存储已处理区块的信息。
     */
    private static class ProcessedChunkInfo {
        private final ChunkPos chunkPos;
        private long processingTime; // 区块被处理或更新的时间
        private final int simulationDistanceAtProcessing; // 区块被处理时的模拟距离

        public ProcessedChunkInfo(ChunkPos chunkPos, long processingTime, int simulationDistanceAtProcessing) {
            this.chunkPos = chunkPos;
            this.processingTime = processingTime;
            this.simulationDistanceAtProcessing = simulationDistanceAtProcessing;
        }

        public ChunkPos getChunkPos() {
            return chunkPos;
        }

        public long getProcessingTime() {
            return processingTime;
        }

        public void setProcessingTime(long processingTime) {
            this.processingTime = processingTime;
        }

        public int getSimulationDistanceAtProcessing() {
            return simulationDistanceAtProcessing;
        }
    }
}