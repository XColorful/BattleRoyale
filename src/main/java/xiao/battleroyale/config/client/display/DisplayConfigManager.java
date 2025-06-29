package xiao.battleroyale.config.client.display;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.display.IDisplaySingleEntry;
import xiao.battleroyale.api.client.display.DisplayConfigTag;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.client.display.defaultconfigs.DefaultDisplayConfigGenerator;
import xiao.battleroyale.config.client.display.type.GameEntry;
import xiao.battleroyale.config.client.display.type.TeamEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class DisplayConfigManager extends AbstractConfigManager<DisplayConfigManager.DisplayConfig> {

    private static class DisplayConfigManagerHolder {
        private static final DisplayConfigManager INSTANCE = new DisplayConfigManager();
    }

    public static DisplayConfigManager get() {
        return DisplayConfigManagerHolder.INSTANCE;
    }

    private DisplayConfigManager() {
        allFolderConfigData.put(DEFAULT_DISPLAY_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadDisplayConfigs();
    }

    public static final String DISPLAY_CONFIG_PATH = ClientConfigManager.CLIENT_CONFIG_PATH;
    public static final String DISPLAY_CONFIG_SUB_PATH = "display";

    protected final int DEFAULT_DISPLAY_CONFIG_FOLDER = 0;

    public static class DisplayConfig extends AbstractSingleConfig implements IDisplaySingleEntry {
        public static final String CONFIG_TYPE = "DisplayConfig";

        public final TeamEntry teamEntry;
        public final GameEntry gameEntry;

        public DisplayConfig(int id, String name, String color, TeamEntry teamEntry, GameEntry gameEntry) {
            this(id, name, color, false, teamEntry, gameEntry);
        }

        public DisplayConfig(int id, String name, String color, boolean isDefault, TeamEntry teamEntry, GameEntry gameEntry) {
            super(id, name, color, isDefault);
            this.teamEntry = teamEntry;
            this.gameEntry = gameEntry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(DisplayConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(DisplayConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(DisplayConfigTag.NAME, name);
            jsonObject.addProperty(DisplayConfigTag.COLOR, color);
            if (teamEntry != null) {
                jsonObject.add(DisplayConfigTag.TEAM_ENTRY, teamEntry.toJson());
            }
            if (gameEntry != null) {
                jsonObject.add(DisplayConfigTag.GAME_ENTRY, gameEntry.toJson());
            }
            return jsonObject;
        }

        @Override
        public void applyDefault() {
            teamEntry.applyDefault();
            gameEntry.applyDefault();
        }
    }

    public static TeamEntry deserializeTeamEntry(JsonObject jsonObject) {
        try {
            TeamEntry teamEntry = TeamEntry.fromJson(jsonObject);
            if (teamEntry != null) {
                return teamEntry;
            } else {
                BattleRoyale.LOGGER.warn("Skipped invalid TeamEntry");
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize TeamEntry: {}", e.getMessage());
            return null;
        }
    }

    public static GameEntry deserializeGameEntry(JsonObject jsonObject) {
        try {
            GameEntry gameEntry = GameEntry.fromJson(jsonObject);
            if (gameEntry != null) {
                return gameEntry;
            } else {
                BattleRoyale.LOGGER.warn("Skipped invalid GameEntry");
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize GameEntry: {}", e.getMessage());
            return null;
        }
    }

    @Override protected Comparator<DisplayConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(DisplayConfig::getConfigId);
    }

    @Override public String getFolderType(int folderId) {
        return DisplayConfig.CONFIG_TYPE;
    }

    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_DISPLAY_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int folderId) {
        DefaultDisplayConfigGenerator.generateDefaultDisplayConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_DISPLAY_CONFIG_FOLDER);
    }

    @Nullable
    @Override
    public DisplayConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, DisplayConfigTag.ID, -1);
            JsonObject teamEntryObject = JsonUtils.getJsonObject(configObject, DisplayConfigTag.TEAM_ENTRY, null);
            JsonObject gameEntryObject = JsonUtils.getJsonObject(configObject, DisplayConfigTag.GAME_ENTRY, null);
            if (id < 0 || teamEntryObject == null || gameEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid display config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, DisplayConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, DisplayConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, DisplayConfigTag.COLOR, "");
            TeamEntry teamEntry = DisplayConfigManager.deserializeTeamEntry(teamEntryObject);
            GameEntry gameEntry = DisplayConfigManager.deserializeGameEntry(gameEntryObject);
            return new DisplayConfig(id, name, color, isDefault, teamEntry, gameEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return DISPLAY_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return DISPLAY_CONFIG_SUB_PATH;
    }

    public DisplayConfig getDisplayConfig(int id) {
        return getConfigEntry(id, DEFAULT_DISPLAY_CONFIG_FOLDER);
    }
    public List<DisplayConfig> getDisplayConfigList() {
        return getConfigEntryList(DEFAULT_DISPLAY_CONFIG_FOLDER);
    }

    public void reloadDisplayConfigs() {
        reloadConfigs(DEFAULT_DISPLAY_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_DISPLAY_CONFIG_FOLDER);
    }
}