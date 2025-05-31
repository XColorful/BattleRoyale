package xiao.battleroyale.config.common.game.spawn;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigManager;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.SpawnConfigTag;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.spawn.defaultconfigs.DefaultSpawnConfigGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class SpawnConfigManager implements IConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    public static final String SPAWN_CONFIG_SUB_PATH = "spawn";

    private final Map<Integer, SpawnConfig> spawnConfigs = new HashMap<>();
    private final List<SpawnConfig> allSpawnConfigs = new ArrayList<>();

    private static SpawnConfigManager instance;

    private SpawnConfigManager() {
    }

    public void reloadConfigs() {
        loadSpawnConfigs();

        initializeDefaultConfigsIfEmpty();
    }

    public static void init() {
        if (instance == null) {
            instance = new SpawnConfigManager();
            instance.reloadConfigs();
        }
    }

    public static SpawnConfigManager get() {
        if (instance == null) {
            SpawnConfigManager.init();
        }
        return instance;
    }

    public SpawnConfig getSpawnConfig(int id) {
        return spawnConfigs.get(id);
    }

    public List<SpawnConfig> getAllSpawnConfigs() {
        return allSpawnConfigs;
    }

    public void loadSpawnConfigs() {
        spawnConfigs.clear();
        allSpawnConfigs.clear();
        loadConfigsFromDirectory(Paths.get(GameConfigManager.GAME_CONFIG_PATH, SPAWN_CONFIG_SUB_PATH), spawnConfigs, allSpawnConfigs);
        allSpawnConfigs.sort(Comparator.comparingInt(SpawnConfig::getId));
    }

    private void loadConfigsFromDirectory(Path directoryPath, Map<Integer, SpawnConfig> configMap, List<SpawnConfig> configList) {
        try (Stream<Path> pathStream = Files.list(directoryPath)) {
            pathStream.filter(path -> path.toString().endsWith(".json"))
                    .forEach(filePath -> loadConfigFromFile(filePath, configMap, configList));
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {}", directoryPath, e);
        }
    }

    private void loadConfigFromFile(Path filePath, Map<Integer, SpawnConfig> configMap, List<SpawnConfig> configList) {
        try (InputStream inputStream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            JsonArray configArray = gson.fromJson(reader, JsonArray.class);
            if (configArray == null) {
                BattleRoyale.LOGGER.info("Skipped empty configuration from {}", filePath);
                return;
            }
            for (JsonElement element : configArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject configObject = element.getAsJsonObject();
                try {
                    int id = configObject.has(SpawnConfigTag.SPAWN_ID) ? configObject.getAsJsonPrimitive(SpawnConfigTag.SPAWN_ID).getAsInt() : -1;
                    JsonObject spawnEntryObject = configObject.has(SpawnConfigTag.SPAWN_ENTRY) ? configObject.getAsJsonObject(SpawnConfigTag.SPAWN_ENTRY) : null;
                    if (id < 0 || spawnEntryObject == null) {
                        BattleRoyale.LOGGER.warn("Skipped invalid spawn config in {}", filePath);
                        continue;
                    }
                    String name = configObject.has(SpawnConfigTag.SPAWN_NAME) ? configObject.getAsJsonPrimitive(SpawnConfigTag.SPAWN_NAME).getAsString() : "";
                    String color = configObject.has(SpawnConfigTag.SPAWN_COLOR) ? configObject.getAsJsonPrimitive(SpawnConfigTag.SPAWN_COLOR).getAsString() : "#FFFFFF";
                    ISpawnEntry spawnEntry = JsonUtils.deserializeSpawnEntry(spawnEntryObject);
                    if (spawnEntry == null) {
                        BattleRoyale.LOGGER.warn("Failed to deserialize spawn entry for id: {} in {}", id, filePath);
                        continue;
                    }
                    SpawnConfig spawnConfig = new SpawnConfig(id, name, color, spawnEntry);
                    configMap.put(id, spawnConfig);
                    configList.add(spawnConfig);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.error("Error parsing spawn config entry in {}: {}", filePath, e.getMessage());
                }
            }
            BattleRoyale.LOGGER.info("{} spawn configurations already loaded from {}.", configList.size(), filePath);
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load configuration from {}: {}", filePath, e.getMessage());
        }
    }

    public void initializeDefaultConfigsIfEmpty() {
        if (!allSpawnConfigs.isEmpty()) {
            return;
        }
        BattleRoyale.LOGGER.info("No spawn configurations loaded");
        Path spawnConfigPath = Paths.get(GameConfigManager.GAME_CONFIG_PATH, SPAWN_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(spawnConfigPath) || Files.list(spawnConfigPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No spawn configurations found in {}, generating default", spawnConfigPath);
            }
            DefaultSpawnConfigGenerator.generateDefaultSpawnConfigs();
            loadSpawnConfigs();
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for spawn configurations: {}", e.getMessage());
        }
    }

    public static class SpawnConfig {
        private final int id;
        private final String name;
        private final String color;
        private final ISpawnEntry entry;

        public SpawnConfig(int id, String name, String color, ISpawnEntry entry) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.entry = entry;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public ISpawnEntry getEntry() {
            return entry;
        }

        public IGameSpawner createGameSpawner() { return entry.createGameSpawner(); }
    }
}