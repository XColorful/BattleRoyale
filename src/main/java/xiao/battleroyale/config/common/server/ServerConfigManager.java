package xiao.battleroyale.config.common.server;

import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager.PerformanceConfig;

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

    /**
     * IConfigDefaultable
     */
    public void generateAllDefaultConfigs() {
        generateDefaultPerformanceConfigs();
    }
    public void generateDefaultPerformanceConfigs() {
        PerformanceConfigManager.get().generateDefaultConfigs();
    }

    /**
     * IConfigLoadable
     */
    public String getPerformanceConfigPath() {
        return String.valueOf(PerformanceConfigManager.get().getConfigDirPath());
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

    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllConfigs() {
        reloadPerformanceConfigs();
    }
    public void reloadPerformanceConfigs() {
        PerformanceConfigManager.get().reloadPerformanceConfigs();
    }

    public boolean switchNextPerformanceConfig() {
        return PerformanceConfigManager.get().switchConfigFile();
    }
    public boolean switchPerformanceConfig(String fileName) {
        return PerformanceConfigManager.get().switchConfigFile(fileName);
    }

    public boolean applyPerformanceConfig(int id) {
        PerformanceConfig performanceConfig = getPerformanceConfig(id);
        if (performanceConfig == null) {
            return false;
        }
        performanceConfig.applyDefault();
        return true;
    }

    public String getPerformanceConfigName(int id) {
        PerformanceConfig performanceConfig = getPerformanceConfig(id);
        return performanceConfig != null ? performanceConfig.name : "";
    }
}