package xiao.battleroyale.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xiao.battleroyale.config.client.ClientConfigManager;

public class ClientConfig {
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        ClientConfigManager.init();

        return builder.build();
    }
}