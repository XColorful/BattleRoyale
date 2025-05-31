package xiao.battleroyale.common.game.loot;

import net.minecraft.world.level.ChunkPos;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayDeque;

public class LootAreaCalculator {

    // 缓存：模拟距离 -> (玩家移动相对偏移量 -> 需要新增的区块偏移量集合)
    private final Map<Integer, Map<ChunkPos, Set<ChunkPos>>> cachedIncrementalOffsets;

    public LootAreaCalculator() {
        this.cachedIncrementalOffsets = new HashMap<>();
    }

    /**
     * 获取玩家从旧区块移动到新区块时，需要额外处理的区块偏移量集合。
     * 如果缓存中不存在，会进行计算并缓存。
     *
     * @param simulationDistance 当前模拟距离
     * @param oldPlayerRelativeChunk 旧的玩家相对区块位置 (通常为 ChunkPos(0,0))
     * @param newPlayerRelativeChunk 新的玩家相对区块位置 (例如 ChunkPos(1,0) 代表玩家向X正方向移动一格)
     * @return 需要额外处理的区块相对于新玩家位置的偏移量集合
     */
    public Set<ChunkPos> getIncrementalOffsets(int simulationDistance, ChunkPos oldPlayerRelativeChunk, ChunkPos newPlayerRelativeChunk) {
        Map<ChunkPos, Set<ChunkPos>> distanceMap = cachedIncrementalOffsets.computeIfAbsent(simulationDistance, k -> new HashMap<>());

        Set<ChunkPos> offsets = distanceMap.get(newPlayerRelativeChunk);
        if (offsets == null) {
            offsets = calculateAndCacheIncrementalOffsets(simulationDistance, oldPlayerRelativeChunk, newPlayerRelativeChunk, distanceMap);
        }
        return offsets;
    }

    /**
     * 清理指定模拟距离或所有模拟距离的缓存。
     * @param simulationDistance 如果为 null，则清理所有缓存；否则清理指定模拟距离的缓存。
     */
    public void clearCache(Integer simulationDistance) {
        if (simulationDistance == null) {
            cachedIncrementalOffsets.clear();
        } else {
            cachedIncrementalOffsets.remove(simulationDistance);
        }
    }

    /**
     * 实际计算并缓存增量偏移量。
     * @param simulationDistance 当前模拟距离
     * @param oldPlayerRelativeChunk 旧的玩家相对区块位置 (例如 ChunkPos(0,0))
     * @param newPlayerRelativeChunk 新的玩家相对区块位置 (例如 ChunkPos(1,0))
     * @param distanceMap 当前模拟距离下的缓存 Map
     * @return 需要额外处理的区块相对于新玩家位置的偏移量集合
     */
    private Set<ChunkPos> calculateAndCacheIncrementalOffsets(int simulationDistance, ChunkPos oldPlayerRelativeChunk, ChunkPos newPlayerRelativeChunk, Map<ChunkPos, Set<ChunkPos>> distanceMap) {
        // 计算旧玩家位置为中心时的所有区块（相对坐标）
        Set<ChunkPos> oldAreaOffsets = calculateAreaOffsets(oldPlayerRelativeChunk, simulationDistance);
        // 计算新玩家位置为中心时的所有区块（相对坐标）
        Set<ChunkPos> newAreaOffsets = calculateAreaOffsets(newPlayerRelativeChunk, simulationDistance);

        // 找出 newAreaOffsets 中相对于 oldAreaOffsets 新增的区块
        Set<ChunkPos> incrementalOffsets = new HashSet<>(newAreaOffsets);
        incrementalOffsets.removeAll(oldAreaOffsets); // 移除旧区域中已有的

        // 将结果添加到缓存中，使用不可修改的 Set
        distanceMap.put(newPlayerRelativeChunk, Collections.unmodifiableSet(incrementalOffsets));
        return incrementalOffsets;
    }

    /**
     * 计算以指定中心点为起点，在给定模拟距离下的所有区块偏移量。
     * 此方法执行 BFS 扩散，只计算相对偏移量。
     * @param center 相对中心点 (例如 ChunkPos(0,0))
     * @param simulationDistance 模拟距离
     * @return 相对于中心点的所有区块偏移量集合
     */
    public Set<ChunkPos> calculateAreaOffsets(ChunkPos center, int simulationDistance) {
        Set<ChunkPos> areaOffsets = new HashSet<>();
        Queue<ChunkPos> bfsQueue = new ArrayDeque<>();

        bfsQueue.offer(center);
        areaOffsets.add(center);

        for (int i = 0; i < simulationDistance; i++) {
            int currentLevelSize = bfsQueue.size();
            if (currentLevelSize == 0) break; // 当前层级已无区块可遍历

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
                    if (areaOffsets.add(neighbor)) { // 如果是新加入的区块，则添加到集合并入队
                        bfsQueue.offer(neighbor);
                    }
                }
            }
        }
        return areaOffsets;
    }
}