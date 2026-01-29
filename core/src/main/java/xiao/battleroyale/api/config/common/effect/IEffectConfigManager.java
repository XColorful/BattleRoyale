package xiao.battleroyale.api.config.common.effect;

import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.command.CommandArg;

public interface IEffectConfigManager extends IConfigManager {

    default @Override String getNameKey() {
        return CommandArg.EFFECT;
    }
}
