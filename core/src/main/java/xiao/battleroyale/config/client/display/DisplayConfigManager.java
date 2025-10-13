package xiao.battleroyale.config.client.display;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.display.IDisplaySingleEntry;
import xiao.battleroyale.api.client.display.DisplayConfigTag;
import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.FolderConfigData;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.client.display.defaultconfigs.DefaultDisplayConfigGenerator;
import xiao.battleroyale.config.client.display.type.GameEntry;
import xiao.battleroyale.config.client.display.type.MapEntry;
import xiao.battleroyale.config.client.display.type.TeamEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class DisplayConfigManager extends AbstractConfigSubManager<DisplayConfigManager.DisplayConfig> {

    private static class DisplayConfigManagerHolder {
        private static final DisplayConfigManager INSTANCE = new DisplayConfigManager();
    }

    public static DisplayConfigManager get() {
        return DisplayConfigManagerHolder.INSTANCE;
    }

    private DisplayConfigManager() {
        super(CommandArg.DISPLAY);
        allFolderConfigData.put(DEFAULT_DISPLAY_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        ClientConfigManager.get().registerSubManager(get());
    }

    public static final String DISPLAY_CONFIG_PATH = ClientConfigManager.CLIENT_CONFIG_PATH;
    public static final String DISPLAY_CONFIG_SUB_PATH = "display";

    protected final int DEFAULT_DISPLAY_CONFIG_FOLDER = 0;

    public static class DisplayConfig extends AbstractSingleConfig implements IDisplaySingleEntry {
        public static final String CONFIG_TYPE = "DisplayConfig";

        public final TeamEntry teamEntry;
        public final GameEntry gameEntry;
        public final MapEntry mapEntry;

        public DisplayConfig(int id, String name, String color, TeamEntry teamEntry, GameEntry gameEntry, MapEntry mapEntry) {
            this(id, name, color, false, teamEntry, gameEntry, mapEntry);
        }
        public DisplayConfig(int id, String name, String color, boolean isDefault, TeamEntry teamEntry, GameEntry gameEntry, MapEntry mapEntry) {
            super(id, name, color, isDefault);
            this.teamEntry = teamEntry;
            this.gameEntry = gameEntry;
            this.mapEntry = mapEntry;
        }
        @Override public @NotNull DisplayConfig copy() {
            return new DisplayConfig(id, name, color, isDefault, teamEntry.copy(), gameEntry.copy(), mapEntry.copy());
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
            if (mapEntry != null) {
                jsonObject.add(DisplayConfigTag.MAP_ENTRY, mapEntry.toJson());
            }
            return jsonObject;
        }

        @Override
        public void applyDefault() {
            teamEntry.applyDefault();
            gameEntry.applyDefault();
            mapEntry.applyDefault();
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

    public static MapEntry deserializeMapEntry(JsonObject jsonObject) {
        try {
            MapEntry mapEntry = MapEntry.fromJson(jsonObject);
            if (mapEntry != null) {
                return mapEntry;
            } else {
                BattleRoyale.LOGGER.warn("Skipped invalid MapEntry");
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize MapEntry: {}", e.getMessage());
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
            JsonObject mapEntryObject = JsonUtils.getJsonObject(configObject, DisplayConfigTag.MAP_ENTRY, null);
            if (id < 0 || teamEntryObject == null || gameEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid display config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, DisplayConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, DisplayConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, DisplayConfigTag.COLOR, "");
            TeamEntry teamEntry = DisplayConfigManager.deserializeTeamEntry(teamEntryObject);
            GameEntry gameEntry = DisplayConfigManager.deserializeGameEntry(gameEntryObject);
            MapEntry mapEntry = DisplayConfigManager.deserializeMapEntry(mapEntryObject);
            return new DisplayConfig(id, name, color, isDefault, teamEntry, gameEntry, mapEntry);
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
        return getConfigEntry(DEFAULT_DISPLAY_CONFIG_FOLDER, id);
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