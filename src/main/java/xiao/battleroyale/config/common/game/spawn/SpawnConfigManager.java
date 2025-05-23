package xiao.battleroyale.config.common.game.spawn;

import xiao.battleroyale.api.game.spawn.ISpawnEntry;

public class SpawnConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    private static final String SPAWN_CONFIG_SUB_PATH = "spawn";

    private static SpawnConfigManager instance;

    private SpawnConfigManager() {
        ;
    }

    public void reloadConfigs() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new SpawnConfigManager();
        }
    }

    public static SpawnConfigManager get() {
        if (instance == null) {
            SpawnConfigManager.init();
        }
        return instance;
    }

    public static class SpawnConfig {
        private final int id;
        private final String name;
        private final String color;
        private final ISpawnEntry entry;

        public SpawnConfig(int id, String name, String color, ISpawnEntry entry) {
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

        public String getColor() {
            return color;
        }

        public ISpawnEntry getEntry() {
            return entry;
        }
    }
}