package xiao.battleroyale.util;

import com.google.gson.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Nullable
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            return GSON.fromJson(jsonString, clazz);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize JSON to class {}: {}", clazz.getName(), e.getMessage());
            return null;
        }
    }

    public static String toJsonString(Object object) {
        if (object == null) {
            return "null";
        }
        return GSON.toJson(object);
    }

    public static boolean writeJsonToFile(String filePath, Object object) {
        if (object == null) return false;

        Path path = Paths.get(filePath);
        Path parent = path.getParent();

        // 确保父目录存在
        if (parent != null && Files.notExists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                BattleRoyale.LOGGER.warn("JsonUtils: Failed to create directory: {}", e.getMessage());
                return false;
            }
        }

        // 先写临时文件，再移动覆盖
        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            Files.writeString(tempPath, GSON.toJson(object));
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            BattleRoyale.LOGGER.debug("Write json to file: {}", path);
            return true;
        } catch (IOException e) {
            BattleRoyale.LOGGER.warn("JsonUtils: Failed to write json to file: {}", e.getMessage());
            return false;
        } finally {
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
        }
    }

    @NotNull
    public static <T> List<T> readListFromJson(@Nullable JsonArray jsonArray, Function<JsonElement, T> mapper) {
        List<T> list = new ArrayList<>();
        if (jsonArray == null) return list;

        for (JsonElement element : jsonArray) {
            try {
                T result = mapper.apply(element);
                if (result != null) list.add(result); // 转换失败返回 null 的自动舍弃
            } catch (Exception ignored) {} // 类型转换异常的自动舍弃
        }
        return list;
    }
    @NotNull
    public static List<Vec3> readVec3ListFromJson(@Nullable JsonArray array) {
        return readListFromJson(array, e -> e.isJsonPrimitive() ? StringUtils.parseVectorString(e.getAsString()) : null);
    }
    @NotNull
    public static List<Integer> readIntListFromJson(@Nullable JsonArray array) {
        return readListFromJson(array, e -> (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) ? e.getAsInt() : null);
    }
    @NotNull
    public static List<Float> readFloatListFromJson(@Nullable JsonArray array) {
        return readListFromJson(array, e -> (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) ? e.getAsFloat() : null);
    }
    @NotNull
    public static List<String> readStringListFromJson(@Nullable JsonArray array) {
        return readListFromJson(array, e -> (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) ? e.getAsString() : null);
    }

    @NotNull
    public static Map<UUID, String> readUUIDStringFromJson(@Nullable JsonObject jsonObject) {
        Map<UUID, String> map = new HashMap<>();
        if (jsonObject == null) return map;

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            // 利用 getValueFromJson 的思想：尝试转换 key 和 value
            try {
                UUID uuid = UUID.fromString(entry.getKey());
                JsonElement v = entry.getValue();
                if (v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
                    map.put(uuid, v.getAsString());
                }
            } catch (Exception ignored) {}
        }
        return map;
    }

    @NotNull
    public static <T> JsonArray writeListToJson(List<T> list, Function<T, JsonElement> mapper) {
        JsonArray jsonArray = new JsonArray();
        if (list == null) return jsonArray;
        for (T item : list) {
            try {
                JsonElement element = mapper.apply(item);
                if (element != null) jsonArray.add(element);
            } catch (Exception ignored) {}
        }
        return jsonArray;
    }
    @NotNull
    public static JsonArray writeVec3ListToJson(List<Vec3> list) {
        return writeListToJson(list, v -> new JsonPrimitive(StringUtils.vectorToString(v)));
    }
    @NotNull
    public static JsonArray writeIntListToJson(List<Integer> list) {
        return writeListToJson(list, JsonPrimitive::new);
    }
    @NotNull
    public static JsonArray writeFloatListToJson(List<Float> list) {
        return writeListToJson(list, JsonPrimitive::new);
    }
    @NotNull
    public static JsonArray writeStringListToJson(List<String> list) {
        return writeListToJson(list, JsonPrimitive::new);
    }

    @Deprecated
    @NotNull
    public static JsonObject writeTagToJson(CompoundTag tag) {
        JsonObject jsonObject = new JsonObject();
        if (tag == null) {
            return jsonObject;
        }

        String nbtString = NBTUtils.nbtToString(tag);
        try {
            JsonElement element = JsonParser.parseString(nbtString);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        } catch (Exception ignored) {
            ;
        }
        return jsonObject;
    }

    public static <T> T getValueFromJson(@Nullable JsonObject jsonObject, String key, T defaultValue, Function<JsonElement, T> mapper) {
        if (jsonObject == null || key == null || key.isEmpty()) return defaultValue;

        JsonElement element = jsonObject.get(key);
        if (element == null || element.isJsonNull()) return defaultValue;

        try {
            T result = mapper.apply(element);
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public static int getJsonInt(@Nullable JsonObject json, String key, int def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsInt() : null);
    }
    public static Integer getJsonInteger(@Nullable JsonObject json, String key, Integer def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsInt() : null);
    }
    public static boolean getJsonBool(@Nullable JsonObject json, String key, boolean def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean() ? e.getAsBoolean() : null);
    }
    public static Boolean getJsonBoolean(@Nullable JsonObject json, String key, Boolean def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean() ? e.getAsBoolean() : null);
    }
    public static double getJsonDouble(@Nullable JsonObject json, String key, double def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsDouble() : null);
    }
    public static Double getJsonDoubleClass(@Nullable JsonObject json, String key, Double def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsDouble() : null);
    }
    public static String getJsonString(@Nullable JsonObject json, String key, String def) {
        return getValueFromJson(json, key, def, e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isString() ? e.getAsString() : null);
    }
    public static Vec3 getJsonVec(@Nullable JsonObject json, String key, Vec3 def) {
        return getValueFromJson(json, key, def, e -> StringUtils.parseVectorString(e.getAsString()));
    }
    public static Component getJsonComponent(@Nullable JsonObject json, String key, Component def) {
        return getValueFromJson(json, key, def, e -> StringUtils.parseComponentString(e.getAsString()));
    }
    public static CompoundTag getJsonNBT(@Nullable JsonObject json, String key) {
        return getValueFromJson(json, key, new CompoundTag(), e -> NBTUtils.stringToNBT(e.getAsString()));
    }

    @Deprecated
    public static CompoundTag getJsonTag(@Nullable JsonObject jsonObject, String key, CompoundTag defaultValue) {
        if (jsonObject == null || key == null || key.isEmpty()) {
            return defaultValue;
        }

        JsonElement element = jsonObject.get(key);

        if (element == null || element.isJsonNull() || !element.isJsonObject()) {
            return defaultValue;
        }

        try {
            String jsonString = GSON.toJson(element.getAsJsonObject());
            return NBTUtils.stringToNBT(jsonString);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @NotNull
    public static List<Integer> getJsonIntList(@Nullable JsonObject json, String key) {
        return readListFromJson(getJsonArray(json, key, null), e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsInt() : null);
    }
    @NotNull
    public static List<Float> getJsonFloatList(@Nullable JsonObject json, String key) {
        return readListFromJson(getJsonArray(json, key, null), e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsFloat() : null);
    }
    @NotNull
    public static List<String> getJsonStringList(@Nullable JsonObject json, String key) {
        return readListFromJson(getJsonArray(json, key, null), e -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isString() ? e.getAsString() : null);
    }
    @NotNull
    public static List<Vec3> getJsonVecList(@Nullable JsonObject json, String key) {
        return readListFromJson(getJsonArray(json, key, null), e -> StringUtils.parseVectorString(e.getAsString()));
    }

    @NotNull
    public static Map<UUID, String> getJsonUUIDStringMap(@Nullable JsonObject jsonObject, String key) {
        return readUUIDStringFromJson(JsonUtils.getJsonObject(jsonObject, key, null));
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