package xiao.battleroyale.algorithm;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.algorithm.ICircleGrid;
import xiao.battleroyale.api.algorithm.IGoldenSpiral;
import xiao.battleroyale.api.algorithm.IRectangleGrid;

import java.util.ArrayList;
import java.util.List;

public class Distribution {
    
    public static class RectangleGrid implements IRectangleGrid {

        private static final RectangleGrid INSTANCE = new RectangleGrid();
        public static IRectangleGrid get() {
            return INSTANCE;
        }
        private RectangleGrid() {}

        /**
         * 计算矩形网格所需的 Nx 和 Ny。
         * 该算法通过系统搜索，保证找到 Nx * Ny >= count 且比例 Nx/Ny 最接近区域长宽比的解。
         * @param dimension 区域尺寸 (X, Z分量为长宽)
         * @param count 目标点位数量
         * @return 包含 {Nx, Ny} 的数组
         */
        private int[] calculateGridSize(Vec3 dimension, int count) {
            if (count <= 0) return new int[]{1, 1};

            final double ratio = dimension.x / dimension.z;

            // 初始化最佳解
            int bestNx = 1;
            int bestNy = count;
            double minError = Double.MAX_VALUE;

            // 搜索上限设为 count，确保找到所有可能的 Nx * Ny >= count 的组合。
            int maxSearch = count;

            for (int ny = 1; ny <= maxSearch; ny++) {
                // 计算满足 Nx * Ny >= count 的最小整数 Nx
                int nx = (int) Math.ceil((double) count / ny);

                // 计算比例误差： |当前比例 - 目标比例|
                double currentRatio = (double) nx / ny;
                double error = Math.abs(currentRatio - ratio);

                // 检查是否是更优解
                if (error < minError) {
                    minError = error;
                    bestNx = nx;
                    bestNy = ny;
                } else if (error == minError) {
                    // 如果误差相同，选择总点数 Nx * Ny 最小的解（最小化过采样）
                    if (nx * ny < bestNx * bestNy) {
                        bestNx = nx;
                        bestNy = ny;
                    }
                }

                // 优化退出：当 Nx 降到 1 时，继续增加 Ny 只会导致比例越来越差。
                if (nx == 1 && ny > 1) {
                    break;
                }
            }

            return new int[]{bestNx, bestNy};
        }

        /**
         * 计算矩形网格采样所需的扩大因子。
         * 该因子 F 使最外层基准点恰好落在原始边界上，即 F = 1 / (1 - 1/N)。
         * @param nx X方向网格数
         * @param ny Z方向网格数
         * @return 扩大因子
         */
        private double getRectangleExpansionFactor(int nx, int ny) {
            // 当网格数 N=1 时，因子为 1.0。

            // S_x = 1 - 1/N_x，收缩因子
            double shrinkFactorX = (nx <= 1) ? 1.0 : (1.0 - (1.0 / nx));
            double shrinkFactorZ = (ny <= 1) ? 1.0 : (1.0 - (1.0 / ny));

            // 扩大因子 F = 1 / S。取 S 的最小值确保在两个方向上都恰好达到边界。
            double minShrinkFactor = Math.min(shrinkFactorX, shrinkFactorZ);

            if (minShrinkFactor == 1.0) {
                return 1.0;
            }

            // 返回扩大因子 F = 1 / S
            return 1.0 / minShrinkFactor;
        }

        /**
         * 在矩形区域内，使用网格抖动采样 (Jittered Grid Sampling) 计算分散点位的基础网格中心点。
         * @param center 区域中心点
         * @param dimension 区域尺寸 (X, Z分量为长宽)
         * @param count 目标点位数量
         * @return 初始网格中心点列表
         */
        @Override public List<Vec3> distributed(Vec3 center, Vec3 dimension, int count) {
            int[] gridSize = calculateGridSize(dimension, count);
            int nx = gridSize[0];
            int ny = gridSize[1];
            return distributed(center, dimension, nx, ny);
        }
        private List<Vec3> distributed(Vec3 center, Vec3 dimension, int nx, int ny) {
            List<Vec3> points = new ArrayList<>();

            // 计算每个网格单元的尺寸 (dx, dz)
            double fullDimX = dimension.x * 2.0;
            double fullDimZ = dimension.z * 2.0;
            double dx = fullDimX / nx;
            double dz = fullDimZ / ny;

            // 计算区域的起始点 (左下角)
            double startX = center.x - dimension.x;
            double startZ = center.z - dimension.z;

            // 生成所有网格单元的中心点 (作为 Jitter 抖动的基准点)
            for (int i = 0; i < nx; i++) {
                for (int j = 0; j < ny; j++) {
                    double basePointX = startX + i * dx + dx / 2.0;
                    double basePointZ = startZ + j * dz + dz / 2.0;

                    Vec3 basePos = new Vec3(basePointX, center.y, basePointZ);
                    points.add(basePos);
                }
            }
            return points;
        }

        // 便利接口
        @Override public List<Vec3> distributed(Vec3 center, Vec3 dimension, int count, boolean allowOnBorder, double globalShrinkRatio) {
            double internalFactor = 1.0;

            int[] gridSize = calculateGridSize(dimension, count);
            // 允许在边界上就需要专门缩放
            // 此时 internalFactor > 1.0，点位范围扩大到原始边界 R
            if (allowOnBorder && count > 1) {
                internalFactor = getRectangleExpansionFactor(gridSize[0], gridSize[1]);
            }

            double finalShrinkRatio = internalFactor * globalShrinkRatio;

            if (finalShrinkRatio == 1.0) {
                return distributed(center, dimension, gridSize[0], gridSize[1]);
            }

            Vec3 shrunkDimension = dimension.multiply(finalShrinkRatio, 1, finalShrinkRatio);
            return distributed(center, shrunkDimension, gridSize[0], gridSize[1]);
        }
        
    }

    public static class GoldenSpiral implements IGoldenSpiral {

        private static final GoldenSpiral INSTANCE = new GoldenSpiral();
        public static IGoldenSpiral get() {
            return INSTANCE;
        }
        private GoldenSpiral() {}

        /**
         * 在圆形区域内，使用黄金角螺旋 (Golden Angle/Fibonacci Spiral) 计算分散点位。
         * 该方法在圆盘区域内产生相对均匀的分布，点位密度从中心向外均匀递减。
         *
         * @param center 区域中心点
         * @param dimension 区域尺寸 (X分量为半径)
         * @param count 目标点位数量
         * @return 初始螺旋点位列表
         */
        @Override public List<Vec3> distributed(Vec3 center, Vec3 dimension, int count) {
            return distributed(center, dimension, count, 1);
        }
        // 便利接口
        @Override public List<Vec3> distributed(Vec3 center, Vec3 dimension, int count, boolean allowOnBorder, double globalShrinkRatio) {
            double shrinkFactor = 1.0;
            if (!allowOnBorder && count > 1) { // 1的时候不缩放
                // 收缩因子计算：1 - 1/√(N+4)
                shrinkFactor = 1.0 - (1.0 / Math.sqrt(count + 4));
            }

            double finalShrinkRatio = shrinkFactor * globalShrinkRatio;

            return distributed(center, dimension, count, finalShrinkRatio);
        }
        private List<Vec3> distributed(Vec3 center, Vec3 dimension, int count, double shrinkRatio) {
            List<Vec3> points = new ArrayList<>();

            // 黄金角: 约 137.5 度，用于在圆上按索引顺序生成低偏差的螺旋角度
            final double GOLDEN_ANGLE = Math.PI * (3.0 - Math.sqrt(5.0));
            double maxRadius = dimension.x * shrinkRatio;

            for (int i = 0; i < count; i++) {
                double angle = i * GOLDEN_ANGLE;

                // 半径 r 的计算采用 r ∝ √i 的形式，保证点位在圆盘区域均匀分布
                // factor = i / (count - 1)。当 i=count-1 时 factor=1，点位位于最大半径上。
                double factor = (count > 1) ? (double) i / (count - 1) : 0.0;
                double radius = maxRadius * Math.sqrt(factor);

                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);

                // 点位 = 中心点 + 偏移
                Vec3 basePos = center.add(x, 0, z);
                points.add(basePos);
            }
            return points;
        }
        
    }

    public static class CircleGrid implements ICircleGrid {

        private static final CircleGrid INSTANCE = new CircleGrid();
        public static ICircleGrid get() {
            return INSTANCE;
        }
        private CircleGrid() {}

        public void preCalculate(int n) {
            CircleGridCalculator.preCalculate(n);
        }
        public void preCalculate(List<Integer> nList) {
            CircleGridCalculator.preCalculate(nList);
        }
        public void preCalculate(int startN, int endN) {
            CircleGridCalculator.preCalculate(startN, endN);
        }

        /**
         * 在圆形区域内，使用双圆心网格计算分散点位。
         * N为奇数，使用(0, 0)为圆心，点数为1+4k
         * N为偶数，使用(sqrt(2)/2, sqrt(2)/2)为圆心，点数为4k
         * @param center 区域中心点
         * @param dimension 区域尺寸 (X分量为半径)
         * @param minCount 目标点位数量
         * @return 初始网格点位列表
         */
        @Override public List<Vec3> distributed(Vec3 center, Vec3 dimension, int minCount) {
            return distributed(center, dimension, minCount, 1.0);
        }
        // 便利接口
        @Override public List<Vec3> distributed(Vec3 center, Vec3 dimension, int minCount, boolean allowOnBorder, double globalShrinkRatio) {
            if (minCount <= 0) return new ArrayList<>();

            double internalShrinkFactor = 1.0;

            if (!allowOnBorder && minCount > 1) {
                // R(N)/R(N+1) 缩放
                int actualN = CircleGridCalculator.getActualN(minCount);
                double rN1 = CircleGridCalculator.getCircleGrid(actualN + 1).minRadius();
                double rN = CircleGridCalculator.getCircleGrid(actualN).minRadius();
                internalShrinkFactor =  rN / rN1;
            }

            double finalShrinkRatio = internalShrinkFactor * globalShrinkRatio;

            return distributed(center, dimension, minCount, finalShrinkRatio);
        }
        private List<Vec3> distributed(Vec3 center, Vec3 dimension, int minCount, double shrinkRatio) {
            CircleGridCalculator.CircleGridData data = CircleGridCalculator.getCircleGrid(minCount);

            List<Vec3> finalPoints = new ArrayList<>(data.size());
            double scale = dimension.x * shrinkRatio / data.minRadius();

            for (Vec3 offset : data.points()) {
                double x = center.x + offset.x * scale;
                double z = center.z + offset.z * scale;

                finalPoints.add(new Vec3(x, center.y, z));
            }

            return finalPoints;
        }
    }
}