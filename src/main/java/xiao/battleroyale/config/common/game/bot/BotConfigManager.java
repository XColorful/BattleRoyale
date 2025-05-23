package xiao.battleroyale.config.common.game.bot;

import xiao.battleroyale.api.game.bot.IBotEntry;

public class BotConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    private static final String BOT_CONFIG_SUB_PATH = "bot";

    private static BotConfigManager instance;

    private BotConfigManager() {
        ;
    }

    public void reloadConfigs() {
        ;
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