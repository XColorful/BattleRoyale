package xiao.battleroyale.config.common.loot.defaultconfigs;

public class DefaultLootConfigGenerator {

    public static void generateAllDefaultConfigs() {
        generateDefaultLootSpawnerConfig();
        generateDefaultAirdropConfig();
        generateDefaultAirdropSpecialConfig();
        generateDefaultEntitySpawnerConfig();
        generateDefaultSecretRoomConfig();
    }

    public static void generateDefaultLootSpawnerConfig() {
        DefaultLootSpawner.generateDefaultConfigs();
        TaczLootSpawner.generateDefaultConfigs();
    }

    public static void generateDefaultAirdropConfig() {
        DefaultAirdrop.generateDefaultConfigs();
    }

    public static void generateDefaultAirdropSpecialConfig() {
        DefaultAirdropSpecial.generateDefaultConfigs();
    }

    public static void generateDefaultEntitySpawnerConfig() {
        DefaultEntitySpawner.generateDefaultConfigs();
    }

    public static void generateDefaultSecretRoomConfig() {
        DefaultSecretRoom.generateDefaultConfigs();
    }
}