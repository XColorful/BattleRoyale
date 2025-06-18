package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

public class SquareShape extends AbstractSimpleShape {

    public SquareShape(StartEntry startEntry, EndEntry endEntry) {
        super(startEntry, endEntry);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.SQUARE;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        if (startDimension.x != startDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal circle shape start dimension (x: {}, z:{}), defaulting to x", startDimension.x, startDimension.z);
            startDimension = new Vec3(startDimension.x, startDimension.y, startDimension.x);
        }
        if (endDimension.x != endDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal circle shape end dimension (x: {}, z:{}), defaulting to x", endDimension.x, endDimension.z);
            endDimension = new Vec3(endDimension.x, endDimension.y, endDimension.x);
        }

        return true;
    }
    
    @Override
    public int getSegments() {
        return 4;
    }
}
