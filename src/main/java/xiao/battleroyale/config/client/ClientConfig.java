package xiao.battleroyale.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        ClientConfigManager.init();

        return builder.build();
    }
}