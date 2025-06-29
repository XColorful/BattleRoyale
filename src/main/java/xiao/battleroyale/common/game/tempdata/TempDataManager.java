package xiao.battleroyale.common.game.tempdata;

import com.google.gson.*;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
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

public class TempDataManager extends AbstractGameManager {

    private static class TempDataManagerHolder {
        private static final TempDataManager INSTANCE = new TempDataManager();
    }

    public static TempDataManager get() {
        return TempDataManagerHolder.INSTANCE;
    }

    private TempDataManager() {
        this.reloadTempData();
    }

    public static final String TEMP_DATA_SUB_PATH = "temp";
    public static final String TEMP_DATA_PATH = Paths.get(AbstractGameManager.MOD_DATA_PATH).resolve(TEMP_DATA_SUB_PATH).toString();
    private volatile Map<String, JsonObject> filenameToJson = new ConcurrentHashMap<>();

    public void reloadTempData() {
        // 不影响旧Map
        Map<String, JsonObject> newFilenameToJson = new ConcurrentHashMap<>();
        Path dirPath = Paths.get(TEMP_DATA_PATH);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
                BattleRoyale.LOGGER.info("Created temporary data directory: {}", dirPath);
            } catch (IOException e) {
                BattleRoyale.LOGGER.error("Failed to create temporary data directory: {}", e.getMessage());
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
                                BattleRoyale.LOGGER.debug("Loaded temporary data from file: {}", path);
                            } else {
                                BattleRoyale.LOGGER.warn("File {} is not a valid JsonObject and will be ignored.", path);
                            }
                        } catch (Exception e) {
                            BattleRoyale.LOGGER.error("Failed to read or parse json file {}: {}", path, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to walk temporary data directory: {}", e.getMessage());
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
     * 辅助方法：创建一个新的 JsonObject 并复制旧属性，然后添加新属性。
     * 这确保了修改是针对一个新对象进行的，避免并发修改问题。
     */
    private JsonObject createNewJsonObjectWithProperty(JsonObject original, String key, JsonElement value) {
        JsonObject newObject = new JsonObject();
        if (original != null) {
            for (Map.Entry<String, JsonElement> entry : original.entrySet()) {
                newObject.add(entry.getKey(), entry.getValue());
            }
        }
        newObject.add(key, value);
        return newObject;
    }

    public void writeString(String fileName, String key, String value) {
        // 使用 compute 方法原子性地更新 Map 中的 JsonObject
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
     * 将临时数据异步保存到 JSON 文件。
     * 该方法会获取当前 Map 的快照，并在后台线程中进行文件写入，
     * 从而不阻塞主线程和任何读写操作。
     */
    public CompletableFuture<Void> tempDataToJsonAsync() {
        // 获取当前 Map 的快照
        final Map<String, JsonObject> snapshot = new HashMap<>(this.filenameToJson);
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<String, JsonObject> entry : snapshot.entrySet()) {
                String fileName = entry.getKey();
                JsonObject jsonObject = entry.getValue();
                Path filePath = Paths.get(TEMP_DATA_PATH, fileName + ".json");
                try {
                    String jsonString = JsonUtils.toJson(jsonObject);
                    if (!Files.exists(filePath.getParent())) {
                        Files.createDirectories(filePath.getParent());
                    }
                    Files.writeString(filePath, jsonString);
                    BattleRoyale.LOGGER.debug("Wrote temporary data to file: {}", filePath);
                } catch (IOException e) {
                    BattleRoyale.LOGGER.error("Failed to write temporary data to file {}: {}", filePath, e.getMessage());
                }
            }
        });
    }

    // 非实际GameManager，以下方法均留空

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        ;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        tempDataToJsonAsync()
                .thenRun(() -> BattleRoyale.LOGGER.debug("Asynchronous temp data write completed"))
                .exceptionally(ex -> {
                    BattleRoyale.LOGGER.error("Asynchronous temp data write failed: {}", ex.getMessage());
                    return null;
                });
        return true;
    }

    @Override
    public void onGameTick(int gameTime) {
        ;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        ;
    }
}