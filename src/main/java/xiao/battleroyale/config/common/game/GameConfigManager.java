package xiao.battleroyale.config.common.game;

import xiao.battleroyale.api.IConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;

import java.util.*;

public class GameConfigManager implements IConfigManager {
    public static final String GAME_CONFIG_PATH = "config/battleroyale/game";

    private static GameConfigManager instance;

    private GameConfigManager() {
    }

    public void reloadConfigs() {
        reloadZoneConfigs();
        reloadSpawnConfigs();
        reloadGameruleConfigs();
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

    public void reloadZoneConfigs() {
        ZoneConfigManager.get().loadZoneConfigs();
        ZoneConfigManager.get().initializeDefaultConfigsIfEmpty();
    }

    public void reloadSpawnConfigs() {
        SpawnConfigManager.get().loadSpawnConfigs();
        SpawnConfigManager.get().initializeDefaultConfigsIfEmpty();
    }

    public void reloadGameruleConfigs() {
        GameruleConfigManager.get().loadGameruleConfigs();
        GameruleConfigManager.get().initializeDefaultConfigsIfEmpty();
    }
}