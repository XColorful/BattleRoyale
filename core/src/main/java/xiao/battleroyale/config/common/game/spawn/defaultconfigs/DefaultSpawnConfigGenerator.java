package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

public class DefaultSpawnConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultSpawnConfigs(configDirPath);
    }

    public static void generateDefaultSpawnConfigs(String configDirPath) {
        DefaultSpawn.generateDefaultConfigs(configDirPath);
        AirSpawn.generateDefaultConfigs(configDirPath);
        PubgSpawn.generateDefaultConfigs(configDirPath);
        FunctionShowcase.generateDefaultConfigs(configDirPath);
    }
}
