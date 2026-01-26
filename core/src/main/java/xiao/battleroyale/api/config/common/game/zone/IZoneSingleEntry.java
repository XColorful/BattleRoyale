package xiao.battleroyale.api.config.common.game.zone;

import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public interface IZoneSingleEntry extends IConfigSingleEntry {

    IGameZone generateZone();
}
