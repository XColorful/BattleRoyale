package xiao.battleroyale.api.game.spawn;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.sub.IConfigEntry;

public interface ISpawnEntry extends IConfigEntry {

    void addPreZoneId(int zoneId);

    IGameSpawner createGameSpawner();

    @Override
    @NotNull
    ISpawnEntry copy();
}
