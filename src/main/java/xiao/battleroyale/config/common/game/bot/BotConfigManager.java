package xiao.battleroyale.config.common.game.bot;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.IConfigSingleEntry;
import xiao.battleroyale.api.game.bot.IBotEntry;
import xiao.battleroyale.config.common.AbstractConfigManager;
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
        allConfigData.put(DEFAULT_BOT_CONFIG_DATA_ID, new ConfigData<>());
    }

    public static void init() {
        get().reloadBotConfigs();
    }

    public static final String BOT_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String BOT_CONFIG_SUB_PATH = "bot";

    protected final int DEFAULT_BOT_CONFIG_DATA_ID = 0;

    public record BotConfig(int id, String name, String color, IBotEntry entry) implements IConfigSingleEntry {
            public static final String CONFIG_TYPE = "BotConfig";

        @Override
            public String getType() {
                return CONFIG_TYPE;
            }

            @Override
            public JsonObject toJson() {
                return null;
            }

            @Override
            public int getConfigId() {
                return id();
            }
        }


    @Override protected Comparator<BotConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(BotConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getConfigType(int configType) {
        return BotConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_BOT_CONFIG_DATA_ID);
    }

    @Override public void generateDefaultConfigs(int configType) {
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_BOT_CONFIG_DATA_ID);
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
        return getConfigEntry(id, DEFAULT_BOT_CONFIG_DATA_ID);
    }
    public List<BotConfig> getAllBotConfigs() {
        return getAllConfigEntries(DEFAULT_BOT_CONFIG_DATA_ID);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadBotConfigs() {
        reloadConfigs(DEFAULT_BOT_CONFIG_DATA_ID);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_BOT_CONFIG_DATA_ID);
    }
}