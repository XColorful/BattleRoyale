package xiao.battleroyale.api.config.common.game.stats;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IStatsConfigManager<T extends IStatsSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.STATS;
    }
}
