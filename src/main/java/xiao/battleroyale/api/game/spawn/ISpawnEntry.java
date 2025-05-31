package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.IConfigEntry;

public interface ISpawnEntry extends IConfigEntry {

    IGameSpawner createGameSpawner();
}
