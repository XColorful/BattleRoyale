package xiao.battleroyale.config.common.game.bot.defaultconfigs;

public class DefaultBotConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultBotConfigs(configDirPath);
    }

    public static void generateDefaultBotConfigs(String configDirPath) {
        DefaultBot.generateDefaultConfigs(configDirPath);
    }
}
