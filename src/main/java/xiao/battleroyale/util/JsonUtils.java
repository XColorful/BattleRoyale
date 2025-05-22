package xiao.battleroyale.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.LootEntryType;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return GSON.fromJson(jsonString, clazz);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize JSON to class {}: {}", clazz.getName(), e.getMessage());
            return null;
        }
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static ILootEntry deserializeLootEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.get("type").getAsString();
            LootEntryType lootEntryType = LootEntryType.fromName(type);
            if (lootEntryType != null) {
                return lootEntryType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.error("Unknown LootEntry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize LootEntry: {}", e.getMessage());
            return null;
        }
    }
}