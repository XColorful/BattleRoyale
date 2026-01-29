package xiao.battleroyale.api.config.common.game.gamerule;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IGameruleConfigManager<T extends IGameruleSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.GAMERULE;
    }
}
