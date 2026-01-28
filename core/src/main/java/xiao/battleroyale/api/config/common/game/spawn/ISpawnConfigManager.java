package xiao.battleroyale.api.config.common.game.spawn;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface ISpawnConfigManager<T extends ISpawnSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.SPAWN;
    }
}
