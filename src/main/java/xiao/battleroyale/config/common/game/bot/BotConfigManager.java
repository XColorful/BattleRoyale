package xiao.battleroyale.config.common.game.bot;

import xiao.battleroyale.api.IConfigManager;
import xiao.battleroyale.api.game.bot.IBotEntry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotConfigManager implements IConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    private static final String BOT_CONFIG_SUB_PATH = "bot";

    private final Map<Integer, BotConfig> botConfigs = new HashMap<>();
    private final List<BotConfig> allBotConfigs = new ArrayList<>();

    private static BotConfigManager instance;

    private BotConfigManager() {
        ;
    }

    public void reloadConfigs() {
        loadBotConfigs();

        initializeDefaultConfigsIfEmpty();
    }

    public static void init() {
        if (instance == null) {
            instance = new BotConfigManager();
        }
    }

    public static BotConfigManager get() {
        if (instance == null) {
            BotConfigManager.init();
        }
        return instance;
    }

    public BotConfig getBotConfig(int id) {
        return botConfigs.get(id);
    }

    public List<BotConfig> getAllBotConfigs() {
        return allBotConfigs;
    }

    public void loadBotConfigs() {
        ;
    }

    private void loadConfigsFromDirectory(Path directoryPath, Map<Integer, BotConfig> configMap, List<BotConfig> configList) {
        ;
    }

    private void loadConfigFromFile(Path filePath, Map<Integer, BotConfig> configMap, List<BotConfig> configList) {
        ;
    }

    public void initializeDefaultConfigsIfEmpty() {

    }

    public static class BotConfig {
        private final int id;
        private final String name;
        private final int color;
        private final IBotEntry entry;

        public BotConfig(int id, String name, int color, IBotEntry entry) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.entry = entry;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }

        public IBotEntry getEntry() {
            return entry;
        }
    }
}