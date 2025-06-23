package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.client.renderer.game.ZoneRenderer;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import javax.annotation.Nullable;

public class EllipsoidShape extends Abstract3DShape {

    public EllipsoidShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0 || !isDetermined()) {
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

        // 椭球在 X, Y, Z 轴方向上的半轴长
        double effectiveA = Math.abs(dimension.x);
        double effectiveB = Math.abs(dimension.y);
        double effectiveC = Math.abs(dimension.z);

        boolean isZoneInverted = Mth.sign(dimension.x) * Mth.sign(dimension.y) * Mth.sign(dimension.z) < 0;

        double pX_relative = checkPos.x - center.x;
        double pY_relative = checkPos.y - center.y;
        double pZ_relative = checkPos.z - center.z;

        double distFromCenterSq = pX_relative * pX_relative + pY_relative * pY_relative + pZ_relative * pZ_relative;

        double minRadius = Math.min(effectiveA, Math.min(effectiveB, effectiveC));
        double maxRadius = Math.max(effectiveA, Math.max(effectiveB, effectiveC));

        double minRadiusSq = minRadius * minRadius;
        double maxRadiusSq = maxRadius * maxRadius;

        // 内接球判断
        if (distFromCenterSq <= minRadiusSq) {
            return !isZoneInverted;
        }

        // 外接球判断
        if (distFromCenterSq >= maxRadiusSq) {
            return isZoneInverted;
        }


        double finalCheckX;
        double finalCheckZ;

        // 仅在 XZ 平面进行旋转（绕 Y 轴旋转）
        if (Math.abs(rotateDegree) < EPSILON) {
            finalCheckX = pX_relative;
            finalCheckZ = pZ_relative;
        } else {
            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            finalCheckX = pX_relative * cosDegree + pZ_relative * sinDegree;
            finalCheckZ = -pX_relative * sinDegree + pZ_relative * cosDegree;
        }

        double result;

        // 处理退化情况，防止除以零，并处理极端形状
        if (effectiveA <= EPSILON && effectiveB <= EPSILON && effectiveC <= EPSILON) { // 退化为点
            double currentDistSq = finalCheckX * finalCheckX + pY_relative * pY_relative + finalCheckZ * finalCheckZ;
            return (currentDistSq < EPSILON * EPSILON) != isZoneInverted;
        } else if (effectiveA <= EPSILON && effectiveB <= EPSILON) { // 退化为Z轴线段
            return (Math.abs(finalCheckX) < EPSILON && Math.abs(pY_relative) < EPSILON && Math.abs(finalCheckZ) <= effectiveC) != isZoneInverted;
        } else if (effectiveA <= EPSILON && effectiveC <= EPSILON) { // 退化为Y轴线段
            return (Math.abs(finalCheckX) < EPSILON && Math.abs(finalCheckZ) < EPSILON && Math.abs(pY_relative) <= effectiveB) != isZoneInverted;
        } else if (effectiveB <= EPSILON && effectiveC <= EPSILON) { // 退化为X轴线段
            return (Math.abs(pY_relative) < EPSILON && Math.abs(finalCheckZ) < EPSILON && Math.abs(finalCheckX) <= effectiveA) != isZoneInverted;
        } else if (effectiveA <= EPSILON) { // 退化为YZ平面上的椭圆
            result = (pY_relative * pY_relative) / (effectiveB * effectiveB) + (finalCheckZ * finalCheckZ) / (effectiveC * effectiveC);
            return (Math.abs(finalCheckX) < EPSILON && result <= 1.0 + EPSILON) != isZoneInverted;
        } else if (effectiveB <= EPSILON) { // 退化为XZ平面上的椭圆
            result = (finalCheckX * finalCheckX) / (effectiveA * effectiveA) + (finalCheckZ * finalCheckZ) / (effectiveC * effectiveC);
            return (Math.abs(pY_relative) < EPSILON && result <= 1.0 + EPSILON) != isZoneInverted;
        } else if (effectiveC <= EPSILON) { // 退化为XY平面上的椭圆
            result = (finalCheckX * finalCheckX) / (effectiveA * effectiveA) + (pY_relative * pY_relative) / (effectiveB * effectiveB);
            return (Math.abs(finalCheckZ) < EPSILON && result <= 1.0 + EPSILON) != isZoneInverted;
        } else {
            // 正常三维椭球方程计算
            result = (finalCheckX * finalCheckX) / (effectiveA * effectiveA)
                    + (pY_relative * pY_relative) / (effectiveB * effectiveB)
                    + (finalCheckZ * finalCheckZ) / (effectiveC * effectiveC);
        }

        return (result <= 1.0 + EPSILON) != isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.ELLIPSOID;
    }

    @Override
    public int getSegments() {
        return ZoneRenderer.ELLIPSOID_SEGMENTS;
    }
}
