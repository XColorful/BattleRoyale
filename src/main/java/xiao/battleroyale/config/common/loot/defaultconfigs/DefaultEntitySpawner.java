package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootConfigTag;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.type.EntityEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultEntitySpawner {
    private static final String DEFAULT_FILE_NAME = "default.json";

    public static void generateDefaultConfigs() {
        JsonArray entitySpawnerConfigsJson = new JsonArray();
        entitySpawnerConfigsJson.add(generateDefaultEntitySpawner());
        writeJsonToFile(Paths.get(LootConfigManager.COMMON_LOOT_CONFIG_PATH, LootConfigManager.ENTITY_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), entitySpawnerConfigsJson);
    }

    private static JsonObject generateDefaultEntitySpawner() {
        JsonObject config = new JsonObject();
        config.addProperty(LootConfigTag.LOOT_ID, 101);
        config.addProperty(LootConfigTag.LOOT_NAME, "Horse Spawn with Probability");
        config.addProperty(LootConfigTag.LOOT_COLOR, "#A0522D");

        ILootEntry randomEntry = new RandomEntry(0.2, new EntityEntry("minecraft:horse", null, 1, 1));
        config.add(LootConfigTag.LOOT_ENTRY, randomEntry.toJson());
        return config;
    }
}