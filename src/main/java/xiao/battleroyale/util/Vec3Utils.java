package xiao.battleroyale.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Vec3Utils {

    public static final double EPSILON = 1e-9;

    @Nullable
    public static Vec3 addVec(@Nullable Vec3 baseVec, @Nullable Vec3 addedVec) {
        if (addedVec == null || baseVec == null) {
            return baseVec;
        }
        return baseVec.add(addedVec);
    }

    /**
     * 对两个 Vec3 进行线性插值
     * 结果点 = startVec + (endVec - startVec) * delta
     * @param startVec 起始向量 (当delta为0时返回)
     * @param endVec 结束向量 (当delta为1时返回)
     * @param delta 插值因子，通常在0.0到1.0之间
     * @return 插值后的 Vec3
     */
    public static Vec3 lerp(Vec3 startVec, Vec3 endVec, double delta) {
        if (delta == 0) {
            return startVec;
        } else if (delta == 1) {
            return endVec;
        }
        return new Vec3(
                startVec.x + (endVec.x - startVec.x) * delta,
                startVec.y + (endVec.y - startVec.y) * delta,
                startVec.z + (endVec.z - startVec.z) * delta
        );
    }

    public static Vec3 positive(Vec3 baseVec) {
        return new Vec3(Math.abs(baseVec.x), Math.abs(baseVec.y), Math.abs(baseVec.z));
    }

    public static boolean hasNegative(Vec3 checkVec) {
        return checkVec.x < 0 || checkVec.y < 0 || checkVec.z < 0;
    }

    /**
     * 判断两向量各绝对值是否相等
     */
    public static boolean equalAbs(Vec3 v1, Vec3 v2) {
        return v1.equals(v2)
                || (Math.abs(Math.abs(v1.x) - Math.abs(v2.x)) < EPSILON && Math.abs(Math.abs(v1.y) - Math.abs(v2.y)) < EPSILON && Math.abs(Math.abs(v1.z) - Math.abs(v2.z)) < EPSILON);
    }

    /**
     * 判断向量的XZ分量绝对值是否相等
     */
    public static boolean equalXZAbs(Vec3 v) {
        return Math.abs(Math.abs(v.x) - Math.abs(v.z)) < EPSILON;
    }

    /**
     * 判断向量的XYZ分量绝对值是否相等
     */
    public static boolean equalXYZAbs(Vec3 v) {
        double yAbs = Math.abs(v.y);
        return Math.abs(Math.abs(v.x) - yAbs) < EPSILON
                && Math.abs(yAbs - Math.abs(v.z)) < EPSILON;
    }

    /**
     * 将向量的X分量的绝对值应用到Z分量
     */
    public static Vec3 applyXAbsToZ(Vec3 baseV) {
        return new Vec3(baseV.x, baseV.y, Math.abs(baseV.x) * Mth.sign(baseV.z));
    }

    /**
     * 将向量的Y分量绝对值应用到XZ分量
     */
    public static Vec3 applyYAbsToXZ(Vec3 baseV) {
        double yAbs = Math.abs(baseV.y);
        return new Vec3(yAbs * Mth.sign(baseV.x), baseV.y, yAbs * Mth.sign(baseV.z));
    }

    /**
     * 以输入向量为基准，往XZ正反方向随机偏移
     */
    public static Vec3 randomAdjustXZ(@NotNull Vec3 baseVec, double range, Supplier<Float> random) {
        return baseVec.add(range * (random.get() - 0.5F) * 2, 0, range * (random.get() - 0.5F) * 2);
    }
    public static Vec3 randomAdjustXZ(@NotNull Vec3 baseVec, Vec3 offVec, Supplier<Float> random) {
        return randomAdjustXZ(baseVec, offVec.x, offVec.z, random);
    }
    public static Vec3 randomAdjustXZ(@NotNull Vec3 baseVec, double x, double z, Supplier<Float> random) {
        return baseVec.add(x * (random.get() - 0.5F) * 2, 0, z * (random.get() - 0.5F) * 2);
    }

    /**
     * 以输入向量为基准，往XYZ正反方向随机偏移
     */
    public static Vec3 randomAdjustXYZ(@NotNull Vec3 baseVec, double range, Supplier<Float> random) {
        return baseVec.add(range * (random.get() - 0.5F) * 2, range * (random.get() - 0.5F) * 2, range * (random.get() - 0.5F) * 2);
    }
    public static Vec3 randomAdjustXYZ(@NotNull Vec3 baseVec, Vec3 offVec, Supplier<Float> random) {
        return randomAdjustXYZ(baseVec, offVec.x, offVec.y, offVec.z, random);
    }
    public static Vec3 randomAdjustXYZ(@NotNull Vec3 baseVec, double x, double y, double z, Supplier<Float> random) {
        return baseVec.add(x * (random.get() - 0.5F) * 2, y * (random.get() - 0.5F) * 2, z * (random.get() - 0.5F) * 2);
    }

    /**
     * 以输入向量为基准，往XZ正反方向，Y正方向随机偏移
     */
    public static Vec3 randomAdjustXZExpandY(@NotNull Vec3 baseVec, double range, Supplier<Float> random) {
        return randomAdjustXZExpandY(baseVec, range, range, range, random);
    }
    public static Vec3 randomAdjustXZExpandY(@NotNull Vec3 baseVec, Vec3 offVec, Supplier<Float> random) {
        return randomAdjustXZExpandY(baseVec, offVec.x, offVec.y, offVec.z, random);
    }
    public static Vec3 randomAdjustXZExpandY(@NotNull Vec3 baseVec, double x, double y, double z, Supplier<Float> random) {
        return baseVec.add(x * (random.get() - 0.5F) * 2, y * random.get(), z * (random.get() - 0.5F) * 2);
    }

    /**
     * 缩放XZ方向scale倍
     */
    public static Vec3 scaleXZ(@NotNull Vec3 v, double scale) {
        return v.multiply(scale, 1, scale);
    }

    /**
     * 缩放XYZ方向scale倍
     */
    public static Vec3 scaleXYZ(@NotNull Vec3 v, double scale) {
        return v.multiply(scale, scale, scale);
    }

    /**
     * 以输入向量为基准，取dimension.x为半径，在XZ平面的圆内随机取点
     */
    public static Vec3 randomCircleXZ(@NotNull Vec3 baseVec, Vec3 dimension, Supplier<Float> random) {
        double angle = 2 * Math.PI * random.get();
        double radius = dimension.x * Math.sqrt(random.get());
        return baseVec.add(radius * Math.cos(angle), 0, radius * Math.sin(angle));
    }

    /**
     * 以输入向量为基准，取dimension.x为半径，在XZ平面的圆内随机取点，Y正方向随机偏移
     */
    public static Vec3 randomCircleXZExpandY(@NotNull Vec3 baseVec, Vec3 dimension, Supplier<Float> random) {
        double angle = 2 * Math.PI * random.get();
        double radius = dimension.x * Math.sqrt(random.get());
        return baseVec.add(radius * Math.cos(angle), dimension.y * random.get(), radius * Math.sin(angle));
    }
}
