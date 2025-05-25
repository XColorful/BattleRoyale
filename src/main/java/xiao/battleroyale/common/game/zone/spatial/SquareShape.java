package xiao.battleroyale.common.game.zone.spatial;

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
        if (startDimension.x != startDimension.z || endDimension.x != endDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal square shape center or dimension");
            return false;
        }
        return true;
    }
}
