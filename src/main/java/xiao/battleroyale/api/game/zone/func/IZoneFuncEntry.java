package xiao.battleroyale.api.game.zone.func;

import xiao.battleroyale.api.game.zone.IZoneEntry;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public interface IZoneFuncEntry extends IZoneEntry {

    ITickableZone createTickableZone();
}