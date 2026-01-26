package xiao.battleroyale.api.config.common.game.spawn;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.sub.IConfigEntry;
import xiao.battleroyale.api.game.spawn.IGameSpawner;

public interface ISpawnEntry extends IConfigEntry {

    void addPreZoneId(int zoneId);

    IGameSpawner createGameSpawner();

    @Override
    @NotNull
    ISpawnEntry copy();
}
