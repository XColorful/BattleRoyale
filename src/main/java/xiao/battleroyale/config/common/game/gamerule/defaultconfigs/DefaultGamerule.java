package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultGamerule {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray gameruleConfigJson = new JsonArray();
        gameruleConfigJson.add(generateDefaultGameruleConfig0());
        gameruleConfigJson.add(generateDefaultGameruleConfig1());
        writeJsonToFile(Paths.get(GameConfigManager.get().getGameruleConfigPath(), DEFAULT_FILE_NAME).toString(), gameruleConfigJson);
    }

    private static JsonObject generateDefaultGameruleConfig0() {
        BattleroyaleEntry brEntry = new BattleroyaleEntry(100, 4, true, true, 12000,
                new Vec3(128, -60, 128), new Vec3(10, 10, 10),
                true, true, false, true, true);

        MinecraftEntry mcEntry = new MinecraftEntry(true, false, false,
                true, false, false,
                false, false, false,
                false, false, true,
                false, 5000);

        GameruleConfig gameruleConfig = new GameruleConfig(0, "Adventure battleroyale", "#FFFFFFAA", true,
                brEntry, mcEntry, null);

        return gameruleConfig.toJson();
    }

    private static JsonObject generateDefaultGameruleConfig1() {
        BattleroyaleEntry brEntry = new BattleroyaleEntry(100, 4, false, false, 12000,
                new Vec3(128, -60, 128), new Vec3(10, 10, 10),
                true, true, false, true, true);

        MinecraftEntry mcEntry = new MinecraftEntry(false, true, false,
                true, true, true,
                true, true, true,
                true, false, true,
                false, 5000);

        GameruleConfig gameruleConfig = new GameruleConfig(1, "Survival battleroyale", "#FFFFFFAA",
                brEntry, mcEntry, null);

        return gameruleConfig.toJson();
    }
}
