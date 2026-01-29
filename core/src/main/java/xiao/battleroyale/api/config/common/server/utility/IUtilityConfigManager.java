package xiao.battleroyale.api.config.common.server.utility;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IUtilityConfigManager<T extends IUtilitySingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.UTILITY;
    }
}
