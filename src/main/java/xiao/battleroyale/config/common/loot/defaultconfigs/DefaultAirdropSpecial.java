package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.AIRDROP_SPECIAL;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultAirdropSpecial{

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray specialAirdropConfigsJson = new JsonArray();
        specialAirdropConfigsJson.add(generateDefaultSpecialAirdrop());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(AIRDROP_SPECIAL), LootConfigManager.AIRDROP_SPECIAL_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), specialAirdropConfigsJson);
    }

    private static JsonObject generateDefaultSpecialAirdrop() {
        ILootEntry repeatEntry = new RepeatEntry(2, 2,
                new MultiEntry(Arrays.asList(
                        new ItemEntry("minecraft:netherite_helmet", null, 1),
                        new ItemEntry("minecraft:netherite_chestplate", null, 1),
                        new WeightEntry(Arrays.asList(
                                WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_sword", null, 1)),
                                WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_axe", null, 1))
                        )),
                        new RandomEntry(0.05, new ItemEntry("minecraft:enchanted_golden_apple", null, 1))
                ))
        );

        LootConfig lootConfig = new LootConfig(201, "Netherite Airdrop Special", "#FFFF00",
                repeatEntry);

        return lootConfig.toJson();
    }
}