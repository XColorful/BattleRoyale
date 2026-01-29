package xiao.battleroyale.api.config.common.server.performance;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IPerformanceConfigManager<T extends IPerformanceSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.PERFORMANCE;
    }
}
