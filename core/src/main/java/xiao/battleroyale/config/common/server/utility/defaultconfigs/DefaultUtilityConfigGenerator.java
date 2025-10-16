package xiao.battleroyale.config.common.server.utility.defaultconfigs;

public class DefaultUtilityConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultUtilityConfig(configDirPath);
    }

    public static void generateDefaultUtilityConfig(String configDirPath) {
        DefaultUtility.generateDefaultConfigs(configDirPath);
    }
}
