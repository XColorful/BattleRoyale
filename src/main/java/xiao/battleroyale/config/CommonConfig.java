package xiao.battleroyale.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xiao.battleroyale.config.common.ItemConfig;
import xiao.battleroyale.config.common.VehicleConfig;

public class CommonConfig {
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        VehicleConfig.init(builder);
        ItemConfig.init(builder);
        return builder.build();
    }
}
