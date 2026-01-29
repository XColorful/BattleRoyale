package xiao.battleroyale.api.config.client.display;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IDisplayConfigManager<T extends IDisplaySingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.DISPLAY;
    }
}