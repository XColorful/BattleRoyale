package xiao.battleroyale.common.game.zone.spatial;

import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

public class CuboidShape extends Abstract3DShape {

    public CuboidShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CUBOID;
    }

    @Override
    public int getSegments() {
        return 4;
    }
}
