package xiao.battleroyale.common.game.tempdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private final Map<String, JsonObject> filenameToJson = new HashMap<>();

    public void reloadTempData() {
        this.filenameToJson.clear();
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
                                this.filenameToJson.put(fileName, element.getAsJsonObject());
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

    public void writeString(String fileName, String key, String value) {
        JsonObject jsonObject = filenameToJson.computeIfAbsent(fileName, k -> new JsonObject());
        jsonObject.addProperty(key, value);
    }

    public void writeInt(String fileName, String key, int value) {
        JsonObject jsonObject = filenameToJson.computeIfAbsent(fileName, k -> new JsonObject());
        jsonObject.addProperty(key, value);
    }

    public void writeDouble(String fileName, String key, double value) {
        JsonObject jsonObject = filenameToJson.computeIfAbsent(fileName, k -> new JsonObject());
        jsonObject.addProperty(key, value);
    }

    public void writeBool(String fileName, String key, boolean value) {
        JsonObject jsonObject = filenameToJson.computeIfAbsent(fileName, k -> new JsonObject());
        jsonObject.addProperty(key, value);
    }

    public void writeJsonObject(String fileName, String key, JsonObject jsonObject) {
        JsonObject fileJsonObject = filenameToJson.computeIfAbsent(fileName, k -> new JsonObject());
        fileJsonObject.add(key, jsonObject);
    }

    public void writeJsonArray(String fileName, String key, JsonArray jsonArray) {
        JsonObject fileJsonObject = filenameToJson.computeIfAbsent(fileName, k -> new JsonObject());
        fileJsonObject.add(key, jsonArray);
    }

    public void tempDataToJson() {
        for (Map.Entry<String, JsonObject> entry : filenameToJson.entrySet()) {
            String fileName = entry.getKey();
            JsonObject jsonObject = entry.getValue();
            Path filePath = Paths.get(TEMP_DATA_PATH, fileName + ".json");
            try {
                // 使用 Gson 将 JsonObject 转换为格式化后的 JSON 字符串
                String jsonString = JsonUtils.toJson(jsonObject);
                // 确保父目录存在
                if (!Files.exists(filePath.getParent())) {
                    Files.createDirectories(filePath.getParent());
                }
                // 将字符串写入文件
                Files.writeString(filePath, jsonString);
                BattleRoyale.LOGGER.debug("Wrote temporary data to file: {}", filePath);
            } catch (IOException e) {
                BattleRoyale.LOGGER.error("Failed to write temporary data to file {}: {}", filePath, e.getMessage());
            }
        }
    }

    // 非实际GameManager，以下方法均留空

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        ;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
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