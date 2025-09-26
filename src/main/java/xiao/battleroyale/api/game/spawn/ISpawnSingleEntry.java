package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.config.sub.IConfigSingleEntry;

public interface ISpawnSingleEntry extends IConfigSingleEntry {

    IGameSpawner createGameSpawner();
}
