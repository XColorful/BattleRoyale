package xiao.battleroyale.config.common.game.bot;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.bot.BotConfigTag;
import xiao.battleroyale.api.game.bot.IBotEntry;
import xiao.battleroyale.api.game.bot.IBotSingleEntry;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.defaultconfigs.DefaultBotConfigGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;


public class BotConfigManager extends AbstractConfigManager<BotConfigManager.BotConfig> {

    private static class BotConfigManagerHolder {
        private static final BotConfigManager INSTANCE = new BotConfigManager();
    }

    public static BotConfigManager get() {
        return BotConfigManagerHolder.INSTANCE;
    }

    private BotConfigManager() {
        allFolderConfigData.put(DEFAULT_BOT_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadBotConfigs();
    }

    public static final String BOT_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String BOT_CONFIG_SUB_PATH = "bot";

    protected final int DEFAULT_BOT_CONFIG_FOLDER = 0;

    public static class BotConfig extends AbstractSingleConfig implements IBotSingleEntry {
        public static final String CONFIG_TYPE = "BotConfig";

        public final IBotEntry entry;

        public BotConfig(int id, String name, String color, IBotEntry entry) {
            this(id, name, color, false, entry);
        }

        public BotConfig(int id, String name, String color, boolean isDefault, IBotEntry entry) {
            super(id, name, color, isDefault);
            this.entry = entry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(BotConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(BotConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(BotConfigTag.NAME, name);
            jsonObject.addProperty(BotConfigTag.COLOR, color);

            return jsonObject;
        }

        @Override
        public void applyDefault() {
            GameManager.get().setBotConfigId(getConfigId());
        }
    }


    @Override protected Comparator<BotConfig> getConfigIdComparator(int folderId) {
        return Comparator.comparingInt(BotConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return BotConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_BOT_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int folderId) {
        DefaultBotConfigGenerator.generateDefaultBotConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_BOT_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public BotConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, BotConfigTag.ID, -1);
            if (id < 0) {
                BattleRoyale.LOGGER.warn("Skipped invalid bot config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBoolean(configObject, BotConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, BotConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, BotConfigTag.COLOR, "");

            return new BotConfig(id, name, color, isDefault, null);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return BOT_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return BOT_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public BotConfig getBotConfig(int id) {
        return getConfigEntry(id, DEFAULT_BOT_CONFIG_FOLDER);
    }
    public List<BotConfig> getBotConfigList() {
        return getConfigEntryList(DEFAULT_BOT_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadBotConfigs() {
        reloadConfigs(DEFAULT_BOT_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_BOT_CONFIG_FOLDER);
    }
}