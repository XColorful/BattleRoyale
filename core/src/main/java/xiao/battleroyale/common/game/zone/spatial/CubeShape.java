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
        assert startDimension != null && endDimension != null;

        hasBadShape = hasNegativeDimension();
        checkBadShape = hasBadShape && !allowBadShape;
        needEqualAbs = !hasEqualXYZAbsDimension();
        return true;
    }

    @Override
    public int getSegments() {
        return 4;
    }


    @Override
    public @Nullable Vec3 getStartDimension() {
        if (startDimension == null) return null;
        Vec3 baseVec = needEqualAbs ? Vec3Utils.applyYAbsToXZ(startDimension) : startDimension;
        if (checkBadShape) {
            return Vec3Utils.toPositiveAndEqualXZ(baseVec);
        } else {
            return baseVec;
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
        if (needEqualAbs) {
            baseVec = Vec3Utils.applyYAbsToXZ(baseVec);
        }
        if (checkBadShape) {
            return Vec3Utils.toPositiveAndEqualXZ(baseVec);
        } else {
            return baseVec;
        }
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        if (endDimension == null) return null;
        Vec3 baseVec = needEqualAbs ? Vec3Utils.applyYAbsToXZ(endDimension) : endDimension;
        if (checkBadShape) {
            return Vec3Utils.toPositiveAndEqualXZ(baseVec);
        } else {
            return baseVec;
        }
    }
}
