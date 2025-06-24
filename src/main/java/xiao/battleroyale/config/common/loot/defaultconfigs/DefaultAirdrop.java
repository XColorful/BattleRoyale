package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.AIRDROP;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultAirdrop{

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray airdropConfigsJson = new JsonArray();
        airdropConfigsJson.add(generateDefaultAirdrop());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(AIRDROP), LootConfigManager.AIRDROP_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), airdropConfigsJson);
    }

    private static JsonObject generateDefaultAirdrop() {
        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:netherite_helmet", null, 1),
                new ItemEntry("minecraft:netherite_chestplate", null, 1),
                new WeightEntry(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_sword", null, 1)),
                        WeightEntry.createWeightedEntry(20, new ItemEntry("minecraft:netherite_axe", null, 1))
                )),
                new RandomEntry(0.05, new ItemEntry("minecraft:enchanted_golden_apple", null, 1))
        ));

        LootConfig lootConfig = new LootConfig(201, "Netherite Airdrop", "#FFFF00",
                multiEntry);

        return lootConfig.toJson();
    }
}