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
 * 二维 十字形
 */
public class CrossShape extends AbstractSimpleShape {

    public CrossShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0) { // 进度小于0则为未创建
            return false;
        }
        if (!isDetermined()) {
            return false;
        }
        double allowedProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension; // dimension.x 为外正方形半边长，dimension.z 为内正方形半边长
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

        double rawOuterHalfWidth = dimension.x;
        double rawInnerHalfWidth = dimension.z;

        double effectiveOuterHalfWidth = Math.abs(rawOuterHalfWidth);
        double effectiveInnerHalfWidth = Math.abs(rawInnerHalfWidth);

        boolean isZoneInverted = rawOuterHalfWidth < 0;

        double pX_relative = checkPos.x - center.x;
        double pZ_relative = checkPos.z - center.z;

        double pX_rotated;
        double pZ_rotated;

        if (Math.abs(rotateDegree) < EPSILON) {
            pX_rotated = pX_relative;
            pZ_rotated = pZ_relative;
        } else {
            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            pX_rotated = pX_relative * cosDegree + pZ_relative * sinDegree;
            pZ_rotated = -pX_relative * sinDegree + pZ_relative * cosDegree;
        }

        return ( // 横向矩形：X轴长（外半长），Z轴窄（内半长）
                (Math.abs(pX_rotated) <= effectiveOuterHalfWidth
                        && Math.abs(pZ_rotated) <= effectiveInnerHalfWidth)
                // 纵向矩形：X轴窄（内半长），Z轴长（外半长）
                ||
                        (Math.abs(pX_rotated) <= effectiveInnerHalfWidth
                        && Math.abs(pZ_rotated) <= effectiveOuterHalfWidth)
        ) == !isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CROSS;
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
        /*
            __
         __|  |__
        |__    __|
           |__|
         */
        return 12;
    }
}
