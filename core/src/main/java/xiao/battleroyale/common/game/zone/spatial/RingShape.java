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
 * 二维 环形
 */
public class RingShape extends AbstractSimpleShape {

    private static int RING_SEGMENTS = 64;
    public static int getRingSegments() { return RING_SEGMENTS; }
    public static void setRingSegments(int segments) { RING_SEGMENTS = Math.max(32, segments); }

    protected boolean needEqualAbs = false;

    public RingShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
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
        double outerDimSq = dimension.x * dimension.x;
        double innerDimSq = dimension.z * dimension.z;
        boolean isZoneInverted = dimension.x < 0;
        // 旋转对圆没有几何影响

        // 忽略y方向
        double xDist = center.x - checkPos.x;
        double zDist = center.z - checkPos.z;
        double distSq = xDist * xDist + zDist * zDist;
        return ( // 在外圆环内
                distSq < outerDimSq
                 // 在内圆环内
                && distSq > innerDimSq
        ) == !isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.RING;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        assert startDimension != null && endDimension != null;

        hasBadShape = hasNegativeDimension()
                || startDimension.x < startDimension.z || endDimension.x < endDimension.z;
        checkBadShape = hasBadShape && !allowBadShape;
        return true;
    }

    @Override
    public @Nullable Vec3 getStartDimension() {
        if (startDimension == null) return null;
        if (checkBadShape) {
            return Vec3Utils.toPositiveAndXGreaterThanZ(startDimension);
        } else {
            return startDimension;
        }
    }

    @Override
    public @Nullable Vec3 getDimension(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected dimension calculation");
        }
        Vec3 baseVec = getDimensionNoCheck(allowedProgress);
        if (baseVec == null) return null;
        if (checkBadShape) {
            return Vec3Utils.toPositiveAndXGreaterThanZ(baseVec);
        } else {
            return baseVec;
        }
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        if (endDimension == null) return null;
        if (checkBadShape) {
            return Vec3Utils.toPositiveAndXGreaterThanZ(endDimension);
        } else {
            return endDimension;
        }
    }

    @Override
    public int getSegments() {
        return RING_SEGMENTS;
    }
}
