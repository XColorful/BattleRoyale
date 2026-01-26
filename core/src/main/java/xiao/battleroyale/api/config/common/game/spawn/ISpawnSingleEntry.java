package xiao.battleroyale.api.config.common.game.spawn;

import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.api.game.spawn.IGameSpawner;

public interface ISpawnSingleEntry extends IConfigSingleEntry {

    IGameSpawner createGameSpawner();
}
