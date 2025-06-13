package xiao.battleroyale.config.common.game;

import xiao.battleroyale.config.common.AbstractConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager.BotConfig;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;

import java.nio.file.Paths;
import java.util.*;

public class GameConfigManager {
    public static final String GAME_CONFIG_SUB_PATH = "game";
    public static final String GAME_CONFIG_PATH = Paths.get(AbstractConfigManager.MOD_CONFIG_PATH).resolve(GAME_CONFIG_SUB_PATH).toString();

    private static class GameConfigManagerHolder {
        private static final GameConfigManager INSTANCE = new GameConfigManager();
    }

    public static GameConfigManager get() {
        return GameConfigManagerHolder.INSTANCE;
    }

    private GameConfigManager() {}

    public static void init() {
        get();
        BotConfigManager.init();
        GameruleConfigManager.init();
        SpawnConfigManager.init();
        ZoneConfigManager.init();
    }

    /**
     * IConfigManager
     */
    public String getBotConfigEntryFileName() {
        return BotConfigManager.get().getConfigEntryFileName();
    }
    public String getGameruleConfigEntryFileName() {
        return GameruleConfigManager.get().getConfigEntryFileName();
    }
    public String getSpawnConfigEntryFileName() {
        return SpawnConfigManager.get().getConfigEntryFileName();
    }
    public String getZoneConfigEntryFileName() {
        return ZoneConfigManager.get().getConfigEntryFileName();
    }

    /**
     * IConfigDefaultable
     */
    public void generateDefaultBotConfigs() {
        BotConfigManager.get().generateDefaultConfigs();
    }
    public void generateDefaultGameruleConfigs() {
        GameruleConfigManager.get().generateDefaultConfigs();
    }
    public void generateDefaultSpawnConfigs() {
        SpawnConfigManager.get().generateDefaultConfigs();
    }
    public void generateDefaultZoneConfigs() {
        ZoneConfigManager.get().generateDefaultConfigs();
    }
    public int getDefaultBotConfigId() {
        return BotConfigManager.get().getDefaultConfigId();
    }
    public int getDefaultGameruleConfigId() {
        return GameruleConfigManager.get().getDefaultConfigId();
    }
    public int getDefaultSpawnConfigId() {
        return SpawnConfigManager.get().getDefaultConfigId();
    }
    public int getDefaultZoneConfigId() {
        return ZoneConfigManager.get().getDefaultConfigId();
    }
    public void setDefaultBotConfigId(int id) {
        BotConfigManager.get().setDefaultConfigId(id);
    }
    public void setDefaultGameruleConfigId(int id) {
        GameruleConfigManager.get().setDefaultConfigId(id);
    }
    public void setDefaultSpawnConfigId(int id) {
        SpawnConfigManager.get().setDefaultConfigId(id);
    }
    public void setDefaultZoneConfigId(int id) { // 没啥意义
        ZoneConfigManager.get().setDefaultConfigId(id);
    }

    /**
     * IConfigLoadable
     */
    public String getBotConfigPath() {
        return String.valueOf(BotConfigManager.get().getConfigDirPath());
    }
    public String getGameruleConfigPath() {
        return String.valueOf(GameruleConfigManager.get().getConfigDirPath());
    }
    public String getSpawnConfigPath() {
        return String.valueOf(SpawnConfigManager.get().getConfigDirPath());
    }
    public String getZoneConfigPath() {
        return String.valueOf(ZoneConfigManager.get().getConfigDirPath());
    }

    /**
     * 特定类别的获取接口
     */
    public BotConfig getBotConfig(int id) {
        return BotConfigManager.get().getBotConfig(id);
    }
    public List<BotConfig> getAllBotConfigs() {
        return BotConfigManager.get().getAllBotConfigs();
    }
    public GameruleConfig getGameruleConfig(int gameId) {
        return GameruleConfigManager.get().getGameruleConfig(gameId);
    }
    public List<GameruleConfig> getAllGameruleConfigs() {
        return GameruleConfigManager.get().getAllGameruleConfigs();
    }
    public SpawnConfig getSpawnConfig(int id) {
        return SpawnConfigManager.get().getSpawnConfig(id);
    }
    public List<SpawnConfig> getAllSpawnConfigs() {
        return SpawnConfigManager.get().getAllSpawnConfigs();
    }
    public ZoneConfig getZoneConfig(int zoneId) {
        return ZoneConfigManager.get().getZoneConfig(zoneId);
    }
    public List<ZoneConfig> getAllZoneConfigs() {
        return ZoneConfigManager.get().getAllConfigEntries();
    }


    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllConfigs() {
        // reloadBotConfigs();
        reloadZoneConfigs();
        reloadSpawnConfigs();
        reloadGameruleConfigs();
    }
    public void reloadBotConfigs() {
        BotConfigManager.get().reloadBotConfigs();
    }
    public void reloadGameruleConfigs() {
        GameruleConfigManager.get().reloadGameruleConfigs();
    }
    public void reloadSpawnConfigs() {
        SpawnConfigManager.get().reloadSpawnConfigs();
    }
    public void reloadZoneConfigs() {
        ZoneConfigManager.get().reloadZoneConfigs();
    }

    public boolean switchNextBotConfig() {
        return BotConfigManager.get().switchConfigFile();
    }
    public boolean switchBotConfig(String fileName) {
        return BotConfigManager.get().switchConfigFile(fileName);
    }
    public boolean switchNextGameruleConfig() {
        return GameruleConfigManager.get().switchConfigFile();
    }
    public boolean switchGameruleConfig(String fileName) {
        return GameruleConfigManager.get().switchConfigFile(fileName);
    }
    public boolean switchNextSpawnConfig() {
        return SpawnConfigManager.get().switchConfigFile();
    }
    public boolean switchSpawnConfig(String fileName) {
        return SpawnConfigManager.get().switchConfigFile(fileName);
    }
    public boolean switchNextZoneConfig() {
        return ZoneConfigManager.get().switchConfigFile();
    }
    public boolean switchZoneConfig(String fileName) {
        return ZoneConfigManager.get().switchConfigFile(fileName);
    }
}