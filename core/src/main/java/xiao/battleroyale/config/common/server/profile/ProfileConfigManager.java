package xiao.battleroyale.config.common.server.profile;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.server.profile.IProfileConfigManager;
import xiao.battleroyale.api.config.common.server.profile.IProfileSingleEntry;
import xiao.battleroyale.api.config.common.server.profile.ProfileConfigTag;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.FolderConfigData;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.profile.defaultconfigs.DefaultProfileConfigGenerator;
import xiao.battleroyale.config.common.server.profile.config.ConfigApplicationEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;

public class ProfileConfigManager
        extends AbstractConfigSubManager<ProfileConfigManager.ProfileConfig>
        implements IProfileConfigManager<ProfileConfigManager.ProfileConfig> {

    private static class ProfileConfigManagerHolder {
        private static final ProfileConfigManager INSTANCE = new ProfileConfigManager();
    }

    public static ProfileConfigManager get() {
        return ProfileConfigManagerHolder.INSTANCE;
    }

    private ProfileConfigManager() {
        allFolderConfigData.put(DEFAULT_PROFILE_CONFIG_FOLDER, new FolderConfigData<>(DEFAULT_PROFILE_CONFIG_FOLDER));
    }

    public static void init() {
        ServerConfigManager.get().registerSubManager(get());
    }

    public static final String PROFILE_CONFIG_PATH = ServerConfigManager.SERVER_CONFIG_PATH;
    public static final String PROFILE_CONFIG_SUB_PATH = "profile";

    protected final int DEFAULT_PROFILE_CONFIG_FOLDER = 0;

    public static class ProfileConfig extends AbstractSingleConfig implements IProfileSingleEntry {
        public static final String CONFIG_TYPE = "ProfileConfig";

        public final ConfigApplicationEntry configApplicationEntry;

        public ProfileConfig(int id, String name, String color, ConfigApplicationEntry configApplicationEntry) {
            this(id, name, color, false, configApplicationEntry);
        }
        public ProfileConfig(int id, String name, String color, boolean isDefault, ConfigApplicationEntry configApplicationEntry) {
            super(id, name, color, isDefault);
            this.configApplicationEntry = configApplicationEntry;
        }
        @Override public @NotNull ProfileConfig copy() {
            return new ProfileConfig(id, name, color, isDefault, configApplicationEntry.copy());
        }

        @Override public int applyAllProfile() {
            return configApplicationEntry.applyConfigProfile();
        }

        @Override public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(ProfileConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(ProfileConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(ProfileConfigTag.NAME, name);
            jsonObject.addProperty(ProfileConfigTag.COLOR, color);
            if (configApplicationEntry != null) {
                jsonObject.add(ProfileConfigTag.CONFIG_APPLICATION_ENTRY, configApplicationEntry.toJson());
            }

            return jsonObject;
        }

        public static ConfigApplicationEntry deserializeConfigApplicationEntry(JsonObject jsonObject) {
            try {
                ConfigApplicationEntry configApplicationEntry = ConfigApplicationEntry.fromJson(jsonObject);
                if (configApplicationEntry != null) {
                    return configApplicationEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid ConfigApplicationEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ConfigApplicationEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<ProfileConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(ProfileConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return ProfileConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public boolean generateDefaultConfigs() {
        return generateDefaultConfigs(DEFAULT_PROFILE_CONFIG_FOLDER);
    }

    @Override public boolean generateDefaultConfigs(int folderId) {
        DefaultProfileConfigGenerator.generateAllDefaultConfigs(String.valueOf(getConfigDirPath()));
        return true;
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_PROFILE_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public ProfileConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, ProfileConfigTag.ID, -1);
            JsonObject configApplicationEntryObject = JsonUtils.getJsonObject(configObject, ProfileConfigTag.CONFIG_APPLICATION_ENTRY, null);
            if (id < 0 || configApplicationEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid profile config in {}", filePath);
                return null;
            }

            boolean isDefault = JsonUtils.getJsonBool(configObject, ProfileConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, ProfileConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, ProfileConfigTag.COLOR, "#FFFFFF");
            ConfigApplicationEntry configApplicationEntry = ProfileConfig.deserializeConfigApplicationEntry(configApplicationEntryObject);
            if (configApplicationEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize profile entry for id: {}, in {}", id, filePath);
                return null;
            }
            return new ProfileConfig(id, name, color, isDefault, configApplicationEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return PROFILE_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return PROFILE_CONFIG_SUB_PATH;
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_PROFILE_CONFIG_FOLDER);
    }
}