package xiao.battleroyale.api.game.zone.shape;

import xiao.battleroyale.api.game.zone.IZoneEntry;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

public interface IZoneShapeEntry extends IZoneEntry {

    ZoneShapeType getZoneShapeType();

    ISpatialZone createSpatialZone();
}