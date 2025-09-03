package xiao.battleroyale.config.common.server;

import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager.PerformanceConfig;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager.UtilityConfig;

import java.nio.file.Paths;
import java.util.List;

public class ServerConfigManager {

    public static final String SERVER_CONFIG_SUB_PATH = "server";
    public static final String SERVER_CONFIG_PATH = Paths.get(AbstractConfigManager.MOD_CONFIG_PATH).resolve(SERVER_CONFIG_SUB_PATH).toString();

    private static class ServerConfigManagerHolder {
        private static final ServerConfigManager INSTANCE = new ServerConfigManager();
    }

    public static ServerConfigManager get() {
        return ServerConfigManagerHolder.INSTANCE;
    }

    private ServerConfigManager() {}

    public static void init() {
        get();
        PerformanceConfigManager.init();
    }

    /**
     * IConfigManager
     */
    public String getPerformanceConfigEntryFileName() {
        return PerformanceConfigManager.get().getConfigEntryFileName();
    }
    public String getUtilityConfigEntryFileName() {
        return UtilityConfigManager.get().getConfigEntryFileName();
    }

    /**
     * IConfigDefaultable
     */
    public void generateAllDefaultConfigs() {
        generateDefaultPerformanceConfigs();
        generateDefaultUtilityConfigs();
    }
    public void generateDefaultPerformanceConfigs() {
        PerformanceConfigManager.get().generateDefaultConfigs();
    }
    public void generateDefaultUtilityConfigs() {
        UtilityConfigManager.get().generateDefaultConfigs();
    }

    /**
     * IConfigLoadable
     */
    public String getPerformanceConfigPath() {
        return String.valueOf(PerformanceConfigManager.get().getConfigDirPath());
    }
    public String getUtilityConfigPath() {
        return String.valueOf(UtilityConfigManager.get().getConfigDirPath());
    }

    /**
     * 特定类别的获取接口
     */
    public PerformanceConfig getPerformanceConfig(int id) {
        return PerformanceConfigManager.get().getPerformanceConfig(id);
    }
    public List<PerformanceConfig> getPerformanceConfigList() {
        return PerformanceConfigManager.get().getPerformanceConfigList();
    }
    public UtilityConfig getUtilityConfig(int id) {
        return UtilityConfigManager.get().getUtilityConfig(id);
    }
    public List<UtilityConfig> getUtilityConfigList() {
        return UtilityConfigManager.get().getUtilityConfigList();
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllConfigs() {
        reloadPerformanceConfigs();
        reloadUtilityConfigs();
    }
    public void reloadPerformanceConfigs() {
        PerformanceConfigManager.get().reloadPerformanceConfigs();
    }
    public void reloadUtilityConfigs() {
        UtilityConfigManager.get().reloadUtilityConfigs();
    }

    public boolean switchNextPerformanceConfig() {
        return PerformanceConfigManager.get().switchConfigFile();
    }
    public boolean switchPerformanceConfig(String fileName) {
        return PerformanceConfigManager.get().switchConfigFile(fileName);
    }
    public boolean switchNextUtilityConfig() {
        return UtilityConfigManager.get().switchConfigFile();
    }
    public boolean switchUtilityConfig(String fileName) {
        return UtilityConfigManager.get().switchConfigFile(fileName);
    }

    public boolean applyPerformanceConfig(int id) {
        PerformanceConfig performanceConfig = getPerformanceConfig(id);
        if (performanceConfig == null) {
            return false;
        }
        performanceConfig.applyDefault();
        return true;
    }
    public boolean applyUtilityConfig(int id) {
        UtilityConfig utilityConfig = getUtilityConfig(id);
        if (utilityConfig == null) {
            return false;
        }
        utilityConfig.applyDefault();
        return true;
    }

    public String getPerformanceConfigName(int id) {
        PerformanceConfig performanceConfig = getPerformanceConfig(id);
        return performanceConfig != null ? performanceConfig.name : "";
    }
    public String getUtilityConfigName(int id) {
        UtilityConfig utilityConfig = getUtilityConfig(id);
        return utilityConfig != null ? utilityConfig.name : "";
    }
}