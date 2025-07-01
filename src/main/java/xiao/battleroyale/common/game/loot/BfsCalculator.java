package xiao.battleroyale.common.game.loot;

import xiao.battleroyale.common.game.loot.GameLootManager.Offset2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BfsCalculator {

    private static final List<List<Offset2D>> calculatedCenterOffset = new ArrayList<>();

    /**
     * 计算距离中心 [0, distance] 个区块距离的偏移
     * 返回列表中.get(distance)即距离distance区块的偏移列表
     * 例如列表索引0应有{(0,0)}，索引1应有{(1,0),(0,1),(0,-1),(-1,0)}
     */
    public static List<List<Offset2D>> calculateCenterOffset(int distance) {
        if (distance < calculatedCenterOffset.size()) {
            return calculatedCenterOffset.subList(0, distance + 1);
        }

        Set<Offset2D> visited = new HashSet<>();

        for (List<Offset2D> offsets : calculatedCenterOffset) {
            visited.addAll(offsets);
        }

        int startDistance = calculatedCenterOffset.size();
        if (startDistance == 0) {
            // 如果列表为空，从距离0开始
            calculatedCenterOffset.add(new ArrayList<>());
            Offset2D origin = new Offset2D(0, 0);
            calculatedCenterOffset.get(0).add(origin);
            visited.add(origin);
            startDistance = 1;
        }

        // 迭代计算每个距离层级的偏移量
        for (int i = startDistance; i <= distance; i++) {
            List<Offset2D> currentDistanceOffsets = new ArrayList<>();
            // 遍历所有可能的x和z坐标，其曼哈顿距离等于i
            for (int x = -i; x <= i; x++) {
                int z = i - Math.abs(x);
                Offset2D offset1 = new Offset2D(x, z);
                if (visited.add(offset1)) {
                    currentDistanceOffsets.add(offset1);
                }
                // 检查对称的z坐标，避免重复添加
                if (z != 0) {
                    Offset2D offset2 = new Offset2D(x, -z);
                    if (visited.add(offset2)) {
                        currentDistanceOffsets.add(offset2);
                    }
                }
            }
            calculatedCenterOffset.add(currentDistanceOffsets);
        }

        return calculatedCenterOffset.subList(0, distance + 1);
    }
}
