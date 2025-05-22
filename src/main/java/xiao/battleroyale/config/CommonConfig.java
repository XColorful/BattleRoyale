package xiao.battleroyale.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.ItemConfig;
import xiao.battleroyale.config.common.VehicleConfig;
import xiao.battleroyale.config.common.loot.LootConfigManager;

public class CommonConfig {
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        VehicleConfig.init(builder);
        ItemConfig.init(builder);
        LootConfigManager.init();
        GameManager.init();
        return builder.build();
    }
}
