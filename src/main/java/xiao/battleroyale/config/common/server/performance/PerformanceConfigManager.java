package xiao.battleroyale.config.common.server.performance;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.server.performance.IPerformanceSingleEntry;
import xiao.battleroyale.api.server.performance.PerformanceConfigTag;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.performance.defaultconfigs.DefaultPerformanceConfigGenerator;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class PerformanceConfigManager extends AbstractConfigManager<PerformanceConfigManager.PerformanceConfig> {

    private static class PerformanceConfigManagerHolder {
        private static final PerformanceConfigManager INSTANCE = new PerformanceConfigManager();
    }

    public static PerformanceConfigManager get() {
        return PerformanceConfigManagerHolder.INSTANCE;
    }

    private PerformanceConfigManager() {
        allFolderConfigData.put(DEFAULT_PERFORMANCE_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadPerformanceConfigs();
    }

    public static final String PERFORMANCE_CONFIG_PATH = ServerConfigManager.SERVER_CONFIG_PATH;
    public static final String PERFORMANCE_CONFIG_SUB_PATH = "performance";

    protected final int DEFAULT_PERFORMANCE_CONFIG_FOLDER = 0;

    public static class PerformanceConfig extends AbstractSingleConfig implements IPerformanceSingleEntry {
        public static final String CONFIG_TYPE = "PerformanceConfig";

        public final GeneratorEntry generatorEntry;

        public PerformanceConfig(int id, String name, String color, GeneratorEntry generatorEntry) {
            this(id, name, color, false, generatorEntry);
        }

        public PerformanceConfig(int id, String name, String color, boolean isDefault, GeneratorEntry generatorEntry) {
            super(id, name, color, isDefault);
            this.generatorEntry = generatorEntry;
        }

        @Override
        public GeneratorEntry getGeneratorEntry() {
            return generatorEntry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(PerformanceConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(PerformanceConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(PerformanceConfigTag.NAME, name);
            jsonObject.addProperty(PerformanceConfigTag.COLOR, color);
            if (generatorEntry != null) {
                jsonObject.add(PerformanceConfigTag.GENERATOR_ENTRY, generatorEntry.toJson());
            }

            return jsonObject;
        }

        public static GeneratorEntry deserializeGeneratorEntry(JsonObject jsonObject) {
            try {
                GeneratorEntry generatorEntry = GeneratorEntry.fromJson(jsonObject);
                if (generatorEntry != null) {
                    return generatorEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid GeneratorEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize GeneratorEntry: {}", e.getMessage());
                return null;
            }
        }

        @Override
        public void applyDefault() {
            this.generatorEntry.applyDefault();
        }
    }

    @Override protected Comparator<PerformanceConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(PerformanceConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return PerformanceConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_PERFORMANCE_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int folderId) {
        DefaultPerformanceConfigGenerator.generateDefaultPerformanceConfig();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_PERFORMANCE_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public PerformanceConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, PerformanceConfigTag.ID, -1);
            JsonObject generatorEntryObject = JsonUtils.getJsonObject(configObject, PerformanceConfigTag.GENERATOR_ENTRY, null);
            if (id < 0 || generatorEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid performance config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, PerformanceConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, PerformanceConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, PerformanceConfigTag.COLOR, "#FFFFFF");
            GeneratorEntry generatorEntry = PerformanceConfig.deserializeGeneratorEntry(generatorEntryObject);
            if (generatorEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize performance entry for id: {}, in {}", id, filePath);
                return null;
            }

            return new PerformanceConfig(id, name, color, isDefault, generatorEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return PERFORMANCE_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return PERFORMANCE_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public PerformanceConfig getPerformanceConfig(int id) {
        return getConfigEntry(id, DEFAULT_PERFORMANCE_CONFIG_FOLDER);
    }
    public List<PerformanceConfig> getPerformanceConfigList() {
        return getConfigEntryList(DEFAULT_PERFORMANCE_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新获取接口
     */
    public void reloadPerformanceConfigs() {
        reloadConfigs(DEFAULT_PERFORMANCE_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_PERFORMANCE_CONFIG_FOLDER);
    }
}