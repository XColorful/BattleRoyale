package xiao.battleroyale.config.common.game.bot;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.bot.IBotEntry;
import xiao.battleroyale.config.common.AbstractConfigManager;
import xiao.battleroyale.config.common.AbstractSingleConfig;
import xiao.battleroyale.config.common.game.GameConfigManager;

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

    public class BotConfig extends AbstractSingleConfig {
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
            return null;
        }
    }


    @Override protected Comparator<BotConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(BotConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int configType) {
        return BotConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_BOT_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int configType) {
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_BOT_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public BotConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        return null;
    }
    @Override public String getConfigPath(int configType) {
        return BOT_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int configType) {
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