package xiao.battleroyale.api.config.common.server;

import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.command.CommandArg;

public interface IServerConfigManager extends IConfigManager {

    default @Override String getNameKey() {
        return CommandArg.SERVER;
    }
}
