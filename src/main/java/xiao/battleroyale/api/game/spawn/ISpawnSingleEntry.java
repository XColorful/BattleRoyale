package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.IConfigSingleEntry;

public interface ISpawnSingleEntry extends IConfigSingleEntry {

    IGameSpawner createGameSpawner();
}
