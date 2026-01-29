package xiao.battleroyale.api.config.client;

import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.command.CommandArg;

public interface IClientConfigManager extends IConfigManager {

    default @Override String getNameKey() {
        return CommandArg.CLIENT;
    }
}
