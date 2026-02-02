package xiao.battleroyale.api.config.common.server.profile;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IProfileConfigManager<T extends IProfileSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.PROFILE;
    }
}
