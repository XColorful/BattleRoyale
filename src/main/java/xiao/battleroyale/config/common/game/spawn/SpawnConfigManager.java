package xiao.battleroyale.config.common.game.spawn;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.ISpawnSingleEntry;
import xiao.battleroyale.api.game.spawn.SpawnConfigTag;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.config.common.AbstractConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.spawn.defaultconfigs.DefaultSpawnConfigGenerator;
import xiao.battleroyale.config.common.game.spawn.type.SpawnEntryType;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;


public class SpawnConfigManager extends AbstractConfigManager<SpawnConfigManager.SpawnConfig> {

    private static class SpawnConfigMangerHolder {
        private static final SpawnConfigManager INSTANCE = new SpawnConfigManager();
    }

    public static SpawnConfigManager get() {
        return SpawnConfigMangerHolder.INSTANCE;
    }

    private SpawnConfigManager() {
        allConfigData.put(DEFAULT_SPAWN_CONFIG_DATA_ID, new ConfigData<>());
    }

    public static void init() {
        get().reloadSpawnConfigs();
    }

    public static final String SPAWN_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String SPAWN_CONFIG_SUB_PATH = "spawn";

    protected final int DEFAULT_SPAWN_CONFIG_DATA_ID = 0;

    public static class SpawnConfig implements ISpawnSingleEntry {
        public static final String CONFIG_TYPE = "SpawnConfig";

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

        @Override
        public IGameSpawner createGameSpawner() { return entry.createGameSpawner(); }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(SpawnConfigTag.SPAWN_ID, id);
            jsonObject.addProperty(SpawnConfigTag.SPAWN_NAME, name);
            jsonObject.addProperty(SpawnConfigTag.SPAWN_COLOR, color);
            if (entry != null) {
                jsonObject.add(SpawnConfigTag.SPAWN_ENTRY, entry.toJson());
            }
            return jsonObject;
        }

        @Override
        public int getConfigId() {
            return getId();
        }

        public static ISpawnEntry deserializeSpawnEntry(JsonObject jsonObject) {
            try {
                String type = jsonObject.has(SpawnTypeTag.TYPE_NAME) ? jsonObject.getAsJsonPrimitive(SpawnTypeTag.TYPE_NAME).getAsString() : "";
                if (type == null) type = "";
                SpawnEntryType spawnEntryType = SpawnEntryType.fromNames(type);
                if (spawnEntryType != null) {
                    return spawnEntryType.getDeserializer().apply(jsonObject);
                } else {
                    BattleRoyale.LOGGER.warn("Skipped unknown spawn entry type: {}", type);
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize SpawnEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<SpawnConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(SpawnConfig::getId);
    }

    /**
     * IConfigManager
     */
    @Override public String getConfigType(int configType) {
        return SpawnConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_SPAWN_CONFIG_DATA_ID);
    }

    @Override public void generateDefaultConfigs(int configType) {
        DefaultSpawnConfigGenerator.generateDefaultSpawnConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_SPAWN_CONFIG_DATA_ID);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public SpawnConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        try {
            int id = configObject.has(SpawnConfigTag.SPAWN_ID) ? configObject.getAsJsonPrimitive(SpawnConfigTag.SPAWN_ID).getAsInt() : -1;
            JsonObject spawnEntryObject = configObject.has(SpawnConfigTag.SPAWN_ENTRY) ? configObject.getAsJsonObject(SpawnConfigTag.SPAWN_ENTRY) : null;
            if (id < 0 || spawnEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid spawn config in {}", filePath);
                return null;
            }

            String name = configObject.has(SpawnConfigTag.SPAWN_NAME) ? configObject.getAsJsonPrimitive(SpawnConfigTag.SPAWN_NAME).getAsString() : "";
            String color = configObject.has(SpawnConfigTag.SPAWN_COLOR) ? configObject.getAsJsonPrimitive(SpawnConfigTag.SPAWN_COLOR).getAsString() : "#FFFFFF";
            ISpawnEntry spawnEntry = SpawnConfig.deserializeSpawnEntry(spawnEntryObject);
            if (spawnEntry == null) {
                BattleRoyale.LOGGER.warn("Failed to deserialize spawn entry for id: {} in {}", id, filePath);
                return null;
            }

            return new SpawnConfig(id, name, color, spawnEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getConfigType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int configType) {
        return SPAWN_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int configType) {
        return SPAWN_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public SpawnConfig getSpawnConfig(int id) {
        return getConfigEntry(id, DEFAULT_SPAWN_CONFIG_DATA_ID);
    }
    public List<SpawnConfig> getAllSpawnConfigs() {
        return getAllConfigEntries(DEFAULT_SPAWN_CONFIG_DATA_ID);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadSpawnConfigs() {
        reloadConfigs(DEFAULT_SPAWN_CONFIG_DATA_ID);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_SPAWN_CONFIG_DATA_ID);
    }
}