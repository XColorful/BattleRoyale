package xiao.battleroyale.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xiao.battleroyale.config.client.DisplayConfig;
import xiao.battleroyale.config.client.RenderConfig;

public class ClientConfig {
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DisplayConfig.init(builder);
        RenderConfig.init(builder);
        return builder.build();
    }
}
