package xiao.battleroyale.api.config.common.game.bot;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IBotConfigManager<T extends IBotSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.BOT;
    }
}
