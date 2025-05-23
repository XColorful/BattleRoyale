package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootConfigTag;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultSecretRoom{

    private static final String DEFAULT_FILE_NAME = "default.json";

    public static void generateDefaultConfigs() {
        JsonArray secretRoomConfigsJson = new JsonArray();
        secretRoomConfigsJson.add(generateDefaultSecretRoom());
        writeJsonToFile(Paths.get(LootConfigManager.COMMON_LOOT_CONFIG_PATH, LootConfigManager.SECRET_ROOM_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), secretRoomConfigsJson);
    }

    private static JsonObject generateDefaultSecretRoom() {
        JsonObject config = new JsonObject();
        config.addProperty(LootConfigTag.LOOT_ID, 401);
        config.addProperty(LootConfigTag.LOOT_NAME, "Treasure trove");
        config.addProperty(LootConfigTag.LOOT_COLOR, "#00FFFF");

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
        
        config.add(LootConfigTag.LOOT_ENTRY, multiEntry.toJson());
        return config;
    }
}