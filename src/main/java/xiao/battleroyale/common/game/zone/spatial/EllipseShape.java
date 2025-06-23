package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.client.renderer.game.ZoneRenderer;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import javax.annotation.Nullable;

/**
 * 二维 椭圆
 */
public class EllipseShape extends AbstractSimpleShape {

    public EllipseShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0) {
            return false;
        }
        if (!isDetermined()) {
            return false;
        }
        double allowedProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension;
        double rotateDegree;
        if (Math.abs(allowedProgress - cachedProgress) < EPSILON) {
            center = cachedCenter;
            dimension = cachedDimension;
            rotateDegree = cachedRotateDegree;
        } else {
            center = getCenterPos(allowedProgress);
            dimension = getDimension(allowedProgress);
            rotateDegree = getRotateDegree(allowedProgress);
            cachedCenter = center;
            cachedDimension = dimension;
            cachedRotateDegree = rotateDegree;
            cachedProgress = allowedProgress;
        }

        boolean isDimXNegative = dimension.x < 0;
        boolean isDimZNegative = dimension.z < 0;
        boolean isZoneInverted = isDimXNegative != isDimZNegative;

        double effectiveA = Math.abs(dimension.x);
        double effectiveB = Math.abs(dimension.z);

        // 处理退化情况：点或线段
        if (effectiveA <= EPSILON && effectiveB <= EPSILON) {
            double distFromCenterSq = (checkPos.x - center.x) * (checkPos.x - center.x) + (checkPos.z - center.z) * (checkPos.z - center.z);
            if (distFromCenterSq < EPSILON * EPSILON) {
                return !isZoneInverted;
            } else {
                return isZoneInverted;
            }
        } else if (effectiveA <= EPSILON) { // 退化为Z轴线段
            double x_translated = checkPos.x - center.x;
            double z_translated = checkPos.z - center.z;
            double x_rotated;
            double z_rotated;
            if (Math.abs(rotateDegree) < EPSILON) {
                x_rotated = x_translated;
                z_rotated = z_translated;
            } else {
                double radians = Math.toRadians(rotateDegree);
                double cosDegree = Math.cos(radians);
                double sinDegree = Math.sin(radians);
                x_rotated = x_translated * cosDegree + z_translated * sinDegree;
                z_rotated = -x_translated * sinDegree + z_translated * cosDegree;
            }
            return (Math.abs(x_rotated) < EPSILON) && (Math.abs(z_rotated) <= effectiveB) != isZoneInverted;
        } else if (effectiveB <= EPSILON) { // 退化为X轴线段
            double x_translated = checkPos.x - center.x;
            double z_translated = checkPos.z - center.z;
            double x_rotated;
            double z_rotated;
            if (Math.abs(rotateDegree) < EPSILON) {
                x_rotated = x_translated;
                z_rotated = z_translated;
            } else {
                double radians = Math.toRadians(rotateDegree);
                double cosDegree = Math.cos(radians);
                double sinDegree = Math.sin(radians);
                x_rotated = x_translated * cosDegree + z_translated * sinDegree;
                z_rotated = -x_translated * sinDegree + z_translated * cosDegree;
            }
            return (Math.abs(z_rotated) < EPSILON) && (Math.abs(x_rotated) <= effectiveA) != isZoneInverted;
        }

        double x_translated = checkPos.x - center.x;
        double z_translated = checkPos.z - center.z;

        double minRadiusSq = Math.min(effectiveA, effectiveB);
        minRadiusSq *= minRadiusSq;

        double maxRadiusSq = Math.max(effectiveA, effectiveB);
        maxRadiusSq *= maxRadiusSq;

        double distFromCenterSq = x_translated * x_translated + z_translated * z_translated;

        // 内接圆判断
        if (distFromCenterSq <= minRadiusSq) {
            return !isZoneInverted;
        }

        // 外接圆判断
        if (distFromCenterSq >= maxRadiusSq) {
            return isZoneInverted;
        }

        // 详细的椭圆方程计算
        double x_rotated;
        double z_rotated;

        if (Math.abs(rotateDegree) < EPSILON) {
            x_rotated = x_translated;
            z_rotated = z_translated;
        } else {
            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            x_rotated = x_translated * cosDegree + z_translated * sinDegree;
            z_rotated = -x_translated * sinDegree + z_translated * cosDegree;
        }

        /*
         * 椭圆方程
         * x^2   y^2
         * --- + --- = 1，在椭圆上则=1，椭圆内则小于1
         * a^2   b^2
         */
        double result = (x_rotated * x_rotated) / (effectiveA * effectiveA) + (z_rotated * z_rotated) / (effectiveB * effectiveB);
        return (result <= 1.0 + EPSILON) != isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.ELLIPSE;
    }

    @Override
    public int getSegments() {
        return ZoneRenderer.ELLIPSE_SEGMENTS;
    }
}
