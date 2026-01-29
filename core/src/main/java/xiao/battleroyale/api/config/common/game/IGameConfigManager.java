package xiao.battleroyale.api.config.common.game;

import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.command.CommandArg;

public interface IGameConfigManager extends IConfigManager {

    default @Override String getNameKey() {
        return CommandArg.GAME;
    }
}
