package xiao.battleroyale.event;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge; // 引入 MinecraftForge 用于手动注册/取消注册
// 移除了 @Mod.EventBusSubscriber 注解

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class LootTickEvent {

    private static LootTickEvent instance; // 单例实例

    private int MAX_CHUNKS_PER_TICK = 5; // 可配置：每tick最大处理的区块数

    private final Queue<ChunkPos> chunksToProcess = new ArrayDeque<>();
    private final Set<ChunkPos> processedChunkTracker = new HashSet<>(); // 用于去重和检查是否已在队列中
    private UUID currentGenerationGameId = null;
    private ServerLevel currentGenerationLevel = null;
    private int totalLootRefreshedInBatch = 0;
    private CommandSourceStack initiatingCommandSource = null; // 存储发起指令的CommandSourceStack

    // 私有构造函数，确保只能通过 getInstance() 获取单例
    private LootTickEvent() {
    }

    /**
     * 获取 LootTickEvent 的单例实例。
     * @return LootTickEvent 的单例实例
     */
    public static LootTickEvent getInstance() {
        if (instance == null) {
            instance = new LootTickEvent();
        }
        return instance;
    }

    /**
     * 由 LootCommand 调用，开始战利品刷新任务。
     * @param source 发起指令的命令源
     * @param gameId 当前游戏的唯一ID
     * @return 队列中的总区块数；如果已有任务正在进行，则返回 0；如果游戏已在进行，则返回 -1。
     */
    public static int startLootGeneration(CommandSourceStack source, UUID gameId) {
        // 如果游戏正在进行，禁止启动战利品刷新
        if (GameManager.get().isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_cancel_loot"));
            return -1;
        }
        // 获取单例实例
        LootTickEvent currentInstance = getInstance();
        if (currentInstance.currentGenerationGameId != null || !currentInstance.chunksToProcess.isEmpty()) {
            return 0;
        }

        currentInstance.resetLootInfo();
        currentInstance.initiatingCommandSource = source;
        currentInstance.currentGenerationGameId = gameId;
        currentInstance.currentGenerationLevel = source.getLevel();

        MinecraftServer server = currentInstance.currentGenerationLevel.getServer();
        int simulationDistance = server.getPlayerList().getSimulationDistance();

        // 收集所有玩家所在的唯一区块位置
        Set<ChunkPos> uniquePlayerChunkPositions = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // 只收集当前游戏维度内的玩家
            if (player != null && player.level() == currentInstance.currentGenerationLevel) {
                uniquePlayerChunkPositions.add(new ChunkPos(player.blockPosition()));
            }
        }

        // 初始化 BFS 队列和最终待处理队列
        // BFS 队列用于控制扩散层次，chunksToProcess 是最终按优先级排序的队列
        Queue<ChunkPos> bfsQueue = new ArrayDeque<>(uniquePlayerChunkPositions);
        currentInstance.processedChunkTracker.addAll(uniquePlayerChunkPositions); // 将玩家所在区块加入已追踪集合
        currentInstance.chunksToProcess.addAll(uniquePlayerChunkPositions); // 优先将玩家所在区块添加到最终处理队列

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
                    if (currentInstance.processedChunkTracker.add(neighbor)) {
                        bfsQueue.add(neighbor);
                        currentInstance.chunksToProcess.add(neighbor);
                    }
                }
            }
        }

        // 注册当前单例实例到 Forge 事件总线
        MinecraftForge.EVENT_BUS.register(currentInstance);
        BattleRoyale.LOGGER.info("Started loot generation for {} chunks. LootTickEvent instance registered.", currentInstance.chunksToProcess.size());
        return currentInstance.chunksToProcess.size();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (GameManager.get().isInGame() && !currentGenerationGameId.equals(GameManager.get().getGameId())) {
                if (initiatingCommandSource != null) {
                    initiatingCommandSource.sendFailure(Component.translatable("battleroyale.message.game_stop_loot"));
                }
                sendLootRefreshResult();
                unregisterSelf(); // 取消注册自身
                return;
            }

            if (chunksToProcess.isEmpty()) {
                if (initiatingCommandSource != null) {
                    sendLootRefreshResult();
                }
                unregisterSelf(); // 取消注册自身
                return;
            }

            int processedChunkThisTick = 0;
            while (!chunksToProcess.isEmpty() && processedChunkThisTick < MAX_CHUNKS_PER_TICK) {
                ChunkPos chunkPos = chunksToProcess.poll();
                totalLootRefreshedInBatch += LootGenerator.refreshLootInChunk(currentGenerationLevel, chunkPos, currentGenerationGameId);
                processedChunkThisTick++;
            }
        }
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

    /**
     * 取消注册当前实例的事件监听。
     */
    private void unregisterSelf() {
        MinecraftForge.EVENT_BUS.unregister(this);
        instance = null;
        BattleRoyale.LOGGER.info("LootTickEvent instance unregistered.");
    }
}