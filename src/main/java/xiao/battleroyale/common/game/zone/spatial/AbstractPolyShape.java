package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

public abstract class AbstractPolyShape extends AbstractSimpleShape {

    protected double startApothem; // 起始内接圆半径
    protected double endApothem; // 终点内接圆半径
    protected double apothemDist; // 内接圆半径差值

    public AbstractPolyShape(StartEntry startEntry, EndEntry endEntry) {
        super(startEntry, endEntry);
    }

    public double getStartApothem() {
        return startApothem;
    }

    public double getApothem(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected apothem calculation");
        }
        return startApothem + apothemDist * allowedProgress;
    }

    public double getEndApothem() {
        return endApothem;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        if (startDimension.x != startDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal hexagon shape start dimension (x: {}, z:{}), defaulting to x", startDimension.x, startDimension.z);
            startDimension = new Vec3(startDimension.x, startDimension.y, startDimension.x);
        }
        if (endDimension.x != endDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal hexagon shape end dimension (x: {}, z:{}), defaulting to x", endDimension.x, endDimension.z);
            endDimension = new Vec3(endDimension.x, endDimension.y, endDimension.x);
        }

        startApothem = startDimension.x * Mth.cos((float) (Math.PI / (float)getSegments()));
        endApothem = endDimension.x * Mth.cos((float) (Math.PI / (float)getSegments()));
        apothemDist = endApothem - startApothem;

        return true;
    }
}
