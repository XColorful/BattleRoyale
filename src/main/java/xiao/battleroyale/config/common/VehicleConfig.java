package xiao.battleroyale.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class VehicleConfig {
    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("Vehicle");
        builder.pop();
    }
}
