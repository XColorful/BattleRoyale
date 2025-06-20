package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.util.Vec3Utils;

public abstract class AbstractPolyShape extends AbstractSimpleShape {

    protected double startApothem; // 起始内接圆半径
    protected double endApothem; // 终点内接圆半径
    protected double apothemDist; // 内接圆半径差值

    public AbstractPolyShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    public double getStartApothem() {
        return checkBadShape ? Math.abs(startApothem) : startApothem;
    }

    public double getApothem(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected apothem calculation");
        }
        double apothem = startApothem + apothemDist * allowedProgress;
        return checkBadShape ? Math.abs(apothem) : apothem;
    }

    public double getEndApothem() {
        return checkBadShape ? Math.abs(endApothem) : endApothem;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        boolean willProduceBadShape = hasNegativeDimension()
                || !Vec3Utils.equalXZAbs(startDimension) || !Vec3Utils.equalXZAbs(endDimension);
        this.checkBadShape = willProduceBadShape && !allowBadShape;

        startApothem = startDimension.x * Mth.cos((float) (Math.PI / (float)getSegments()));
        endApothem = endDimension.x * Mth.cos((float) (Math.PI / (float)getSegments()));
        apothemDist = endApothem - startApothem;

        return true;
    }

    @Override
    public @Nullable Vec3 getStartDimension() {
        if (checkBadShape
                && (Vec3Utils.hasNegative(startDimension) || !Vec3Utils.equalXZAbs(startDimension))) {
            return Vec3Utils.positive(Vec3Utils.applyXAbsToZ(startDimension));
        } else {
            return startDimension;
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
        if (checkBadShape
                && (Vec3Utils.hasNegative(baseVec) || !Vec3Utils.equalXZAbs(baseVec))) {
            return Vec3Utils.positive(Vec3Utils.applyXAbsToZ(baseVec));
        } else {
            return baseVec;
        }
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        if (checkBadShape
                && (Vec3Utils.hasNegative(endDimension) || !Vec3Utils.equalXZAbs(endDimension))) {
            return Vec3Utils.positive(Vec3Utils.applyXAbsToZ(endDimension));
        } else {
            return endDimension;
        }
    }
}
