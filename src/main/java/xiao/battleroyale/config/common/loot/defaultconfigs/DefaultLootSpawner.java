package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.util.Arrays;

public class DefaultLootSpawner extends DefaultConfigHelper {

    private static final String LOOT_SPAWNER_CONFIG_PATH = "config/battleroyale/loot/loot_spawner/default.json";

    public static void generateDefaultConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner1());
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner2());
        writeJsonToFile(LOOT_SPAWNER_CONFIG_PATH, lootSpawnerConfigsJson);
    }

    private static JsonObject generateDefaultLootSpawner1() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 1);
        config.addProperty("name", "Starter Gear");
        config.addProperty("color", "#A0522D");

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
        config.addProperty("id", 2);
        config.addProperty("name", "Basic Combat Gear");
        config.addProperty("color", "#778899");

        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:iron_helmet", null, 1),
                new ItemEntry("minecraft:iron_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:iron_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:iron_axe", null, 1))
                )),
                new RandomEntry(0.2, new ItemEntry("minecraft:cooked_beef", null, 1))
        ));

        config.add("entry", multiEntry.toJson());
        return config;
    }
}