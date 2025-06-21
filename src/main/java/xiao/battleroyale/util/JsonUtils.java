package xiao.battleroyale.util;

import com.google.gson.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.config.common.loot.type.LootEntryType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
            String type = jsonObject.getAsJsonPrimitive(LootEntryTag.TYPE_NAME).getAsString();
            if (type == null) type = "";
            LootEntryType lootEntryType = LootEntryType.fromName(type);
            if (lootEntryType != null) {
                return lootEntryType.getDeserializer().apply(jsonObject);
            } else {
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize LootEntry: {}", e.getMessage());
            return null;
        }
    }

    public static void writeJsonToFile(String filePath, JsonArray jsonArray) {
        Path path = Paths.get(filePath);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                BattleRoyale.LOGGER.warn("Failed to create default config directory: {}", e.getMessage());
                return;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(path, gson.toJson(jsonArray));
            BattleRoyale.LOGGER.debug("Write json to file: {}", path);
        } catch (IOException e) {
            BattleRoyale.LOGGER.warn("Failed to write json to file: {}", e.getMessage());
        }
    }

    public static List<Vec3> readVec3ListFromJson(@Nullable JsonArray jsonArray) {
        List<Vec3> vec3List = new ArrayList<>();
        if (jsonArray == null) {
            return vec3List;
        }

        try {
            for (JsonElement element : jsonArray) {
                String vec3String = element.getAsJsonPrimitive().getAsString();
                Vec3 v = StringUtils.parseVectorString(vec3String);
                if (v != null) {
                    vec3List.add(v);
                }
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to read Vec3 list from json");
        }

        return vec3List;
    }

    public static JsonArray writeVec3ListToJson(List<Vec3> vec3List) {
        JsonArray jsonArray = new JsonArray();

        for (Vec3 v : vec3List) {
            jsonArray.add(StringUtils.vectorToString(v));
        }

        return jsonArray;
    }

    /**
     * 从 JsonObject 中安全地获取一个 int 值。
     * 如果键不存在、值为 null、或值不是一个可解析为整数的基本类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 int 值或默认值。
     */
    public static int getJsonInt(@Nullable JsonObject jsonObject, String key, int defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }

        JsonElement element = jsonObject.get(key);

        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return defaultValue;
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isNumber()) {
            return defaultValue;
        }

        try {
            return primitive.getAsInt();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从 JsonObject 中安全地获取一个 boolean 值。
     * 如果键不存在、值为 null、或值不是一个可解析为布尔类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 boolean 值或默认值。
     */
    public static boolean getJsonBoolean(@Nullable JsonObject jsonObject, String key, boolean defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }

        JsonElement element = jsonObject.get(key);

        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return defaultValue;
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isBoolean()) {
            return defaultValue;
        }

        return primitive.getAsBoolean();
    }

    /**
     * 从 JsonObject 中安全地获取一个 double 值。
     * 如果键不存在、值为 null、或值不是一个可解析为数字的基本类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 double 值或默认值。
     */
    public static double getJsonDouble(@Nullable JsonObject jsonObject, String key, double defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }

        JsonElement element = jsonObject.get(key);

        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return defaultValue;
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isNumber()) {
            return defaultValue;
        }

        try {
            return primitive.getAsDouble();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从 JsonObject 中安全地获取一个 String 值。
     * 如果键不存在、值为 null、或值不是一个可解析为字符串类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 String 值或默认值。
     */
    public static String getJsonString(@Nullable JsonObject jsonObject, String key, String defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }

        JsonElement element = jsonObject.get(key);

        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return defaultValue;
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isString()) {
            return defaultValue;
        }

        return primitive.getAsString();
    }

    public static CompoundTag getJsonNBT(@Nullable JsonObject jsonObject, String key, CompoundTag defaultValue) {
        String nbtString = getJsonString(jsonObject, key, "");
        return NBTUtils.stringToNBT(nbtString);
    }

    public static Vec3 getJsonVec(@Nullable JsonObject jsonObject, String key, Vec3 defaultValue) {
        String vecString = getJsonString(jsonObject, key, null);
        Vec3 vec = StringUtils.parseVectorString(vecString);
        return vec != null ? vec : defaultValue;
    }

    @NotNull
    public static List<Vec3> getJsonVecList(@Nullable JsonObject jsonObject, String key) {
        List<Vec3> vec3List = new ArrayList<>();
        JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, key, null);
        if (jsonArray == null) {
            return vec3List;
        }

        try {
            for (JsonElement element : jsonArray) {
                String vec3String = element.getAsJsonPrimitive().getAsString();
                Vec3 v = StringUtils.parseVectorString(vec3String);
                if (v != null) {
                    vec3List.add(v);
                }
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to read Vec3 list from json");
        }

        return vec3List;
    }

    /**
     * 从 JsonObject 中安全地获取一个 JsonArray。
     * 如果键不存在、值为 null、或值不是一个 JsonArray，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认 JsonArray。通常是一个新的空 JsonArray。
     * @return 解析后的 JsonArray 或默认值。
     */
    public static JsonArray getJsonArray(@Nullable JsonObject jsonObject, String key, JsonArray defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }
        JsonElement element = jsonObject.get(key);
        if (element == null || element.isJsonNull() || !element.isJsonArray()) {
            return defaultValue;
        }
        return element.getAsJsonArray();
    }

    /**
     * 从 JsonObject 中安全地获取一个 JsonObject。
     * 如果键不存在、值为 null、或值不是一个 JsonObject，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认 JsonObject。通常是一个新的空 JsonObject。
     * @return 解析后的 JsonObject 或默认值。
     */
    public static JsonObject getJsonObject(@Nullable JsonObject jsonObject, String key, JsonObject defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }
        JsonElement element = jsonObject.get(key);
        if (element == null || element.isJsonNull() || !element.isJsonObject()) {
            return defaultValue;
        }
        return element.getAsJsonObject();
    }
}