package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;

import java.util.Arrays;
import java.util.List;

public class DefaultSecretRoom extends DefaultConfigHelper {

    private static final String SECRET_ROOM_CONFIG_PATH = "config/battleroyale/loot/secret_room/default.json";

    public static void generateDefaultConfigs() {
        JsonArray secretRoomConfigsJson = new JsonArray();
        secretRoomConfigsJson.add(generateDefaultSecretRoom());
        writeJsonToFile(SECRET_ROOM_CONFIG_PATH, secretRoomConfigsJson);
    }

    private static JsonObject generateDefaultSecretRoom() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 401);
        config.addProperty("name", "Treasure trove");
        config.addProperty("color", "#00FFFF");

        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:iron_ingot", null, 1),
                new ItemEntry("minecraft:gold_ingot", null, 1),
                new ItemEntry("minecraft:copper_ingot", null, 1),
                new ItemEntry("minecraft:lapis_lazuli", null, 1),
                new ItemEntry("minecraft:redstone", null, 1),
                new ItemEntry("minecraft:diamond", null, 1),
                new ItemEntry("minecraft:emerald", null, 1),
                new ItemEntry("minecraft:amethyst_shard", null, 1),
                new ItemEntry("minecraft:netherite_ingot", null, 1)
        ));
        
        config.add("entry", multiEntry.toJson());
        return config;
    }
}