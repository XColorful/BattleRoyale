package xiao.battleroyale.config.common.game.stats;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.game.stats.IStatsConfigManager;
import xiao.battleroyale.api.config.common.game.stats.IStatsSingleEntry;
import xiao.battleroyale.api.config.common.game.stats.StatsConfigTag;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.FolderConfigData;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.stats.defaultconfigs.DefaultStatsConfigGenerator;
import xiao.battleroyale.config.common.game.stats.scoreboard.ScoreboardEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;

public class StatsConfigManager
        extends AbstractConfigSubManager<StatsConfigManager.StatsConfig>
        implements IStatsConfigManager<StatsConfigManager.StatsConfig> {

    private static class StatsConfigManagerHolder {
        private static final StatsConfigManager INSTANCE = new StatsConfigManager();
    }

    public static StatsConfigManager get() {
        return StatsConfigManagerHolder.INSTANCE;
    }

    private StatsConfigManager() {
        allFolderConfigData.put(DEFAULT_STATS_CONFIG_FOLDER, new FolderConfigData<>(DEFAULT_STATS_CONFIG_FOLDER));
    }

    public static void init() {
        GameConfigManager.get().registerSubManager(get());
    }

    public static final String STATS_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String STATS_CONFIG_SUB_PATH = "stats";

    protected final int DEFAULT_STATS_CONFIG_FOLDER = 0;

    public static class StatsConfig extends AbstractSingleConfig implements IStatsSingleEntry {
        public static final String CONFIG_TYPE = "StatsConfig";

        public final @NotNull ScoreboardEntry scoreboardEntry;

        public StatsConfig(int id, String name, String color, @NotNull ScoreboardEntry scoreboardEntry) {
            this(id, name, color, false, scoreboardEntry);
        }

        public StatsConfig(int id, String name, String color, boolean isDefault, @NotNull ScoreboardEntry scoreboardEntry) {
            super(id, name, color, isDefault);
            this.scoreboardEntry = scoreboardEntry;
        }
        @Override public @NotNull StatsConfig copy() {
            return new StatsConfig(id, name, color, isDefault, scoreboardEntry.copy());
        }

        @Override
        public @NotNull ScoreboardEntry getScoreboardEntry() {
            return scoreboardEntry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(StatsConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(StatsConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(StatsConfigTag.NAME, name);
            jsonObject.addProperty(StatsConfigTag.COLOR, color);
            if (scoreboardEntry != null) {
                jsonObject.add(StatsConfigTag.SCOREBOARD_ENTRY, scoreboardEntry.toJson());
            }
            return jsonObject;
        }

        public static ScoreboardEntry deserializeScoreboardEntry(JsonObject jsonObject) {
            try {
                ScoreboardEntry scoreboardEntry = ScoreboardEntry.fromJson(jsonObject);
                if (scoreboardEntry != null) {
                    return scoreboardEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid ScoreboardEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ScoreboardEntry: {}", e.getMessage());
                return null;
            }
        }

        @Override
        public void applyDefault() {
            BattleRoyale.getGameManager().setStatsConfigId(getConfigId());
        }
    }

    @Override protected Comparator<StatsConfig> getConfigIdComparator(int folderId) {
        return Comparator.comparingInt(StatsConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return StatsConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public boolean generateDefaultConfigs() {
        return generateDefaultConfigs(DEFAULT_STATS_CONFIG_FOLDER);
    }

    @Override public boolean generateDefaultConfigs(int folderId) {
        DefaultStatsConfigGenerator.generateAllDefaultConfigs(String.valueOf(getConfigDirPath()));
        return true;
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_STATS_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override public StatsConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, StatsConfigTag.ID, -1);
            JsonObject sbEntryObject = JsonUtils.getJsonObject(configObject, StatsConfigTag.SCOREBOARD_ENTRY, null);
            if (id < 0 || sbEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid stats config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, StatsConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, StatsConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, StatsConfigTag.COLOR, "#");
            ScoreboardEntry sbEntry = StatsConfig.deserializeScoreboardEntry(sbEntryObject);
            if (sbEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize scoreboard entry for id: {} in {}", id, filePath);
                return null;
            }

            return new StatsConfig(id, name, color, isDefault, sbEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(folderId), filePath, e.getMessage());
            return null;
        }
    }

    @Override public String getConfigPath(int folderId) {
        return STATS_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return STATS_CONFIG_SUB_PATH;
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_STATS_CONFIG_FOLDER);
    }
}