package xiao.battleroyale.config.common.game;

import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;

import java.util.*;

public class GameConfigManager {
    public static final String GAME_CONFIG_PATH = "config/battleroyale/game";

    private static GameConfigManager instance;

    private GameConfigManager() {
        ZoneConfigManager.init();
    }

    public void reloadConfigs() {
        ZoneConfigManager.get().reloadConfigs();
    }

    public static void init() {
        if (instance == null) {
            instance = new GameConfigManager();
        }
    }

    public static GameConfigManager get() {
        if (instance == null) {
            GameConfigManager.init();
        }
        return instance;
    }

    public ZoneConfig getZoneConfig(int zoneId) {
        return ZoneConfigManager.get().getZoneConfig(zoneId);
    }

    public List<ZoneConfig> getAllZoneConfig() {
        return ZoneConfigManager.get().getAllZoneConfigs();
    }

    public void loadZoneConfigs() {
        ZoneConfigManager.get().loadZoneConfigs();
    }
}