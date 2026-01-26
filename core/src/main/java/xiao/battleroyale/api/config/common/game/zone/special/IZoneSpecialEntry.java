package xiao.battleroyale.api.config.common.game.zone.special;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.game.zone.IZoneEntry;
import xiao.battleroyale.api.game.zone.gamezone.IAdditionalZone;
import xiao.battleroyale.config.common.game.zone.zonespecial.ZoneSpecialType;

public interface IZoneSpecialEntry extends IZoneEntry {

    ZoneSpecialType getZoneSpecialType();

    IAdditionalZone createAdditionalZone();

    @Override
    @NotNull
    IZoneSpecialEntry copy();
}
