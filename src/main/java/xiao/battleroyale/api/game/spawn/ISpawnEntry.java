package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.config.sub.IConfigEntry;

public interface ISpawnEntry extends IConfigEntry {

    void addPreZoneId(int zoneId);

    IGameSpawner createGameSpawner();
}
