package xiao.battleroyale.common.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.algorithm.BfsCalculator;
import xiao.battleroyale.algorithm.BfsCalculator.Offset2D;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.common.loot.ILootConfigManager;
import xiao.battleroyale.api.event.IServerTickEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.loot.ICommonLootManager;

import java.util.*;

public class CommonLootManager implements ICommonLootManager {

    private static class CommonLootManagerHolder {
        private static final CommonLootManager INSTANCE = new CommonLootManager();
    }

    public static CommonLootManager get() {
        return CommonLootManagerHolder.INSTANCE;
    }

    protected CommonLootManager() {}

    public static void init(McSide mcSide) {
    }

    private static int MAX_CHUNKS_PER_TICK = 5;
    public static void setMaxChunksPerTick(int chunks) { MAX_CHUNKS_PER_TICK = Math.min(Math.max(chunks, 5), 100000); } // 十万

    private final Queue<ChunkPos> chunksToProcess = new ArrayDeque<>();
    private final Set<ChunkPos> processedChunkTracker = new HashSet<>(); // 用于去重和检查是否已在队列中
    private UUID currentGenerationGameId = null;
    private ServerLevel currentGenerationLevel = null;
    private int totalLootRefreshedInBatch = 0;
    private CommandSourceStack initiatingCommandSource = null;

    public int getMaxLootChunkPerTick() { return MAX_CHUNKS_PER_TICK; }
    public int chunksToProcessSize() { return chunksToProcess.size(); }
    public int processedChunkTrackerSize() { return processedChunkTracker.size(); }
    public @Nullable UUID getCurrentGenerationGameId() { return currentGenerationGameId; }
    public @Nullable ServerLevel getCurrentGenerationLevel() { return currentGenerationLevel; }
    public int totalLootRefreshedInBatch() { return totalLootRefreshedInBatch; }

    @Override
    public LootStatus lootStatusCheck() {
        if (BattleRoyale.getGameManager().isInGame()) {
            return LootStatus.REJECT;
        } else if (this.currentGenerationGameId != null || !this.chunksToProcess.isEmpty()) {
            return LootStatus.PROCESSING;
        } else {
            return LootStatus.AVAILABLE;
        }
    }
    @Override
    public LootStatus lootStatusCheck(CommandSourceStack source) {
        LootStatus status = this.lootStatusCheck();
        if (source != null) {
            switch(status) {
                case PROCESSING -> source.sendFailure(Component.translatable("battleroyale.message.loot_generation_in_progress"));
                case REJECT -> source.sendFailure(Component.translatable("battleroyale.message.game_cancel_loot"));
            }
        }
        return status;
    }

    @Override
    public LootStatus lootPos(@Nullable CommandSourceStack source, ServerLevel serverLevel, Vec3 pos) {
        LootStatus status = source != null ? lootStatusCheck(source) : lootStatusCheck();
        switch (status) {
            case AVAILABLE -> {
                BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
                BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
                if (blockEntity == null) {
                    return LootStatus.UNAVAILABLE;
                }
                return LootGenerator.refreshLootObject(new LootGenerator.LootContext(serverLevel, ChunkPos.containing(blockPos), UUID.randomUUID()), blockEntity)
                        ? LootStatus.AVAILABLE : LootStatus.UNAVAILABLE;
            }
            case PROCESSING, REJECT -> {
                return status;
            }
            default -> {
                return status;
            }
        }
    }

    @Override
    public int lootChunk(@Nullable CommandSourceStack source, ServerLevel serverLevel, Vec3 pos) {
        LootStatus status = source != null ? lootStatusCheck(source) : lootStatusCheck();
        return switch (status) {
            case AVAILABLE -> LootGenerator.refreshLootInChunk(new LootGenerator.LootContext(serverLevel, pos, UUID.randomUUID()));
            case PROCESSING -> -2; // 避免跟0冲突
            case REJECT -> -1;
            default -> -2;
        };
    }

    // 延迟刷新，保留source以通知
    @Override
    public int lootGeneration(CommandSourceStack source, ServerLevel serverLevel) {
        return switch (lootStatusCheck(source)) {
            case AVAILABLE -> lootGeneration(source, serverLevel, UUID.randomUUID());
            case PROCESSING -> 0;
            case REJECT -> -1;
            default -> -2;
        };
    }

    /**
     * 初始化并开始战利品刷新任务。
     * @param source 发起指令的命令源
     * @param gameId 物资刷新的游戏ID
     * @return 队列中的总区块数
     */
    private int lootGeneration(CommandSourceStack source, ServerLevel serverLevel, UUID gameId) {
        resetLootInfo();
        this.initiatingCommandSource = source;
        this.currentGenerationGameId = gameId;
        this.currentGenerationLevel = serverLevel;

        MinecraftServer server = this.currentGenerationLevel.getServer();
        int simulationDistance = server.getPlayerList().getSimulationDistance();

        // 获取所有中心区块
        Set<ChunkPos> centers = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != null && player.level() == this.currentGenerationLevel) {
                centers.add(ChunkPos.containing(player.blockPosition()));
            }
        }

        List<List<Offset2D>> offsets = new ArrayList<>(BfsCalculator.calculateCenterOffset(simulationDistance));

        for (List<Offset2D> layer : offsets) { // 从距离0的区块开始，确保距离d的区块一定比d+1先入队
            for (ChunkPos center : centers) { // 玩家中心
                for (Offset2D offset : layer) { // 遍历当前距离
                    ChunkPos target = new ChunkPos((center.x()) + offset.x(), center.z() + offset.z());
                    if (this.processedChunkTracker.add(target)) {
                        this.chunksToProcess.add(target);
                    }
                }
            }
        }

        LootGenerationEventHandler.register();

        BattleRoyale.LOGGER.info("Loot generation task initialized for {} chunks, loot gameId: {}", this.chunksToProcess.size(), gameId);
        return this.chunksToProcess.size();
    }

    /**
     * 处理每个服务器 Tick 的战利品生成逻辑。
     * 由 LootGenerationEventHandler 调用。
     * @param event TickEvent.ServerTickEvent
     * @return 如果任务完成或中断，返回 true
     */
    @Override
    public boolean onLootTick(IServerTickEvent event) {
        if (currentGenerationLevel == null) {
            return true;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        boolean isInGame = gameManager.isInGame();
        if (isInGame || chunksToProcess.isEmpty()) { // 游戏中(强制中断)或已结束
            if (isInGame && initiatingCommandSource != null) {
                initiatingCommandSource.sendFailure(Component.translatable("battleroyale.message.game_stop_loot"));
            }
            sendLootRefreshResult();
            resetLootInfo();
            return true;
        }

        // 处理本 Tick 的区块
        int processedChunkThisTick = 0;
        synchronized (lock) {
            ILootConfigManager<?> lootConfigManager = LootGenerator.getILootConfigManager();
            while (!chunksToProcess.isEmpty() && processedChunkThisTick < MAX_CHUNKS_PER_TICK) {
                ChunkPos chunkPos = chunksToProcess.poll();
                int newlyProcessedLoot = LootGenerator.refreshLootInChunk(lootConfigManager, new LootGenerator.LootContext(currentGenerationLevel, chunkPos, currentGenerationGameId));
                if (newlyProcessedLoot != LootGenerator.CHUNK_NOT_LOADED) {
                    totalLootRefreshedInBatch += newlyProcessedLoot;
                    processedChunkThisTick++;
                }
            }
        }
        return false;
    }
    private static final Object lock = new Object();

    @Override
    public boolean stopLootGeneration(CommandSourceStack source) {
        try {
            synchronized (lock) {
                sendLootRefreshResult();
                resetLootInfo();
                BattleRoyale.LOGGER.debug("Stopped loot generation task");
                return true;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to stop loot generation task.", e);
            return false;
        }
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