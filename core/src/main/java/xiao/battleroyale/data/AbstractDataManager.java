package xiao.battleroyale.data;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public abstract class AbstractDataManager {

    public static String MOD_DATA_PATH = "battleroyale";

    protected volatile Map<String, JsonObject> filenameToJson = new ConcurrentHashMap<>();
    protected final String subPath;
    protected final String DATA_PATH;

    /**
     * 标记当前是否正在进行 IO 写入操作
     */
    private final AtomicBoolean isSaving = new AtomicBoolean(false);
    /**
     * 标记在写入期间是否有新的保存请求进来
     */
    private final AtomicBoolean pendingSave = new AtomicBoolean(false);

    /**
     * 内部维护的单线程池，确保该实例的所有 IO 操作顺序执行
     * 每个子类实例拥有独立的线程
     */
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Data-IO:" + getSubPath());
        t.setDaemon(true);
        return t;
    });

    public AbstractDataManager() {
        this(MOD_DATA_PATH);
    }
    public AbstractDataManager(String modDataPath) {
        this.subPath = getSubPath();
        this.DATA_PATH = Paths.get(modDataPath).resolve(this.subPath).toString();
    }
    public AbstractDataManager(String modDataPath, String subPath) {
        this.subPath = subPath;
        this.DATA_PATH = Paths.get(modDataPath).resolve(this.subPath).toString();
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
    @NotNull
    public List<String> getJsonStringList(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonStringList(jsonObject, key);
    }
    @NotNull
    public Map<UUID, String> getJsonUUIDStringMap(String fileName, String key) {
        JsonObject jsonObject = filenameToJson.get(fileName);
        return JsonUtils.getJsonUUIDStringMap(jsonObject, key);
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
     * 实际执行保存逻辑的方法，包含尾随检查逻辑
     */
    private void performSaveInternal() {
        // 抓取当前快照
        final Map<String, JsonObject> snapshot = new HashMap<>(this.filenameToJson);
        // 清除 pending 标记，因为将要开始写最新的快照
        pendingSave.set(false);

        CompletableFuture.runAsync(() -> {
            try {
                for (Map.Entry<String, JsonObject> entry : snapshot.entrySet()) {
                    String fileName = entry.getKey();
                    JsonObject jsonObject = entry.getValue();
                    Path filePath = Paths.get(DATA_PATH, fileName + ".json");
                    try {
                        String jsonString = JsonUtils.toJsonString(jsonObject);
                        if (!Files.exists(filePath.getParent())) {
                            Files.createDirectories(filePath.getParent());
                        }
                        Files.writeString(filePath, jsonString);
                    } catch (IOException e) {
                        BattleRoyale.LOGGER.error("Failed to write {} data to file {}: {}", subPath, filePath, e.getMessage());
                    }
                }
            } finally {
                isSaving.set(false);
                // 检查在写入期间是否又有新请求
                if (pendingSave.get()) {
                    BattleRoyale.LOGGER.debug("Previous save finished, but new changes detected. Triggering follow-up save for {}.", subPath);
                    saveData();
                } else {
                    BattleRoyale.LOGGER.debug("Asynchronous {} data write completed", subPath);
                }
            }
        }, ioExecutor).exceptionally(ex -> {
            BattleRoyale.LOGGER.error("Asynchronous {} data write failed: {}", subPath, ex.getMessage());
            isSaving.set(false);
            return null;
        });
    }

    protected void saveData() {
        if (!isSaving.compareAndSet(false, true)) {
            pendingSave.set(true);
            BattleRoyale.LOGGER.debug("Data save queued for {}: previous write still in progress.", subPath);
            return;
        }

        performSaveInternal();
    }

    /**
     * 异步删除已有json文件名并清空当前 Map
     */
    protected CompletableFuture<Void> clearDataToJson() {
        // 清空内存 Map (主线程原子操作)
        this.filenameToJson = new ConcurrentHashMap<>();
        pendingSave.set(false); // 清空操作覆盖所有保存请求

        // 使用 ioExecutor 保证删除操作与保存操作不会冲突
        return CompletableFuture.runAsync(() -> {
            isSaving.set(true);
            try {
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
                        Files.deleteIfExists(dirPath);
                        BattleRoyale.LOGGER.info("Cleared all {} data files.", subPath);
                    } catch (IOException e) {
                        BattleRoyale.LOGGER.error("Failed to walk or delete {} data directory: {}", subPath, e.getMessage());
                    }
                }
            } finally {
                isSaving.set(false);
            }
        }, ioExecutor);
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