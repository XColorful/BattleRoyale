package xiao.battleroyale.config.common.server.utility;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.server.utility.IUtilitySingleEntry;
import xiao.battleroyale.api.server.utility.UtilityConfigTag;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.utility.defaultconfigs.DefaultUtilityConfigGenerator;
import xiao.battleroyale.config.common.server.utility.type.SurvivalEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class UtilityConfigManager extends AbstractConfigSubManager<UtilityConfigManager.UtilityConfig> {

    private static class UtilityConfigManagerHolder {
        private static final UtilityConfigManager INSTANCE = new UtilityConfigManager();
    }

    public static UtilityConfigManager get() {
        return UtilityConfigManagerHolder.INSTANCE;
    }

    private UtilityConfigManager() {
        allFolderConfigData.put(DEFAULT_UTILITY_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadUtilityConfigs();
    }

    public static final String UTILITY_CONFIG_PATH = ServerConfigManager.SERVER_CONFIG_PATH;
    public static final String UTILITY_CONFIG_SUB_PATH = "utility";

    protected final int DEFAULT_UTILITY_CONFIG_FOLDER = 0;

    public static class UtilityConfig extends AbstractSingleConfig implements IUtilitySingleEntry {
        public static final String CONFIG_TYPE = "UtilityConfig";

        public final SurvivalEntry survivalEntry;

        public UtilityConfig(int id, String name, String color, SurvivalEntry survivalEntry) {
            this(id, name, color, false, survivalEntry);
        }

        public UtilityConfig(int id, String name, String color, boolean isDefault, SurvivalEntry survivalEntry) {
            super(id, name, color, isDefault);
            this.survivalEntry = survivalEntry;
        }

        @Override
        public SurvivalEntry getSurvivalEntry() {
            return survivalEntry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(UtilityConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(UtilityConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(UtilityConfigTag.NAME, name);
            jsonObject.addProperty(UtilityConfigTag.COLOR, color);
            if (survivalEntry != null) {
                jsonObject.add(UtilityConfigTag.SURVIVAL_ENTRY, survivalEntry.toJson());
            }

            return jsonObject;
        }

        public static SurvivalEntry deserializeSurvivalEntry(JsonObject jsonObject) {
            try {
                SurvivalEntry survivalEntry = SurvivalEntry.fromJson(jsonObject);
                if (survivalEntry != null) {
                    return survivalEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid SurvivalEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize SurvivalEntry: {}", e.getMessage());
                return null;
            }
        }

        @Override
        public void applyDefault() {
            this.survivalEntry.applyDefault();
        }
    }

    @Override protected Comparator<UtilityConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(UtilityConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return UtilityConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_UTILITY_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int folderId) {
        DefaultUtilityConfigGenerator.generateDefaultUtilityConfig();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_UTILITY_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public UtilityConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, UtilityConfigTag.ID, -1);
            JsonObject survivalEntryObject = JsonUtils.getJsonObject(configObject, UtilityConfigTag.SURVIVAL_ENTRY, null);
            if (id < 0 || survivalEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid utility config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, UtilityConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, UtilityConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, UtilityConfigTag.COLOR, "#FFFFFF");
            SurvivalEntry survivalEntry = UtilityConfig.deserializeSurvivalEntry(survivalEntryObject);
            if (survivalEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize utility entry for id: {}, in {}", id, filePath);
                return null;
            }

            return new UtilityConfig(id, name, color, isDefault, survivalEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return UTILITY_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return UTILITY_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public UtilityConfig getUtilityConfig(int id) {
        return getConfigEntry(id, DEFAULT_UTILITY_CONFIG_FOLDER);
    }
    public List<UtilityConfig> getUtilityConfigList() {
        return getConfigEntryList(DEFAULT_UTILITY_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新获取接口
     */
    public void reloadUtilityConfigs() {
        reloadConfigs(DEFAULT_UTILITY_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_UTILITY_CONFIG_FOLDER);
    }
}
