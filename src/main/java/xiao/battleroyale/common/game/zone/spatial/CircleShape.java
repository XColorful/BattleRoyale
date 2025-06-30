package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.Vec3Utils;

/**
 * 二维 Circle
 */
public class CircleShape extends AbstractSimpleShape {

    private static int CIRCLE_SEGMENTS = 64;
    public static int getCircleSegments() { return CIRCLE_SEGMENTS; }
    public static void setCircleSegments(int segments) { CIRCLE_SEGMENTS = Math.max(32, segments); }

    protected boolean needEqualAbs = false;

    public CircleShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
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
        double allowProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension;
        if (Math.abs(allowProgress - cachedProgress) < EPSILON) {
            center = cachedCenter;
            dimension = cachedDimension;
        } else {
            center = getCenterPos(allowProgress);
            dimension = getDimension(allowProgress);
            cachedCenter = center;
            cachedDimension = dimension;
            cachedProgress = allowProgress;
        }
        double dimSq = dimension.x * dimension.z;
        boolean isZoneInverted = dimSq < 0;
        // 旋转对圆没有几何影响

        // 忽略y方向
        double xDist = center.x - checkPos.x;
        double zDist = center.z - checkPos.z;
        return (xDist * xDist + zDist * zDist) <= Math.abs(dimSq) != isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CIRCLE;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        boolean willProduceBadShape = hasNegativeDimension();
        checkBadShape = willProduceBadShape && !allowBadShape;
        needEqualAbs = !hasEqualXZAbsDimension();
        return true;
    }

    @Override
    public int getSegments() {
        return CIRCLE_SEGMENTS;
    }

    @Override
    public @Nullable Vec3 getStartDimension() {
        Vec3 baseV = needEqualAbs ? Vec3Utils.applyXAbsToZ(startDimension) : startDimension;
        if (checkBadShape
                && (Vec3Utils.hasNegative(startDimension) || !Vec3Utils.equalXZAbs(startDimension))) {
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
            baseVec = Vec3Utils.applyXAbsToZ(baseVec);
        }
        if (checkBadShape
                && (Vec3Utils.hasNegative(baseVec) || !Vec3Utils.equalXZAbs(baseVec))) {
            return Vec3Utils.positive(baseVec);
        } else {
            return baseVec;
        }
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        Vec3 baseV = needEqualAbs ? Vec3Utils.applyXAbsToZ(endDimension) : endDimension;
        if (checkBadShape
                && (Vec3Utils.hasNegative(endDimension) || !Vec3Utils.equalXZAbs(endDimension))) {
            return Vec3Utils.positive(baseV);
        } else {
            return baseV;
        }
    }
}
