package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.Vec3Utils;

public class SphereShape extends Abstract3DShape {

    private static int SPHERE_SEGMENTS = 64;
    public static int getSphereSegments() { return SPHERE_SEGMENTS; }
    public static void setSphereSegments(int segments) { SPHERE_SEGMENTS = Math.max(32, segments); }

    protected boolean needEqualAbs = false;

    public SphereShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0 || !isDetermined()) {
            return false;
        }

        double allowedProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension;

        if (Math.abs(allowedProgress - cachedProgress) < EPSILON) {
            center = cachedCenter;
            dimension = cachedDimension;
        } else {
            center = getCenterPos(allowedProgress);
            dimension = getDimension(allowedProgress);
            cachedCenter = center;
            cachedDimension = dimension;
            cachedProgress = allowedProgress;
        }

        boolean isZoneInverted = Mth.sign(dimension.x) * Mth.sign(dimension.y) * Mth.sign(dimension.z) < 0;
        double radiusSq = dimension.y * dimension.y;
        // 旋转对球没有几何影响

        double xDist = center.x - checkPos.x;
        double yDist = center.y - checkPos.y;
        double zDist = center.z - checkPos.z;
        return (xDist*xDist + yDist*yDist + zDist*zDist <= Math.abs(radiusSq)) != isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.SPHERE;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        hasBadShape = hasNegativeDimension();
        checkBadShape = hasBadShape && !allowBadShape;
        needEqualAbs = !hasEqualXYZAbsDimension();
        return true;
    }

    @Override
    public int getSegments() {
        return SPHERE_SEGMENTS;
    }

    @Override
    public @Nullable Vec3 getStartDimension() {
        Vec3 baseV = needEqualAbs ? Vec3Utils.applyYAbsToXZ(startDimension) : startDimension;
        if (checkBadShape
                && (Vec3Utils.hasNegative(startDimension) || !Vec3Utils.equalXYZAbs(startDimension))) {
            return Vec3Utils.positive(baseV);
        } else {
            return baseV;
        }
    }

    @Override
    public @Nullable Vec3 getDimension(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            if (dimensionDist == null) {
                return null;
            }
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected dimension calculation");
        }
        Vec3 baseVec = getDimensionNoCheck(allowedProgress);
        if (needEqualAbs) {
            baseVec = Vec3Utils.applyYAbsToXZ(baseVec);
        }
        if (checkBadShape
                && (Vec3Utils.hasNegative(baseVec) || !Vec3Utils.equalXYZAbs(baseVec))) {
            return Vec3Utils.positive(baseVec);
        } else {
            return baseVec;
        }
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        Vec3 baseV = needEqualAbs ? Vec3Utils.applyYAbsToXZ(endDimension) : endDimension;
        if (checkBadShape
                && (Vec3Utils.hasNegative(endDimension) || !Vec3Utils.equalXYZAbs(endDimension))) {
            return Vec3Utils.positive(baseV);
        } else {
            return baseV;
        }
    }
}
