package xiao.battleroyale.common.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.event.loot.LootGenerationEventHandler;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class CommonLootManager {

    private static class CommonLootManagerHolder {
        private static final CommonLootManager INSTANCE = new CommonLootManager();
    }

    public static CommonLootManager get() {
        return CommonLootManagerHolder.INSTANCE;
    }

    private CommonLootManager() {}

    private static int MAX_CHUNKS_PER_TICK = 5;
    public static void setMaxChunksPerTick(int chunks) { MAX_CHUNKS_PER_TICK = Math.min(Math.max(chunks, 5), 100000); } // 十万

    private final Queue<ChunkPos> chunksToProcess = new ArrayDeque<>();
    private final Set<ChunkPos> processedChunkTracker = new HashSet<>(); // 用于去重和检查是否已在队列中
    private UUID currentGenerationGameId = null;
    private ServerLevel currentGenerationLevel = null;
    private int totalLootRefreshedInBatch = 0;
    private CommandSourceStack initiatingCommandSource = null;

    public static int getMaxChunksPerTick() { return MAX_CHUNKS_PER_TICK; }
    public int chunksToProcessSize() { return chunksToProcess.size(); }
    public int processedChunkTrackerSize() { return processedChunkTracker.size(); }
    public @Nullable UUID getCurrentGenerationGameId() { return currentGenerationGameId; }
    public @Nullable ServerLevel getCurrentGenerationLevel() { return currentGenerationLevel; }
    public int totalLootRefreshedInBatch() { return totalLootRefreshedInBatch; }

    /**
     * 由 LootCommand 调用，初始化并开始战利品刷新任务。
     * @param source 发起指令的命令源
     * @param gameId 当前游戏的唯一ID
     * @return 队列中的总区块数；如果已有任务正在进行，则返回 0；如果游戏已在进行，则返回 -1。
     */
    public int startGenerationTask(CommandSourceStack source, UUID gameId) {
        if (GameManager.get().isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_cancel_loot"));
            return -1;
        }

        if (this.currentGenerationGameId != null || !this.chunksToProcess.isEmpty()) {
            return 0;
        }

        resetLootInfo();
        this.initiatingCommandSource = source;
        this.currentGenerationGameId = gameId;
        this.currentGenerationLevel = source.getLevel();

        MinecraftServer server = this.currentGenerationLevel.getServer();
        int simulationDistance = server.getPlayerList().getSimulationDistance();

        Set<ChunkPos> uniquePlayerChunkPositions = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != null && player.level() == this.currentGenerationLevel) {
                uniquePlayerChunkPositions.add(new ChunkPos(player.blockPosition()));
            }
        }

        // 初始化 BFS 队列和最终待处理队列
        // BFS 队列用于控制扩散层次，chunksToProcess 是最终按优先级排序的队列
        Queue<ChunkPos> bfsQueue = new ArrayDeque<>(uniquePlayerChunkPositions);
        this.processedChunkTracker.addAll(uniquePlayerChunkPositions); // 将玩家所在区块加入已追踪集合
        this.chunksToProcess.addAll(uniquePlayerChunkPositions); // 优先将玩家所在区块添加到最终处理队列

        // BFS 遍历，限制层数，确保在 simulationDistance 范围内
        for (int i = 0; i < simulationDistance; i++) {
            int currentLevelSize = bfsQueue.size();
            if (currentLevelSize == 0) break;

            for (int j = 0; j < currentLevelSize; j++) {
                ChunkPos currentChunk = bfsQueue.poll();
                ChunkPos[] neighbors = new ChunkPos[]{
                        new ChunkPos(currentChunk.x + 1, currentChunk.z),
                        new ChunkPos(currentChunk.x - 1, currentChunk.z),
                        new ChunkPos(currentChunk.x, currentChunk.z + 1),
                        new ChunkPos(currentChunk.x, currentChunk.z - 1)
                };

                for (ChunkPos neighbor : neighbors) {
                    if (this.processedChunkTracker.add(neighbor)) {
                        bfsQueue.add(neighbor);
                        this.chunksToProcess.add(neighbor);
                    }
                }
            }
        }

        LootGenerationEventHandler.register();

        BattleRoyale.LOGGER.info("Loot generation task initialized for {} chunks.", this.chunksToProcess.size());
        return this.chunksToProcess.size();
    }

    /**
     * 处理每个服务器 Tick 的战利品生成逻辑。
     * 由 LootGenerationEventHandler 调用。
     * @param event TickEvent.ServerTickEvent
     * @return 如果任务完成或中断，返回 true；否则返回 false。
     */
    public boolean onTick(TickEvent.ServerTickEvent event) {
        if (currentGenerationLevel == null || GameManager.get().isInGame() && !this.currentGenerationGameId.equals(GameManager.get().getGameId())) {
            if (initiatingCommandSource != null) {
                initiatingCommandSource.sendFailure(Component.translatable("battleroyale.message.game_stop_loot"));
            }
            sendLootRefreshResult();
            resetLootInfo();
            return true;
        }

        if (chunksToProcess.isEmpty()) {
            if (initiatingCommandSource != null) {
                sendLootRefreshResult();
            }
            resetLootInfo();
            return true;
        }

        // 处理本 Tick 的区块
        int processedChunkThisTick = 0;
        while (!chunksToProcess.isEmpty() && processedChunkThisTick < MAX_CHUNKS_PER_TICK) {
            ChunkPos chunkPos = chunksToProcess.poll();
            int newlyProcessedLoot = LootGenerator.refreshLootInChunk(new LootGenerator.LootContext(currentGenerationLevel, chunkPos, currentGenerationGameId));
            if (newlyProcessedLoot != LootGenerator.CHUNK_NOT_LOADED) {
                totalLootRefreshedInBatch += newlyProcessedLoot;
                processedChunkThisTick++;
            }
        }
        return false;
    }

    private void resetLootInfo() {
        initiatingCommandSource = null;
        currentGenerationGameId = null;
        currentGenerationLevel = null;
        totalLootRefreshedInBatch = 0;
        chunksToProcess.clear();
        processedChunkTracker.clear();
    }

    private void sendLootRefreshResult() {
        if (currentGenerationLevel == null) {
            BattleRoyale.LOGGER.warn("CommonLootManager.currentGenerationLevel == null");
        }
        if (initiatingCommandSource != null) {
            initiatingCommandSource.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_finished", totalLootRefreshedInBatch), true);
        }
        BattleRoyale.LOGGER.info("Loot generation batch finished. Total refreshed: {}", totalLootRefreshedInBatch);
    }
}