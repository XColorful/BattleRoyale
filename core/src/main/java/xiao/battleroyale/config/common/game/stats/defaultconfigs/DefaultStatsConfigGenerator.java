package xiao.battleroyale.config.common.game.stats.defaultconfigs;

public class DefaultStatsConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultStatsConfigs(configDirPath);
    }

    public static void generateDefaultStatsConfigs(String configDirPath) {
        DefaultStats.generateDefaultConfigs(configDirPath);
        BattleRoyaleStats.generateDefaultConfigs(configDirPath);
        DeathMatchStats.generateDefaultConfigs(configDirPath);
    }
}
