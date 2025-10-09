package xiao.battleroyale.util;

import com.google.gson.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    public static boolean writeJsonToFile(String filePath, JsonArray jsonArray) {
        Path path = Paths.get(filePath);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                BattleRoyale.LOGGER.warn("Failed to create default config directory: {}", e.getMessage());
                return false;
            }
        }
        try {
            Files.writeString(path, GSON.toJson(jsonArray));
            BattleRoyale.LOGGER.debug("Write json to file: {}", path);
            return true;
        } catch (IOException e) {
            BattleRoyale.LOGGER.warn("Failed to write json to file: {}", e.getMessage());
            return false;
        }
    }

    @NotNull
    public static List<Vec3> readVec3ListFromJson(@Nullable JsonArray jsonArray) {
        List<Vec3> vec3List = new ArrayList<>();
        if (jsonArray == null) {
            return vec3List;
        }

        for (JsonElement element : jsonArray) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (!jsonPrimitive.isString()) {
                continue;
            }
            String vec3String = jsonPrimitive.getAsString();
            Vec3 v = StringUtils.parseVectorString(vec3String);
            if (v != null) {
                vec3List.add(v);
            }
        }

        return vec3List;
    }

    @NotNull
    public static List<Integer> readIntListFromJson(@Nullable JsonArray jsonArray) {
        List<Integer> intList = new ArrayList<>();
        if (jsonArray == null) {
            return intList;
        }

        for (JsonElement element : jsonArray) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (!jsonPrimitive.isNumber()) {
                continue;
            }
            int x = (int) jsonPrimitive.getAsDouble();
            intList.add(x);
        }

        return intList;
    }

    @NotNull
    public static List<Float> readFloatListFromJson(@Nullable JsonArray jsonArray) {
        List<Float> floatList = new ArrayList<>();
        if (jsonArray == null) {
            return floatList;
        }

        for (JsonElement element : jsonArray) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (!jsonPrimitive.isNumber()) {
                continue;
            }
            float x = jsonPrimitive.getAsFloat();
            floatList.add(x);
        }

        return floatList;
    }

    @NotNull
    public static List<String> readStringListFromJson(@Nullable JsonArray jsonArray) {
        List<String> stringList = new ArrayList<>();
        if (jsonArray == null) {
            return stringList;
        }

        for (JsonElement element : jsonArray) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (!jsonPrimitive.isString()) {
                continue;
            }
            String str = jsonPrimitive.getAsString();
            stringList.add(str);
        }

        return stringList;
    }

    @NotNull
    public static Map<UUID, String> readUUIDStringFromJson(@Nullable JsonObject jsonObject) {
        Map<UUID, String> UUIDString = new HashMap<>();
        if (jsonObject == null) {
            return UUIDString;
        }

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement valueElement = entry.getValue();
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                BattleRoyale.LOGGER.warn("Skipped invalid UUID key {}", key);
                continue;
            }
            if (valueElement == null || !valueElement.isJsonPrimitive()) {
                continue;
            }
            JsonPrimitive jsonPrimitive = valueElement.getAsJsonPrimitive();
            if (!jsonPrimitive.isString()) {
                continue;
            }
            String value = jsonPrimitive.getAsString();
            UUIDString.put(uuid, value);
        }

        return UUIDString;
    }

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

    @NotNull
    public static JsonArray writeVec3ListToJson(List<Vec3> vec3List) {
        JsonArray jsonArray = new JsonArray();

        for (Vec3 v : vec3List) {
            jsonArray.add(StringUtils.vectorToString(v));
        }

        return jsonArray;
    }

    @NotNull
    public static JsonArray writeIntListToJson(List<Integer> intList) {
        JsonArray jsonArray = new JsonArray();

        for (Integer x : intList) {
            jsonArray.add(x);
        }

        return jsonArray;
    }

    @NotNull
    public static JsonArray writeFloatListToJson(List<Float> floatList) {
        JsonArray jsonArray = new JsonArray();

        for (Float x : floatList) {
            jsonArray.add(x);
        }

        return jsonArray;
    }

    @NotNull
    public static JsonArray writeStringListToJson(List<String> stringList) {
        JsonArray jsonArray = new JsonArray();

        for (String str : stringList) {
            jsonArray.add(str);
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
     * 从 JsonObject 中安全地获取一个 Integer 对象。
     * 如果键不存在、值为 null、或值不是一个可解析为整数的数字类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 Integer 值或默认值。
     */
    @Nullable
    public static Integer getJsonInteger(@Nullable JsonObject jsonObject, String key, @Nullable Integer defaultValue) {
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
    public static boolean getJsonBool(@Nullable JsonObject jsonObject, String key, boolean defaultValue) {
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
     * 从 JsonObject 中安全地获取一个 Boolean 对象。
     * 如果键不存在、值为 null、或值不是一个可解析为布尔类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 Boolean 值或默认值。
     */
    @Nullable
    public static Boolean getJsonBoolean(@Nullable JsonObject jsonObject, String key, @Nullable Boolean defaultValue) {
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
     * 从 JsonObject 中安全地获取一个 Double 对象。
     * 如果键不存在、值为 null、或值不是一个可解析为数字的类型，则返回默认值。
     *
     * @param jsonObject 要从中获取值的 JsonObject。
     * @param key        要获取的键名。
     * @param defaultValue 如果获取失败，则返回的默认值。
     * @return 解析后的 Double 值或默认值。
     */
    @Nullable
    public static Double getJsonDoubleClass(@Nullable JsonObject jsonObject, String key, @Nullable Double defaultValue) {
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

    public static CompoundTag getJsonNBT(@Nullable JsonObject jsonObject, String key) {
        String nbtString = getJsonString(jsonObject, key, "{}");
        return NBTUtils.stringToNBT(nbtString);
    }

    public static Vec3 getJsonVec(@Nullable JsonObject jsonObject, String key, Vec3 defaultValue) {
        String vecString = getJsonString(jsonObject, key, null);
        Vec3 vec = StringUtils.parseVectorString(vecString);
        return vec != null ? vec : defaultValue;
    }

    public static Component getJsonComponent(@Nullable JsonObject jsonObject, String key, Component defaultValue) {
        String componentString = getJsonString(jsonObject, key, null);
        Component component = StringUtils.parseComponentString(componentString);
        return component != null ? component : defaultValue;
    }

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
    public static List<Vec3> getJsonVecList(@Nullable JsonObject jsonObject, String key) {
        return readVec3ListFromJson(JsonUtils.getJsonArray(jsonObject, key, null));
    }

    @NotNull
    public static List<Integer> getJsonIntList(@Nullable JsonObject jsonObject, String key) {
        return readIntListFromJson(JsonUtils.getJsonArray(jsonObject, key, null));
    }

    @NotNull
    public static List<Float> getJsonFloatList(@Nullable JsonObject jsonObject, String key) {
        return readFloatListFromJson(JsonUtils.getJsonArray(jsonObject, key, null));
    }

    @NotNull
    public static List<String> getJsonStringList(@Nullable JsonObject jsonObject, String key) {
        return readStringListFromJson(JsonUtils.getJsonArray(jsonObject, key, null));
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