package xiao.battleroyale.config.common.game.gamerule;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.gamerule.GameruleConfigTag;
import xiao.battleroyale.api.game.gamerule.IGameruleSingleEntry;
import xiao.battleroyale.config.common.AbstractConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.defaultconfigs.DefaultGameruleConfigGenerator;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;


public class GameruleConfigManager extends AbstractConfigManager<GameruleConfigManager.GameruleConfig> {

    private static class GameruleConfigManagerHolder {
        private static final GameruleConfigManager INSTANCE = new GameruleConfigManager();
    }

    public static GameruleConfigManager get() {
        return GameruleConfigManagerHolder.INSTANCE;
    }

    private GameruleConfigManager() {
        allFolderConfigData.put(DEFAULT_GAMERULE_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadGameruleConfigs();
    }


    public static final String GAMERULE_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String GAMERULE_CONFIG_SUB_PATH = "gamerule";

    protected final int DEFAULT_GAMERULE_CONFIG_FOLDER = 0;

    public static class GameruleConfig implements IGameruleSingleEntry {
        public static final String CONFIG_TYPE = "GameruleConfig";

        private final int gameId;
        private final String gameName;
        private final String color;
        private final BattleroyaleEntry brEntry;
        private final MinecraftEntry mcEntry;

        public GameruleConfig(int gameId, String name, String color, BattleroyaleEntry brEntry, MinecraftEntry mcEntry) {
            this.gameId = gameId;
            this.gameName = name;
            this.color = color;
            this.brEntry = brEntry;
            this.mcEntry = mcEntry;
        }

        public int getGameId() {
            return gameId;
        }
        public String getGameName() {
            return gameName;
        }
        public String getColor() {
            return color;
        }

        @Override
        public BattleroyaleEntry getBattleRoyaleEntry() {
            return brEntry;
        }
        @Override
        public MinecraftEntry getMinecraftEntry() {
            return mcEntry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(GameruleConfigTag.GAME_ID, gameId);
            jsonObject.addProperty(GameruleConfigTag.GAME_NAME, gameName);
            jsonObject.addProperty(GameruleConfigTag.GAME_COLOR, color);
            if (brEntry != null) {
                jsonObject.add(GameruleConfigTag.BATTLEROYALE_ENTRY, brEntry.toJson());
            }
            if (mcEntry != null) {
                jsonObject.add(GameruleConfigTag.MINECRAFT_ENTRY, mcEntry.toJson());
            }
            return jsonObject;
        }

        @Override
        public int getConfigId() {
            return getGameId();
        }

        public static BattleroyaleEntry deserializeBattleroyaleEntry(JsonObject jsonObject) {
            try {
                BattleroyaleEntry brEntry = BattleroyaleEntry.fromJson(jsonObject);
                if (brEntry != null) {
                    return brEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid BattleroyaleEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize BattleroyaleEntry: {}", e.getMessage());
                return null;
            }
        }

        public static MinecraftEntry deserializeMinecraftEntry(JsonObject jsonObject) {
            try {
                MinecraftEntry mcEntry = MinecraftEntry.fromJson(jsonObject);
                if (mcEntry != null) {
                    return mcEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid MinecraftEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize MinecraftEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<GameruleConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(GameruleConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int configType) {
        return GameruleConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_GAMERULE_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int configType) {
        DefaultGameruleConfigGenerator.generateDefaultGameruleConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_GAMERULE_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public GameruleConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        try {
            int gameId = JsonUtils.getJsonInt(configObject, GameruleConfigTag.GAME_ID, -1);
            JsonObject brEntryObject = JsonUtils.getJsonObject(configObject, GameruleConfigTag.BATTLEROYALE_ENTRY, null);
            JsonObject mcEntryObject = JsonUtils.getJsonObject(configObject, GameruleConfigTag.MINECRAFT_ENTRY, null);
            if (gameId < 0 || brEntryObject == null || mcEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid gamerule config in {}", filePath);
                return null;
            }

            String gameName = JsonUtils.getJsonString(configObject, GameruleConfigTag.GAME_NAME, "");
            String color = JsonUtils.getJsonString(configObject, GameruleConfigTag.GAME_COLOR, "");
            BattleroyaleEntry brEntry = GameruleConfig.deserializeBattleroyaleEntry(brEntryObject);
            MinecraftEntry mcEntry = GameruleConfig.deserializeMinecraftEntry(mcEntryObject);
            if (brEntry == null || mcEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize gamerule entry for id: {} in {}", gameId, filePath);
                return null;
            }

            return new GameruleConfig(gameId, gameName, color, brEntry, mcEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int configType) {
        return GAMERULE_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int configType) {
        return GAMERULE_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public GameruleConfig getGameruleConfig(int gameId) {
        return getConfigEntry(gameId, DEFAULT_GAMERULE_CONFIG_FOLDER);
    }
    public List<GameruleConfig> getGameruleConfigList() {
        return getConfigEntryList(DEFAULT_GAMERULE_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadGameruleConfigs() {
        reloadConfigs(DEFAULT_GAMERULE_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_GAMERULE_CONFIG_FOLDER);
    }
}