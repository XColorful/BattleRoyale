package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.LOOT_SPAWNER;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultLootSpawner{

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner0());
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner1());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(LOOT_SPAWNER), LootConfigManager.LOOT_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    private static JsonObject generateDefaultLootSpawner0() {
        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:leather_helmet", "{Damage:27}", 1),
                new ItemEntry("minecraft:leather_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:wooden_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:wooden_axe", null, 1)),
                        WeightEntry.createWeightedEntry(20, new EmptyEntry())
                )),
                new RandomEntry(0.5, new ItemEntry("minecraft:melon_slice", null, 1)),
                new RepeatEntry(0, 5, new ItemEntry("minecraft:grass", null, 5)),
                new TimeEntry(200, 12000, new ItemEntry("minecraft:dirt", null, 1))
        ));

        LootConfig lootConfig = new LootConfig(0, "All entry type example", "#FFFFFFAA",
                multiEntry);

        return lootConfig.toJson();
    }

    private static JsonObject generateDefaultLootSpawner1() {
        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:iron_helmet", null, 1),
                new ItemEntry("minecraft:iron_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:iron_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:iron_axe", null, 1))
                )),
                new RandomEntry(0.2, new ItemEntry("minecraft:cooked_beef", null, 1))
        ));

        LootConfig lootConfig = new LootConfig(1, "Basic Combat Gear", "#FFFFFFAA",
                multiEntry);

        return lootConfig.toJson();
    }
}