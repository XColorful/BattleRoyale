package xiao.battleroyale.api.config.common.game.zone.func;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.game.zone.IZoneEntry;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;

public interface IZoneFuncEntry extends IZoneEntry {

    ITickableZone createTickableZone();

    @Override
    @NotNull
    IZoneFuncEntry copy();
}