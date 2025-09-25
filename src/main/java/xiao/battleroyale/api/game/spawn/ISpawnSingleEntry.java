package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.config.IConfigSingleEntry;

public interface ISpawnSingleEntry extends IConfigSingleEntry {

    IGameSpawner createGameSpawner();
}
