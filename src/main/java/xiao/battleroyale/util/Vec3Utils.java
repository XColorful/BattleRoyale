package xiao.battleroyale.util;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Vec3Utils {

    /**
     * 以输入向量为基准，往XZ正反方向
     */
    public static Vec3 randomAdjustXZ(@NotNull Vec3 baseVec, double range, Supplier<Float> random) {
        if (range > 0) {
            return new Vec3(baseVec.x + range * (random.get() - 0.5F) * 2,
                    baseVec.y,
                    baseVec.z + range * (random.get() - 0.5F) * 2
            );
        } else {
            return baseVec;
        }
    }
    public static Vec3 randomAdjustXZ(@NotNull Vec3 baseVec, Vec3 offVec, Supplier<Float> random) {
        return randomAdjustXZ(baseVec, offVec.x, offVec.z, random);
    }
    public static Vec3 randomAdjustXZ(@NotNull Vec3 baseVec, double x, double z, Supplier<Float> random) {
        double xOff = x > 0 ? x * (random.get() - 0.5F) * 2 : 0;
        double zOff = z > 0 ? z * (random.get() - 0.5F) * 2 : 0;
        return baseVec.add(xOff, 0, zOff);
    }

    /**
     * 以输入向量为基准，往XZY正反方向，Y正方向随机偏移
     */
    public static Vec3 randomAdjustXYZ(@NotNull Vec3 baseVec, double range, Supplier<Float> random) {
        if (range > 0) {
            return new Vec3(baseVec.x + range * (random.get() - 0.5F) * 2,
                    baseVec.y + range * (random.get() - 0.5F) * 2,
                    baseVec.z + range * (random.get() - 0.5F) * 2
            );
        } else {
            return baseVec;
        }
    }
    public static Vec3 randomAdjustXYZ(@NotNull Vec3 baseVec, Vec3 offVec, Supplier<Float> random) {
        return randomAdjustXYZ(baseVec, offVec.x, offVec.y, offVec.z, random);
    }
    public static Vec3 randomAdjustXYZ(@NotNull Vec3 baseVec, double x, double y, double z, Supplier<Float> random) {
        double xOff = x > 0 ? x * (random.get() - 0.5F) * 2 : 0;
        double yOff = y > 0 ? y * (random.get() - 0.5F) * 2 : 0;
        double zOff = z > 0 ? z * (random.get() - 0.5F) * 2 : 0;
        return baseVec.add(xOff, yOff, zOff);
    }

    /**
     * 以输入向量为基准，往XZ正反方向，Y正方向随机偏移
     */
    public static Vec3 randomAdjustXZExpandY(@NotNull Vec3 baseVec, double range, Supplier<Float> random) {
        if (range > 0) {
            return new Vec3(baseVec.x + range * (random.get() - 0.5F) * 2,
                    baseVec.y + range * random.get(),
                    baseVec.z + range * (random.get() - 0.5F) * 2
            );
        } else {
            return baseVec;
        }
    }
    public static Vec3 randomAdjustXZExpandY(@NotNull Vec3 baseVec, Vec3 offVec, Supplier<Float> random) {
        return randomAdjustXZExpandY(baseVec, offVec.x, offVec.y, offVec.z, random);
    }
    public static Vec3 randomAdjustXZExpandY(@NotNull Vec3 baseVec, double x, double y, double z, Supplier<Float> random) {
        double xOff = x > 0 ? x * (random.get() - 0.5F) * 2 : 0;
        double yOff = y > 0 ? y * random.get() : 0;
        double zOff = z > 0 ? z * (random.get() - 0.5F) * 2 : 0;
        return baseVec.add(xOff, yOff, zOff);
    }

    /**
     * 缩放XZ方向scale倍
     */
    public static Vec3 scaleXZ(@NotNull Vec3 v, double scale) {
        return new Vec3(v.x * scale, v.y, v.z * scale);
    }

    /**
     * 缩放XYZ方向scale倍
     */
    public static Vec3 scaleXYZ(@NotNull Vec3 v, double scale) {
        return v.scale(scale);
    }

    /**
     * 以输入向量为基准，取dimension.x为半径，在XZ平面的圆内随机取点
     */
    public static Vec3 randomCircleXZ(@NotNull Vec3 baseVec, Vec3 dimension, Supplier<Float> random) {
        double angle = 2 * Math.PI * random.get();
        double radius = dimension.x * Math.sqrt(random.get());
        double xOffset = radius * Math.cos(angle);
        double zOffset = radius * Math.sin(angle);
        return baseVec.add(xOffset, 0, zOffset);
    }
}
