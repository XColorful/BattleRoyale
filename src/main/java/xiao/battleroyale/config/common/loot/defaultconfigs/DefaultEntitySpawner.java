package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.EntityEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;

public class DefaultEntitySpawner extends DefaultConfigHelper {

    private static final String ENTITY_SPAWNER_CONFIG_PATH = "config/battleroyale/loot/entity_spawner/default.json";

    public static void generateDefaultConfigs() {
        JsonArray entitySpawnerConfigsJson = new JsonArray();
        entitySpawnerConfigsJson.add(generateDefaultEntitySpawner());
        writeJsonToFile(ENTITY_SPAWNER_CONFIG_PATH, entitySpawnerConfigsJson);
    }

    private static JsonObject generateDefaultEntitySpawner() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 101);
        config.addProperty("name", "Horse Spawn with Probability");
        config.addProperty("color", "#A0522D"); // Changed color to brown for horse
        ILootEntry<?> randomEntry = new RandomEntry<>(0.2, new EntityEntry(new ResourceLocation("minecraft:horse"), null, 1, 1));
        config.add("entry", randomEntry.toJson());
        return config;
    }
}