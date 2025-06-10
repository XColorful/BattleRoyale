package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.gamerule.GameruleConfigTag;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
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
        JsonObject config = new JsonObject();
        config.addProperty(GameruleConfigTag.GAME_ID, 0);
        config.addProperty(GameruleConfigTag.GAME_NAME, "Adventure battleroyale");
        config.addProperty(GameruleConfigTag.GAME_COLOR, "#FFFFFFAA");

        BattleroyaleEntry brEntry = new BattleroyaleEntry(100, 4, true, true, 12000,
                new Vec3(128, -60, 128), new Vec3(10, 10, 10),
                true, false, true);

        MinecraftEntry mcEntry = new MinecraftEntry(true, false, false,
                true, false, false,
                false, false, false,
                false, false, 5000);

        config.add(GameruleConfigTag.BATTLEROYALE_ENTRY, brEntry.toJson());
        config.add(GameruleConfigTag.MINECRAFT_ENTRY, mcEntry.toJson());

        return config;
    }

    private static JsonObject generateDefaultGameruleConfig1() {
        JsonObject config = new JsonObject();
        config.addProperty(GameruleConfigTag.GAME_ID, 1);
        config.addProperty(GameruleConfigTag.GAME_NAME, "Survival battleroyale");
        config.addProperty(GameruleConfigTag.GAME_COLOR, "#FFFFFFAA");

        BattleroyaleEntry brEntry = new BattleroyaleEntry(100, 4, false, false, 12000,
                new Vec3(128, -60, 128), new Vec3(10, 10, 10),
                true, false, true);

        MinecraftEntry mcEntry = new MinecraftEntry(false, true, false,
                true, true, true,
                true, true, true,
                true, false, 5000);

        config.add(GameruleConfigTag.BATTLEROYALE_ENTRY, brEntry.toJson());
        config.add(GameruleConfigTag.MINECRAFT_ENTRY, mcEntry.toJson());

        return config;
    }
}
