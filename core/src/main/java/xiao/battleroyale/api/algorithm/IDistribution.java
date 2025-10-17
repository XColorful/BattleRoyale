package xiao.battleroyale.api.algorithm;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IDistribution {

    List<Vec3> distributed(Vec3 center, Vec3 dimension, int count);

    List<Vec3> distributed(Vec3 center, Vec3 dimension, int count, boolean allowOnBorder, double globalShrinkRatio);
}
