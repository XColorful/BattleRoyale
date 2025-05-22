package xiao.battleroyale.event;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID)
public class LootTickEvent {

    private static final int MAX_CHUNKS_PER_TICK = 5; // 可配置：每tick最大处理的区块数

    private static Queue<ChunkPos> chunksToProcess = new ArrayDeque<>();
    private static Set<ChunkPos> processedChunkTracker = new HashSet<>(); // 用于去重和检查是否已在队列中
    private static UUID currentGenerationGameId = null;
    private static ServerLevel currentGenerationLevel = null;
    private static int totalLootRefreshedInBatch = 0;
    private static CommandSourceStack initiatingCommandSource = null; // 存储发起指令的CommandSourceStack

    /**
     * 由 LootCommand 调用，开始战利品刷新任务。
     * @param source 发起指令的命令源
     * @param gameId 当前游戏的唯一ID
     * @return 队列中的总区块数；如果已有任务正在进行，则返回 0
     */
    public static int startLootGeneration(CommandSourceStack source, UUID gameId) {
        if (!chunksToProcess.isEmpty()) {
            return 0;
        }

        initiatingCommandSource = source; // 存储命令源
        currentGenerationGameId = gameId;
        currentGenerationLevel = source.getLevel();
        totalLootRefreshedInBatch = 0;
        chunksToProcess.clear();
        processedChunkTracker.clear();

        MinecraftServer server = currentGenerationLevel.getServer();
        int simulationDistance = server.getPlayerList().getSimulationDistance();

        // 收集所有玩家所在的唯一区块位置
        Set<ChunkPos> uniquePlayerChunkPositions = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.level() == currentGenerationLevel) {
                uniquePlayerChunkPositions.add(new ChunkPos(player.blockPosition()));
            }
        }

        // 初始化 BFS 队列和最终待处理队列
        // BFS 队列用于控制扩散层次，chunksToProcess 是最终按优先级排序的队列
        Queue<ChunkPos> bfsQueue = new ArrayDeque<>(uniquePlayerChunkPositions);
        processedChunkTracker.addAll(uniquePlayerChunkPositions); // 将玩家所在区块加入已追踪集合
        chunksToProcess.addAll(uniquePlayerChunkPositions); // 优先将玩家所在区块添加到最终处理队列

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
                    if (processedChunkTracker.add(neighbor)) {
                        bfsQueue.add(neighbor);
                        chunksToProcess.add(neighbor);
                    }
                }
            }
        }

        BattleRoyale.LOGGER.info("Started loot generation for {} chunks.", chunksToProcess.size());
        return chunksToProcess.size();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && currentGenerationLevel != null && currentGenerationGameId != null) {
            if (chunksToProcess.isEmpty()) {
                if (initiatingCommandSource != null) { // 所有区块处理完毕
                    initiatingCommandSource.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_finished", totalLootRefreshedInBatch), true);
                    BattleRoyale.LOGGER.info("Loot generation batch finished. Total refreshed: {}", totalLootRefreshedInBatch);
                    initiatingCommandSource = null; // 清空，防止重复发送
                }
                currentGenerationGameId = null;
                currentGenerationLevel = null;
                totalLootRefreshedInBatch = 0;
                return;
            }

            int processedThisTick = 0;
            while (!chunksToProcess.isEmpty() && processedThisTick < MAX_CHUNKS_PER_TICK) {
                ChunkPos chunkPos = chunksToProcess.poll();
                totalLootRefreshedInBatch += LootGenerator.refreshLootInChunk(currentGenerationLevel, chunkPos, currentGenerationGameId);
                processedThisTick++;
            }
        }
    }
}