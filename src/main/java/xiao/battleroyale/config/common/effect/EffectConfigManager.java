package xiao.battleroyale.config.common.effect;

import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.ConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager;

import java.nio.file.Paths;

public class EffectConfigManager extends AbstractConfigManager {

    public static final String EFFECT_CONFIG_SUB_PATH = "effect";
    public static final String EFFECT_CONFIG_PATH = Paths.get(ConfigManager.MOD_CONFIG_PATH).resolve(EFFECT_CONFIG_SUB_PATH).toString();

    private static class EffectConfigManagerHolder {
        private static final EffectConfigManager INSTANCE = new EffectConfigManager();
    }

    public static EffectConfigManager get() { return EffectConfigManagerHolder.INSTANCE; }

    private EffectConfigManager() {
        super(CommandArg.EFFECT);
    }

    public static void init() {
        get();
        ParticleConfigManager.init();
    }
}
