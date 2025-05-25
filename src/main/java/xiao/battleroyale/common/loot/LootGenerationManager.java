package xiao.battleroyale.common.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.event.LootGenerationEventHandler; // 引入事件处理器

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class LootGenerationManager {

    private static LootGenerationManager instance;

    private final int MAX_CHUNKS_PER_TICK = 5; // 可配置：每tick最大处理的区块数

    private final Queue<ChunkPos> chunksToProcess = new ArrayDeque<>();
    private final Set<ChunkPos> processedChunkTracker = new HashSet<>(); // 用于去重和检查是否已在队列中
    private UUID currentGenerationGameId = null;
    private ServerLevel currentGenerationLevel = null;
    private int totalLootRefreshedInBatch = 0;
    private CommandSourceStack initiatingCommandSource = null; // 存储发起指令的CommandSourceStack

    // 私有构造函数，确保只能通过 get() 获取单例
    private LootGenerationManager() {
    }

    /**
     * 获取 LootGenerationManager 的单例实例。
     * @return LootGenerationManager 的单例实例
     */
    public static LootGenerationManager get() {
        if (instance == null) {
            instance = new LootGenerationManager();
        }
        return instance;
    }

    /**
     * 由 LootCommand 调用，初始化并开始战利品刷新任务。
     * @param source 发起指令的命令源
     * @param gameId 当前游戏的唯一ID
     * @return 队列中的总区块数；如果已有任务正在进行，则返回 0；如果游戏已在进行，则返回 -1。
     */
    public int startGenerationTask(CommandSourceStack source, UUID gameId) {
        // 如果游戏正在进行，禁止启动战利品刷新
        if (GameManager.get().isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_cancel_loot"));
            return -1;
        }

        // 如果已有战利品刷新任务正在进行，则返回 0
        if (this.currentGenerationGameId != null || !this.chunksToProcess.isEmpty()) {
            return 0;
        }

        // 重置所有任务相关的实例信息，确保从头开始
        resetLootInfo();
        this.initiatingCommandSource = source;
        this.currentGenerationGameId = gameId;
        this.currentGenerationLevel = source.getLevel();

        MinecraftServer server = this.currentGenerationLevel.getServer();
        int simulationDistance = server.getPlayerList().getSimulationDistance();

        // 收集所有玩家所在的唯一区块位置
        Set<ChunkPos> uniquePlayerChunkPositions = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // 只收集当前游戏维度内的玩家
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
            int currentLevelSize = bfsQueue.size(); // 获取当前层级的区块数量
            if (currentLevelSize == 0) break; // 当前层级没有区块了，停止扩散

            for (int j = 0; j < currentLevelSize; j++) {
                ChunkPos currentChunk = bfsQueue.poll();
                ChunkPos[] neighbors = new ChunkPos[]{
                        new ChunkPos(currentChunk.x + 1, currentChunk.z),
                        new ChunkPos(currentChunk.x - 1, currentChunk.z),
                        new ChunkPos(currentChunk.x, currentChunk.z + 1),
                        new ChunkPos(currentChunk.x, currentChunk.z - 1)
                };

                for (ChunkPos neighbor : neighbors) {
                    if (this.processedChunkTracker.add(neighbor)) { // 如果是新区块（未追踪过）
                        bfsQueue.add(neighbor); // 加入 BFS 队列进行下一层扩散
                        this.chunksToProcess.add(neighbor); // 加入最终处理队列
                    }
                }
            }
        }

        // 注册事件监听器，让其开始接收 Tick 事件
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
        // 正在进行游戏则取消刷新，并检查是否是同一个游戏实例
        // 如果 GameManager 报告游戏正在进行，并且当前战利品生成任务的游戏ID与 GameManager 中的游戏ID不一致
        // 这意味着一个新游戏开始了，应该中断旧的战利品生成任务。
        if (GameManager.get().isInGame() && !this.currentGenerationGameId.equals(GameManager.get().getGameId())) {
            if (initiatingCommandSource != null) {
                initiatingCommandSource.sendFailure(Component.translatable("battleroyale.message.game_stop_loot"));
            }
            sendLootRefreshResult();
            resetLootInfo(); // 清理内部状态
            return true; // 任务中断，通知处理器取消注册
        }

        // 如果队列为空，表示所有区块已处理完毕
        if (chunksToProcess.isEmpty()) {
            if (initiatingCommandSource != null) {
                sendLootRefreshResult();
            }
            resetLootInfo(); // 清理内部状态
            return true; // 任务完成，通知处理器取消注册
        }

        // 处理本 Tick 的区块
        int processedChunkThisTick = 0;
        while (!chunksToProcess.isEmpty() && processedChunkThisTick < MAX_CHUNKS_PER_TICK) {
            ChunkPos chunkPos = chunksToProcess.poll(); // 取出下一个待处理区块
            // 调用 LootGenerator 刷新该区块的战利品，并累加刷新数量
            totalLootRefreshedInBatch += LootGenerator.refreshLootInChunk(currentGenerationLevel, chunkPos, currentGenerationGameId);
            processedChunkThisTick++;
        }
        return false; // 任务仍在进行中
    }

    /**
     * 重置所有实例任务信息，为下一次任务做准备。
     */
    private void resetLootInfo() {
        initiatingCommandSource = null;
        currentGenerationGameId = null;
        currentGenerationLevel = null;
        totalLootRefreshedInBatch = 0;
        chunksToProcess.clear();
        processedChunkTracker.clear();
    }

    /**
     * 向发起命令的源发送战利品刷新结果。
     */
    private void sendLootRefreshResult() {
        if (initiatingCommandSource != null) {
            initiatingCommandSource.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_finished", totalLootRefreshedInBatch), true);
        }
        BattleRoyale.LOGGER.info("Loot generation batch finished. Total refreshed: {}", totalLootRefreshedInBatch);
    }
}