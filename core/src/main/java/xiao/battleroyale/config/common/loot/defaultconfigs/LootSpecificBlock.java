package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class LootSpecificBlock {

    private static final String DEFAULT_FILE_NAME = "example_specific_block.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateMultiRegexSelector());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    private static JsonObject generateMultiRegexSelector() {
        ILootEntry abortEntry = new AbortEntry(1, 1, true,
                Arrays.asList(
                        new RegexEntry(false, "id:\"minecraft:chest\"", new MultiEntry(Arrays.asList(
                                new EmptyEntry(EmptyEntry.TYPE_ITEM),
                                generateChestMessage()
                        ))),
                        new RegexEntry(false, "id:\"minecraft:barrel\"", new MultiEntry(Arrays.asList(
                                new EmptyEntry(EmptyEntry.TYPE_ITEM),
                                generateBarrelMessage()
                        ))),
                        new RegexEntry(false, "id:\"battleroyale:loot_spawner\"", new MultiEntry(Arrays.asList(
                                new EmptyEntry(EmptyEntry.TYPE_ITEM),
                                generateLootSpawnerMessage()
                        ))),
                        new RegexEntry(false, "", generateNothing())
        ));

        LootConfig lootConfig = new LootConfig(0, "Loot specific block example", "#FFFFFFAA",
                abortEntry);

        return lootConfig.toJson();
    }

    private static ILootEntry generateChestMessage() {
        return new MessageEntry(false, true, "Detected: Chest", "#FFFF00");
    }

    private static ILootEntry generateBarrelMessage() {
        return new MessageEntry(false, true, "Detected: Barrel", "#FFA500");
    }

    private static ILootEntry generateLootSpawnerMessage() {
        return new MessageEntry(false, true, "Detected: Loot spawner", "#00FF00");
    }

    private static ILootEntry generateNothing() {
        return new NoneEntry();
    }
}
