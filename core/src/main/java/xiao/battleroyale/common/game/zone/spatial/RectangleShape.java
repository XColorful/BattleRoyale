package xiao.battleroyale.common.game.zone.spatial;

import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

/**
 * 二维 RectangleGrid
 */
public class RectangleShape extends AbstractSimpleShape {

    public RectangleShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.RECTANGLE;
    }

    @Override
    public int getSegments() {
        return 4;
    }
}
