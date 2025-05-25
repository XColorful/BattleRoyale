package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootConfigTag;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultLootSpawner{

    private static final String DEFAULT_FILE_NAME = "default.json";

    public static void generateDefaultConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner1());
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner2());
        writeJsonToFile(Paths.get(LootConfigManager.COMMON_LOOT_CONFIG_PATH, LootConfigManager.LOOT_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    private static JsonObject generateDefaultLootSpawner1() {
        JsonObject config = new JsonObject();
        config.addProperty(LootConfigTag.LOOT_ID, 1);
        config.addProperty(LootConfigTag.LOOT_NAME, "Starter Gear");
        config.addProperty(LootConfigTag.LOOT_COLOR, "#A0522D");

        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:leather_helmet", null, 1),
                new ItemEntry("minecraft:leather_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:wooden_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:wooden_axe", null, 1))
                )),
                new RandomEntry(0.5, new ItemEntry("minecraft:melon_slice", null, 1))
        ));

        config.add("entry", multiEntry.toJson());
        return config;
    }

    private static JsonObject generateDefaultLootSpawner2() {
        JsonObject config = new JsonObject();
        config.addProperty(LootConfigTag.LOOT_ID, 2);
        config.addProperty(LootConfigTag.LOOT_NAME, "Basic Combat Gear");
        config.addProperty(LootConfigTag.LOOT_COLOR, "#778899");

        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:iron_helmet", null, 1),
                new ItemEntry("minecraft:iron_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:iron_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:iron_axe", null, 1))
                )),
                new RandomEntry(0.2, new ItemEntry("minecraft:cooked_beef", null, 1))
        ));

        config.add(LootConfigTag.LOOT_ENTRY, multiEntry.toJson());
        return config;
    }
}