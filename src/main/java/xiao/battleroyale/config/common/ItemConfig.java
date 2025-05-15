package xiao.battleroyale.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemConfig {
    public static ForgeConfigSpec.IntValue DEFAULT_PARACHUTE_OPEN_HEIGHT;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("Item");
        builder.pop();
    }
}
