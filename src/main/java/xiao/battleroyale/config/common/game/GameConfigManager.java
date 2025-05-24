package xiao.battleroyale.config.common.game;

import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;

import java.util.*;

public class GameConfigManager {
    public static final String GAME_CONFIG_PATH = "config/battleroyale/game";

    private static GameConfigManager instance;

    private GameConfigManager() {
    }

    public void reloadConfigs() {
        loadZoneConfigs();
        loadSpawnConfigs();
        loadGameruleConfigs();

        initializeDefaultConfigsIfEmpty();
    }

    public static void init() {
        if (instance == null) {
            instance = new GameConfigManager();
            ZoneConfigManager.init();
            SpawnConfigManager.init();
            GameruleConfigManager.init();
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

    public List<ZoneConfig> getAllZoneConfigs() {
        return ZoneConfigManager.get().getAllZoneConfigs();
    }

    public SpawnConfig getSpawnConfig(int id) {
        return SpawnConfigManager.get().getSpawnConfig(id);
    }

    public List<SpawnConfig> getAllSpawnConfigs() {
        return SpawnConfigManager.get().getAllSpawnConfigs();
    }

    public GameruleConfig getGameruleConfig(int gameId) {
        return GameruleConfigManager.get().getGameruleConfig(gameId);
    }

    public List<GameruleConfig> getAllGameruleConfigs() {
        return GameruleConfigManager.get().getAllGameruleConfigs();
    }

    public void loadZoneConfigs() {
        ZoneConfigManager.get().loadZoneConfigs();
    }

    public void loadSpawnConfigs() {
        SpawnConfigManager.get().loadSpawnConfigs();
    }

    public void loadGameruleConfigs() {
        GameruleConfigManager.get().loadGameruleConfigs();
    }

    private void initializeDefaultConfigsIfEmpty() {
        ZoneConfigManager.get().initializeDefaultConfigsIfEmpty();
        SpawnConfigManager.get().initializeDefaultConfigsIfEmpty();
        GameruleConfigManager.get().initializeDefaultConfigsIfEmpty();
    }
}