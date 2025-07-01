package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.EntityEntry;
import xiao.battleroyale.config.common.loot.type.NoneEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.ENTITY_SPAWNER;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultEntitySpawner {
    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray entitySpawnerConfigsJson = new JsonArray();
        entitySpawnerConfigsJson.add(generateDefaultEntitySpawner0());
        entitySpawnerConfigsJson.add(generateDefaultEntitySpawner1());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(ENTITY_SPAWNER), LootConfigManager.ENTITY_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), entitySpawnerConfigsJson);
    }

    private static JsonObject generateDefaultEntitySpawner0() {
        ILootEntry randomEntry = new RandomEntry(0.2,
                new EntityEntry("minecraft:horse", null, 1, 1)
        );

        LootConfig lootConfig = new LootConfig(0, "Horse Spawn with Probability", "#FFFFFFAA", true,
                randomEntry);

        return lootConfig.toJson();
    }

    private static JsonObject generateDefaultEntitySpawner1() {
        ILootEntry weightEntry = new WeightEntry(Arrays.asList(
                WeightEntry.createWeightedEntry(0.8, new EntityEntry("minecraft:zombie", null, 5, 20)),
                WeightEntry.createWeightedEntry(0.2, new NoneEntry())
        ));

        LootConfig lootConfig = new LootConfig(1, "Zombie Spawn with High Probability", "#FFFFFFAA",
                weightEntry);

        return lootConfig.toJson();
    }
}