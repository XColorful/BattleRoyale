package xiao.battleroyale.config.common.game.gamerule;

import xiao.battleroyale.api.game.gamerule.IGameruleEntry;

public class GameruleConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    private static final String GAMERULE_CONFIG_SUB_PATH = "gamerule";

    private static GameruleConfigManager instance;

    private GameruleConfigManager() {
        ;
    }

    public void reloadConfigs() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new GameruleConfigManager();
        }
    }

    public static GameruleConfigManager get() {
        if (instance == null) {
            GameruleConfigManager.init();
        }
        return instance;
    }

    public static class GameruleConfig {
        private final int gameId;
        private final String gameName;
        private final int color;
        private final IGameruleEntry entry;

        public GameruleConfig(int gameId, String name, int color, IGameruleEntry entry) {
            this.gameId = gameId;
            this.gameName = name;
            this.color = color;
            this.entry = entry;
        }

        public int getGameId() {
            return gameId;
        }

        public String getGameName() {
            return gameName;
        }

        public int getColor() {
            return color;
        }

        public IGameruleEntry getEntry() {
            return entry;
        }
    }
}