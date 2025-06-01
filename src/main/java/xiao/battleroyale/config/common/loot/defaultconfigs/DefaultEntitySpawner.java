package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.LootConfigTag;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.type.EntityEntry;
import xiao.battleroyale.config.common.loot.type.NoneEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultEntitySpawner {
    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray entitySpawnerConfigsJson = new JsonArray();
        entitySpawnerConfigsJson.add(generateDefaultEntitySpawner0());
        entitySpawnerConfigsJson.add(generateDefaultEntitySpawner1());
        writeJsonToFile(Paths.get(LootConfigManager.COMMON_LOOT_CONFIG_PATH, LootConfigManager.ENTITY_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), entitySpawnerConfigsJson);
    }

    private static JsonObject generateDefaultEntitySpawner0() {
        JsonObject config = new JsonObject();
        config.addProperty(LootConfigTag.LOOT_ID, 0);
        config.addProperty(LootConfigTag.LOOT_NAME, "Horse Spawn with Probability");
        config.addProperty(LootConfigTag.LOOT_COLOR, "#FFFFFFAA");

        config.add(LootConfigTag.LOOT_ENTRY,
                new RandomEntry(0.2,
                        new EntityEntry("minecraft:horse", null, 1, 1)
                )
                .toJson());

        return config;
    }

    private static JsonObject generateDefaultEntitySpawner1() {
        JsonObject config = new JsonObject();
        config.addProperty(LootConfigTag.LOOT_ID, 1);
        config.addProperty(LootConfigTag.LOOT_NAME, "Zombie Spawn with High Probability");
        config.addProperty(LootConfigTag.LOOT_COLOR, "#FFFFFFAA");

        config.add(LootConfigTag.LOOT_ENTRY,
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(0.8, new EntityEntry("minecraft:zombie", null, 5, 20)),
                        WeightEntry.createWeightedEntry(0.2, new NoneEntry())
                ))
                .toJson());

        return config;
    }
}