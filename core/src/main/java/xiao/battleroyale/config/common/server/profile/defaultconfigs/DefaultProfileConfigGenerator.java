package xiao.battleroyale.config.common.server.profile.defaultconfigs;

public class DefaultProfileConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultProfileConfig(configDirPath);
    }

    public static void generateDefaultProfileConfig(String configDirPath) {
        DefaultProfile.generateDefaultConfigs(configDirPath);
    }
}
