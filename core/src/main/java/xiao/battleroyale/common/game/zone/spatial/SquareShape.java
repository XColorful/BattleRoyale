package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.Vec3Utils;

public class SquareShape extends AbstractSimpleShape {

    protected boolean needEqualAbs = false;

    public SquareShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.SQUARE;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        hasBadShape = hasNegativeDimension();
        checkBadShape = hasBadShape && !allowBadShape;
        needEqualAbs = !hasEqualXZAbsDimension();
        return true;
    }

    @Override
    public int getSegments() {
        return 4;
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
            return Vec3Utils.positive(Vec3Utils.applyXAbsToZ(baseVec));
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
