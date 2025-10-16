package xiao.battleroyale.config.common.server.performance.defaultconfigs;

public class DefaultPerformanceConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultPerformanceConfig(configDirPath);
    }

    public static void generateDefaultPerformanceConfig(String configDirPath) {
        DefaultPerformance.generateDefaultConfigs(configDirPath);
    }
}