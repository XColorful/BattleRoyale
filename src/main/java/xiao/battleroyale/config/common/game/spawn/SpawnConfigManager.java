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
import xiao.battleroyale.util.JsonUtils;

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
        allFolderConfigData.put(DEFAULT_SPAWN_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadSpawnConfigs();
    }

    public static final String SPAWN_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String SPAWN_CONFIG_SUB_PATH = "spawn";

    protected final int DEFAULT_SPAWN_CONFIG_FOLDER = 0;

    public record SpawnConfig(int id, String name, String color, ISpawnEntry entry) implements ISpawnSingleEntry {
        public static final String CONFIG_TYPE = "SpawnConfig";

        @Override
            public IGameSpawner createGameSpawner() {
            return entry.createGameSpawner();
        }

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
            return id();
        }

        public static ISpawnEntry deserializeSpawnEntry(JsonObject jsonObject) {
            try {
                SpawnEntryType spawnEntryType = SpawnEntryType.fromNames(JsonUtils.getJsonString(jsonObject, SpawnTypeTag.TYPE_NAME, ""));
                if (spawnEntryType != null) {
                    return spawnEntryType.getDeserializer().apply(jsonObject);
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid SpawnEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize SpawnEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<SpawnConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(SpawnConfig::id);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int configType) {
        return SpawnConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_SPAWN_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int configType) {
        DefaultSpawnConfigGenerator.generateDefaultSpawnConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_SPAWN_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public SpawnConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        try {
            int id = JsonUtils.getJsonInt(configObject, SpawnConfigTag.SPAWN_ID, -1);
            JsonObject spawnEntryObject = JsonUtils.getJsonObject(configObject, SpawnConfigTag.SPAWN_ENTRY, null);
            if (id < 0 || spawnEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid spawn config in {}", filePath);
                return null;
            }

            String name = JsonUtils.getJsonString(configObject, SpawnConfigTag.SPAWN_NAME, "");
            String color = JsonUtils.getJsonString(configObject, SpawnConfigTag.SPAWN_COLOR, "#FFFFFF");
            ISpawnEntry spawnEntry = SpawnConfig.deserializeSpawnEntry(spawnEntryObject);
            if (spawnEntry == null) {
                BattleRoyale.LOGGER.warn("Failed to deserialize spawn entry for id: {} in {}", id, filePath);
                return null;
            }

            return new SpawnConfig(id, name, color, spawnEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
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
        return getConfigEntry(id, DEFAULT_SPAWN_CONFIG_FOLDER);
    }
    public List<SpawnConfig> getSpawnConfigList() {
        return getConfigEntryList(DEFAULT_SPAWN_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadSpawnConfigs() {
        reloadConfigs(DEFAULT_SPAWN_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_SPAWN_CONFIG_FOLDER);
    }
}