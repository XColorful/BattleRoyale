package xiao.battleroyale.common.game.zone.spatial;

import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

public class CubeShape extends Abstract3DShape {

    public CubeShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CUBE;
    }

    @Override
    public int getSegments() {
        return 4;
    }
}
