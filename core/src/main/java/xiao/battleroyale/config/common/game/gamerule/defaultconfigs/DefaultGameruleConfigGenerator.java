package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

public class DefaultGameruleConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultGameruleConfigs(configDirPath);
    }

    public static void generateDefaultGameruleConfigs(String configDirPath) {
        DefaultGamerule.generateDefaultConfigs(configDirPath);
        PubgGamerule.generateDefaultConfigs(configDirPath);
    }
}