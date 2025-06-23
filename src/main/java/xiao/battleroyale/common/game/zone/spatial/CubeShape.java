package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.Vec3Utils;

public class CubeShape extends Abstract3DShape {

    protected boolean needEqualAbs = false;

    public CubeShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CUBE;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        boolean willProduceBadShape = hasNegativeDimension();
        checkBadShape = willProduceBadShape && !allowBadShape;
        needEqualAbs = !hasEqualXYZAbsDimension();
        return true;
    }

    @Override
    public int getSegments() {
        return 4;
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
