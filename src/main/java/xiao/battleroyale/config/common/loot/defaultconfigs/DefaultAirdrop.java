package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.util.Arrays;
import java.util.List;

public class DefaultAirdrop extends DefaultConfigHelper {

    private static final String AIRDROP_CONFIG_PATH = "config/battleroyale/loot/airdrop/default.json";

    public static void generateDefaultConfigs() {
        JsonArray airdropConfigsJson = new JsonArray();
        airdropConfigsJson.add(generateDefaultAirdrop());
        writeJsonToFile(AIRDROP_CONFIG_PATH, airdropConfigsJson);
    }

    private static JsonObject generateDefaultAirdrop() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 201);
        config.addProperty("name", "Netherite Airdrop");
        config.addProperty("color", "#FFFF00");

        List<ILootEntry> entries = Arrays.asList(
                new ItemEntry("minecraft:netherite_helmet", null, 1),
                new ItemEntry("minecraft:netherite_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_axe", null, 1))
                )),
                new RandomEntry(0.05, new ItemEntry("minecraft:enchanted_golden_apple", null, 1))
        );
        ILootEntry multiEntry = new MultiEntry(entries);

        config.add("entry", multiEntry.toJson());
        return config;
    }
}