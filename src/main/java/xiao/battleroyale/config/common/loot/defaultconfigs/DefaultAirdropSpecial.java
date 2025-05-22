package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.*;

import java.util.Arrays;

public class DefaultAirdropSpecial extends DefaultConfigHelper {

    private static final String AIRDROP_SPECIAL_CONFIG_PATH = "config/battleroyale/loot/airdrop_special/default.json";

    public static void generateDefaultConfigs() {
        JsonArray specialAirdropConfigsJson = new JsonArray();
        specialAirdropConfigsJson.add(generateDefaultSpecialAirdrop());
        writeJsonToFile(AIRDROP_SPECIAL_CONFIG_PATH, specialAirdropConfigsJson);
    }

    private static JsonObject generateDefaultSpecialAirdrop() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 201);
        config.addProperty("name", "Netherite Airdrop Special");
        config.addProperty("color", "#FFFF00");

        ILootEntry repeatEntry = new RepeatEntry(2, 2,
                new MultiEntry(Arrays.asList(
                    new ItemEntry("minecraft:netherite_helmet", null, 1),
                    new ItemEntry("minecraft:netherite_chestplate", null, 1),
                    new WeightEntry(Arrays.asList(
                            WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_sword", null, 1)),
                            WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_axe", null, 1))
                    )),
                    new RandomEntry(0.05, new ItemEntry("minecraft:enchanted_golden_apple", null, 1))
                    )
                )
        );

        config.add("entry", repeatEntry.toJson());
        return config;
    }
}