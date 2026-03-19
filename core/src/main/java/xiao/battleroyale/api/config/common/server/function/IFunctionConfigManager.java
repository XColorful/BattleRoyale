package xiao.battleroyale.api.config.common.server.function;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IFunctionConfigManager<T extends IFunctionSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.FUNCTION;
    }
}
