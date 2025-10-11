package xiao.battleroyale.algorithm;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.util.ClassUtils;

import java.util.*;

import static xiao.battleroyale.util.Vec3Utils.EPSILON;

/**
 * 双圆心网格计算器
 * 缓存特定点数 N 时所需的点位列表和最小半径信息
 */
public class CircleGridCalculator {

    private static final Map<Integer, Integer> nToActualN = new HashMap<>(); // N -> 列表.size()
    private static final Map<Integer, CircleGridData> calculatedGridData = new HashMap<>(); // 列表.size() -> {列表,R(N),PossibleN}
    public record CircleGridData(List<Vec3> points, double minRadius, List<Integer> possibleN) {
        public int size() {
            return points.size();
        }
        public boolean isEmpty() {
            return points.isEmpty();
        }
    }

    // 奇数N -> 圆心(0, 0)
    private static final Vec3 CENTER_OFFSET_ODD = Vec3.ZERO;
    // 偶数N -> 圆心(0.5, 0.5)
    private static final Vec3 CENTER_OFFSET_EVEN = new Vec3(0.5, 0, 0.5);

    private static final CircleGridData EMPTY = new CircleGridData(new ArrayList<>(), 0, List.of(0));
    static { // 用不着, 只是防御一下
        nToActualN.put(0, 0);
        calculatedGridData.put(0, EMPTY);
    }

    private static int getTargetN(int n) {
        return Math.max(n, 0);
    }
    public static int getActualN(int n) {
        calculateCircleGrid(n);

        if (!nToActualN.containsKey(n)) {
            return 0;
        }
        return nToActualN.get(n);
    }

    public static void preCalculate(List<Integer> nList) {
        for (int n : nList) {
            getCircleGrid(n);
        }
    }
    public static void preCalculate(int startN, int endN) {
        for (int n = Math.max(1, startN); n <= endN; n++) {
            getCircleGrid(n);
        }
    }
    public static void preCalculate(int n) {
        getCircleGrid(n);
    }

    // 当前圈半径, 略微扩大些保证覆盖边缘点
    private static double calculateCurrentRadius(boolean isEvenSystem, int round) {
        if (!isEvenSystem) { // 奇数, (0,0)圆心
            return round - 1 + EPSILON; // 第一层半径0, 第二层半径1, 第三层半径2
        } else {
            return (round - 0.5) * Math.sqrt(2) + EPSILON; // 第一层半径√2/ 2, 第二层半径3√2/ 2
        }
    }
    private static final int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static void calculateCircleGrid(int n) {
        int targetN = getTargetN(n);
        if (calculatedGridData.containsKey(nToActualN.get(targetN))) {
            return;
        }

        long startTime = System.nanoTime();

        boolean isEvenSystem = (targetN % 2 == 0);
        Vec3 CIRCLE_CENTER = !isEvenSystem ? CENTER_OFFSET_ODD : CENTER_OFFSET_EVEN;

        ClassUtils.ArraySet<Vec3> innerPoints = new ClassUtils.ArraySet<>(); // 已确定的点 (距离为[0, R]的点)
        ClassUtils.ArraySet<Vec3> currentRingPoints = new ClassUtils.ArraySet<>(); // 距离为(R-1, R]的点

        ClassUtils.QueueSet<Vec3> currentQueue;
        ClassUtils.QueueSet<Vec3> nextQueue = new ClassUtils.QueueSet<>(); // 距离为(R, ignored)的点

        if (!isEvenSystem) { // 奇数, (0, 0)圆心
            nextQueue.add(new Vec3(0, 0, 0));
        } else { // 偶数, (0.5, 0.5)圆心
            nextQueue.add(new Vec3(1, 0, 0)); // (1, 0)
            nextQueue.add(new Vec3(1, 0, 1)); // (1, 1)
            nextQueue.add(new Vec3(0, 0, 1)); // (0, 1)
            nextQueue.add(new Vec3(0, 0, 0)); // (0, 0)
        }

        double currentRadius = 0;
        int minSize = 0;

        int round = 0;
        // 圆形扩张的BFS层级遍历
        while (innerPoints.size() < targetN) {
            if (nextQueue.isEmpty()) { // 已结束或者意外
                BattleRoyale.LOGGER.debug("CircleGridCalculator: nextQueue.isEmpty()");
                break;
            }

            currentQueue = nextQueue;
            nextQueue = new ClassUtils.QueueSet<>();
            minSize = innerPoints.size(); // innerPoints.size()会更新, 在n=50的时候会漏掉, 提前保存

            currentRadius = calculateCurrentRadius(isEvenSystem, ++round);
            // 直接得到"填满圆形水池"的结果, 而不是曼哈顿层级遍历
            while (!currentQueue.isEmpty()) {
                Vec3 currentPoint = currentQueue.poll();
                if (innerPoints.contains(currentPoint) // 已记录至最终结果
                        || currentRingPoints.contains(currentPoint)) continue; // 已添加到当前最外圈列表
                double distance = currentPoint.distanceTo(CIRCLE_CENTER);

                // R < 距离
                if (currentRadius < distance) { // 外圈可能需要遍历周围
                    if (currentRadius + 1 < distance) continue; // 外圈+1不遍历周围
                    else if (!nextQueue.contains(currentPoint)) { // 稍微节约一点开销
                        nextQueue.add(currentPoint);
                    }
                }
                // 距离 <= R
                else if (currentRadius - 1 < distance) { // 距离为(R-1, R]
                    currentRingPoints.add(currentPoint); // 先记录
                }
                else { // 距离为(ignored, R-1]
                    innerPoints.add(currentPoint);
                }

                // 距离 < R+1 搜索邻居
                for (int[] dir : directions) {
                    Vec3 neighborOffset = currentPoint.add(dir[0], 0, dir[1]);
                    if (innerPoints.contains(neighborOffset)) continue; // 已记录至最终结果

                    double otherDistance = neighborOffset.distanceTo(CIRCLE_CENTER);
                    if (currentRadius < otherDistance) { // 距离为外圈, 确保边界点进入对应分支
                        if (currentRadius + 1 < otherDistance) continue; // 外圈+1不遍历周围
                        else if (!nextQueue.contains(neighborOffset)) { // 稍微节约一点开销
                            nextQueue.add(neighborOffset);
                        }
                    } else if (currentRadius - 1 < otherDistance) {
                        if (currentRingPoints.contains(neighborOffset)) continue; // 已添加到当前最外圈列表 (已经记录)
                        currentQueue.add(neighborOffset);
                    } else if (!currentQueue.contains(neighborOffset)) {
                        BattleRoyale.LOGGER.warn("CircleGridCalculator: Point({},{}) is ({},{})'s neighbor but not visited, targetN={}, currentRadius={}, ", neighborOffset.x,neighborOffset.z, currentPoint.x,currentPoint.z, targetN, currentRadius);
                        currentQueue.add(neighborOffset); // 之后会遍历邻居
                    }
                }
            }
            innerPoints.addAll(currentRingPoints);
            currentRingPoints.clear();
        }

        // 稳定排序, 在外部排序一次就行
        List<Vec3> finalPoints = innerPoints.asList();
        // 先偏移再排序的精度略微高些
        finalPoints.replaceAll(vec3 -> vec3.subtract(CIRCLE_CENTER.x, 0, CIRCLE_CENTER.z));
        finalPoints.sort(Comparator
                .comparingDouble(Vec3.ZERO::distanceTo) // 到对应圆心的距离
                .thenComparingDouble(p -> -p.z) // 先北南(上下), 后东西(右左)
                .thenComparingDouble(p -> -p.x)
        );

        if (finalPoints.size() < targetN) { // 测试数据没遇到, 但保留
            nToActualN.put(targetN, 0);
            BattleRoyale.LOGGER.error("CircleGridCalculator: BFS exhausted, actual size {} < targetN {}, finalRadius={}", finalPoints.size(), targetN, currentRadius);
            return;
        }

        // 1+4k & 4k检查
        if (!isEvenSystem && (finalPoints.size() - 1) % 4 != 0
                || isEvenSystem && finalPoints.size() % 4 != 0) {
            BattleRoyale.LOGGER.warn("CircleGridCalculator: n = {}, finalPoints.size() = {}, but should be {} even k is not continuous", n, finalPoints.size(), !isEvenSystem ? "1+4k" : "4k");
        }

        // 根据保留的最外圈的点计算所有可能的N值
        int actualN = finalPoints.size();
        if (!isEvenSystem && actualN % 2 == 0 // 需要奇数
                || isEvenSystem && actualN % 2 != 0) { // 需要偶数
            actualN--;
        }
        List<Integer> possibleN = new ArrayList<>();
        for (int pN = actualN; pN > minSize; pN -= 2) {
            if (pN <= 0) continue; // 防御一下

            possibleN.add(pN);
            if (!nToActualN.containsKey(pN)) {
                nToActualN.put(pN, actualN);
            } else {
                BattleRoyale.LOGGER.warn("CircleGridCalculator: attempt to create N -> actualN ({} -> {}), but already has ({} -> {})", pN, actualN, pN, nToActualN.get(pN));
            }
        }
        calculatedGridData.put(actualN, new CircleGridData(finalPoints, currentRadius, possibleN));
        long endTime = System.nanoTime();
        BattleRoyale.LOGGER.debug("CircleGridCalculator: calculateCircleGrid({}), finalPoints.size()={}, radius={}, possibleN.size()={}, minSize={}, time={}ms", n, finalPoints.size(), currentRadius, possibleN.size(), minSize, (endTime - startTime) / 1_000_000.0);
    }

    /**
     * 获取 N 个点时的计算数据，如果未缓存则立即计算。
     */
    public static @NotNull CircleGridData getCircleGrid(int n) {
        int targetN = getTargetN(n);
        calculateCircleGrid(targetN);
        CircleGridData circleGridData = calculatedGridData.get(nToActualN.get(targetN));

        if (circleGridData == null
                || circleGridData.isEmpty()) {
            if (circleGridData == null) {
                BattleRoyale.LOGGER.warn("CircleGridCalculator: getCircleGrid({}), targetN = {}, but calculatedGridData.get({}) is null", n, targetN, targetN);
            } else {
                BattleRoyale.LOGGER.error("CircleGridCalculator: getCircleGrid({}), targetN = {}, but calculatedGridData.get({}) has broken calculation result", n, targetN, targetN);
            }
            return EMPTY;
        }

        return circleGridData;
    }

    public static void debugResult() {
        BattleRoyale.LOGGER.debug("CircleGridCalculatrl: --------DEBUG RESULT--------");
        BattleRoyale.LOGGER.debug("N -> actual N (total:{})", nToActualN.size());
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : nToActualN.entrySet()) {
            BattleRoyale.LOGGER.debug("[{}] {} -> {}", ++i, entry.getKey(), entry.getValue());
        }
        BattleRoyale.LOGGER.debug("calculatedGridData (total:{})", calculatedGridData.size());
        i = 0;
        for (Map.Entry<Integer, CircleGridData> entry : calculatedGridData.entrySet()) {
            CircleGridData data = entry.getValue();
            BattleRoyale.LOGGER.debug("[{}] key:{}, minRadius:{}", i++, entry.getKey(), data.minRadius);
            for (int j = 0; j < data.size(); j += 5) {
                StringBuilder s = new StringBuilder();
                for (int k = j; k < j + 5 && k < data.size(); k++) {
                    Vec3 v = data.points.get(k);
                    s.append(String.format(" (%s,%s)", v.x, v.z));
                }
                BattleRoyale.LOGGER.debug("\t{}", s);
            }
        }
    }
}