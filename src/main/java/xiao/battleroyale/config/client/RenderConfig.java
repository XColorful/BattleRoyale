package xiao.battleroyale.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class RenderConfig {
    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("Render");
        builder.pop();
    }
}
