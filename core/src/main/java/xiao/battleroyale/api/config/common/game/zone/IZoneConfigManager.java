package xiao.battleroyale.api.config.common.game.zone;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IZoneConfigManager<T extends IZoneSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.ZONE;
    }
}
