package xiao.battleroyale.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.server.ServerConfigManager;

public class CommonConfig {
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        LootConfigManager.init();
        EffectConfigManager.init();
        GameConfigManager.init(); GameManager.init();
        ServerConfigManager.init();

        return builder.build();
    }
}