package xiao.battleroyale.config.common.loot.defaultconfigs;

public class DefaultLootConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultLootSpawnerConfig(configDirPath);
        generateDefaultAirdropConfig(configDirPath);
        generateDefaultAirdropSpecialConfig(configDirPath);
        generateDefaultEntitySpawnerConfig(configDirPath);
        generateDefaultSecretRoomConfig(configDirPath);
    }

    public static void generateDefaultLootSpawnerConfig(String configDirPath) {
        DefaultLootSpawner.generateDefaultConfigs(configDirPath);
    }

    public static void generateDefaultAirdropConfig(String configDirPath) {
        DefaultAirdrop.generateDefaultConfigs(configDirPath);
    }

    public static void generateDefaultAirdropSpecialConfig(String configDirPath) {
        DefaultAirdropSpecial.generateDefaultConfigs(configDirPath);
    }

    public static void generateDefaultEntitySpawnerConfig(String configDirPath) {
        DefaultEntitySpawner.generateDefaultConfigs(configDirPath);
        HorseVehicleEntitySpawner.generateDefaultConfigs(configDirPath);
    }

    public static void generateDefaultSecretRoomConfig(String configDirPath) {
        DefaultSecretRoom.generateDefaultConfigs(configDirPath);
    }
}