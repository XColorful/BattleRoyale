package xiao.battleroyale.api.game.zone;

import xiao.battleroyale.api.IConfigSingleEntry;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public interface IZoneSingleEntry extends IConfigSingleEntry {

    IGameZone generateZone();
}
