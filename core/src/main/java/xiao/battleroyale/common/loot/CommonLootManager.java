package xiao.battleroyale.common.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.algorithm.BfsCalculator;
import xiao.battleroyale.algorithm.BfsCalculator.Offset2D;
import xiao.battleroyale.api.event.IServerTickEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.handler.loot.LootGenerationEventHandler;

import java.util.*;

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
        if (BattleRoyale.getGameManager().isInGame()) {
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

        // 获取所有中心区块
        Set<ChunkPos> centers = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != null && player.level() == this.currentGenerationLevel) {
                centers.add(new ChunkPos(player.blockPosition()));
            }
        }

        List<List<Offset2D>> offsets = new ArrayList<>(BfsCalculator.calculateCenterOffset(simulationDistance));

        for (List<Offset2D> layer : offsets) { // 从距离0的区块开始，确保距离d的区块一定比d+1先入队
            for (ChunkPos center : centers) { // 玩家中心
                for (Offset2D offset : layer) { // 遍历当前距离
                    ChunkPos target = new ChunkPos(center.x + offset.x(), center.z + offset.z());
                    if (this.processedChunkTracker.add(target)) {
                        this.chunksToProcess.add(target);
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
    public boolean onTick(IServerTickEvent event) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (currentGenerationLevel == null || gameManager.isInGame() && !this.currentGenerationGameId.equals(gameManager.getGameId())) {
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