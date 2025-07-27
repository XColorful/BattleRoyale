package xiao.battleroyale.data;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public abstract class AbstractDataManager {

    public static String MOD_DATA_PATH = "battleroyale";

    protected volatile Map<String, JsonObject> filenameToJson = new ConcurrentHashMap<>();
    protected final String subPath;
    protected final String DATA_PATH;

    public AbstractDataManager() {
        this.subPath = getSubPath();
        this.DATA_PATH = Paths.get(MOD_DATA_PATH).resolve(this.subPath).toString();
    }

    protected abstract String getSubPath();

    /**
     * 重新加载目录下所有Json
     */
    protected void reloadData() {
        // 避免在加载期间影响旧 Map
        Map<String, JsonObject> newFilenameToJson = new ConcurrentHashMap<>();
        Path dirPath = Paths.get(DATA_PATH);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
                BattleRoyale.LOGGER.info("Created {} data directory: {}", subPath, dirPath);
            } catch (IOException e) {
                BattleRoyale.LOGGER.error("Failed to create {} data directory: {}", subPath, dirPath);
                return;
            }
        }

        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString().replace(".json", "");
                        try (InputStream inputStream = Files.newInputStream(path);
                             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                            JsonElement element = JsonParser.parseReader(reader);
                            if (element.isJsonObject()) {
                                newFilenameToJson.put(fileName, element.getAsJsonObject());
                                BattleRoyale.LOGGER.debug("Loaded {} data from file: {}", subPath, path);
                            } else {
                                BattleRoyale.LOGGER.warn("Skipped non jsonObject file {}", path);
                            }
                        } catch (Exception e) {
                            BattleRoyale.LOGGER.error("Failed to read or parse json file {}: {}", path, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to walk {} data directory: {}", subPath, e.getMessage());
        }
        // 原子替换引用
        this.filenameToJson = newFilenameToJson;
    }

    @Nullable
    public String getString(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonString(jsonObject, key, null);
    }
    @Nullable
    public Integer getInt(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonInteger(jsonObject, key, null);
    }
    @Nullable
    public Double getDouble(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonDoubleClass(jsonObject, key, null);
    }
    @Nullable
    public Boolean getBool(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonBoolean(jsonObject, key, null);
    }
    @Nullable
    public JsonObject getJsonObject(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonObject(jsonObject, key, null);
    }
    @Nullable
    public JsonArray getJsonArray(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonArray(jsonObject, key, null);
    }

    /**
     * 辅助方法
     * 创建一个新的 JsonObject 并复制旧属性，然后添加新属性
     * 确保修改是针对一个新对象进行的，避免并发修改问题
     */
    protected JsonObject createNewJsonObjectWithProperty(JsonObject original, String key, JsonElement value) {
        JsonObject newObject = new JsonObject();
        if (original != null) {
            for (Map.Entry<String, JsonElement> entry : original.entrySet()) {
                newObject.add(entry.getKey(), entry.getValue());
            }
        }
        newObject.add(key, value);
        return newObject;
    }

    // 使用 compute 方法原子性地更新 Map 中的 JsonObject
    public void writeString(String fileName, String key, String value) {
        filenameToJson.compute(fileName, (k, oldObject) ->
                createNewJsonObjectWithProperty(oldObject, key, new JsonPrimitive(value))
        );
    }
    public void writeInt(String fileName, String key, int value) {
        filenameToJson.compute(fileName, (k, oldObject) ->
                createNewJsonObjectWithProperty(oldObject, key, new JsonPrimitive(value))
        );
    }
    public void writeDouble(String fileName, String key, double value) {
        filenameToJson.compute(fileName, (k, oldObject) ->
                createNewJsonObjectWithProperty(oldObject, key, new JsonPrimitive(value))
        );
    }
    public void writeBool(String fileName, String key, boolean value) {
        filenameToJson.compute(fileName, (k, oldObject) ->
                createNewJsonObjectWithProperty(oldObject, key, new JsonPrimitive(value))
        );
    }
    public void writeJsonObject(String fileName, String key, JsonObject jsonObject) {
        filenameToJson.compute(fileName, (k, oldObject) ->
                createNewJsonObjectWithProperty(oldObject, key, jsonObject)
        );
    }
    public void writeJsonArray(String fileName, String key, JsonArray jsonArray) {
        filenameToJson.compute(fileName, (k, oldObject) ->
                createNewJsonObjectWithProperty(oldObject, key, jsonArray)
        );
    }

    /**
     * 将数据异步保存到 JSON 文件
     * 获取当前 Map 的快照，并在后台线程中写入文件
     * 从而不阻塞主线程和任何读写操作
     */
    private CompletableFuture<Void> dataToJsonAsync() {
        // 获取当前 Map 的快照
        final Map<String, JsonObject> snapshot = new HashMap<>(this.filenameToJson);
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<String, JsonObject> entry : snapshot.entrySet()) {
                String fileName = entry.getKey();
                JsonObject jsonObject = entry.getValue();
                Path filePath = Paths.get(DATA_PATH, fileName + ".json");
                try {
                    String jsonString = JsonUtils.toJson(jsonObject);
                    if (!Files.exists(filePath.getParent())) {
                        Files.createDirectories(filePath.getParent());
                    }
                    Files.writeString(filePath, jsonString);
                    BattleRoyale.LOGGER.debug("Wrote {} data to file: {}", subPath, filePath);
                } catch (IOException e) {
                    BattleRoyale.LOGGER.error("Failed to write {} data to file {}: {}", subPath, filePath, e.getMessage());
                }
            }
        });
    }

    protected void saveData() {
        dataToJsonAsync()
                .thenRun(() -> BattleRoyale.LOGGER.debug("Asynchronous {} data write completed", subPath))
                .exceptionally(ex -> {
                    BattleRoyale.LOGGER.error("Asynchronous {} data write failed: {}", subPath, ex.getMessage());
                    return null;
                });
    }

    /**
     * 异步删除已有json文件名并清空当前 Map
     */
    protected CompletableFuture<Void> clearDataToJson() {
        return CompletableFuture.runAsync(() -> {
            // 清空内存 Map
            this.filenameToJson = new ConcurrentHashMap<>(); // 原子替换，旧 Map 变为可被 GC
            Path dirPath = Paths.get(DATA_PATH);
            if (Files.exists(dirPath)) {
                try (Stream<Path> paths = Files.walk(dirPath)) {
                    paths.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".json"))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                    BattleRoyale.LOGGER.debug("Deleted {} data file: {}", subPath, path);
                                } catch (IOException e) {
                                    BattleRoyale.LOGGER.error("Failed to delete {} data file {}: {}", subPath, path, e.getMessage());
                                }
                            });
                    // 尝试删除目录，如果为空的话
                    Files.deleteIfExists(dirPath);
                    BattleRoyale.LOGGER.info("Cleared all {} data files.", subPath);
                } catch (IOException e) {
                    BattleRoyale.LOGGER.error("Failed to walk or delete {} data directory: {}", subPath, e.getMessage());
                }
            }
        });
    }

    /**
     * 异步清除所有数据
     */
    public void clearData() {
        clearDataToJson()
                .thenRun(() -> BattleRoyale.LOGGER.debug("All {} data cleared asynchronously", subPath))
                .exceptionally(ex -> {
                    BattleRoyale.LOGGER.error("Failed to clear all {} data asynchronously: {}", subPath, ex.getMessage());
                    return null;
                });
    }
}
