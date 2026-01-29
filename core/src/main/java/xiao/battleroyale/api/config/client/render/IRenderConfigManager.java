package xiao.battleroyale.api.config.client.render;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IRenderConfigManager<T extends IRenderSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.RENDER;
    }
}
