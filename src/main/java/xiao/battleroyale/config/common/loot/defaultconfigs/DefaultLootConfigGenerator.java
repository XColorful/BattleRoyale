package xiao.battleroyale.config.common.loot.defaultconfigs;

public class DefaultLootConfigGenerator {

    public static void generateDefaultConfigs() {
        DefaultLootSpawner.generateDefaultConfigs();
        DefaultAirdrop.generateDefaultConfigs();
        DefaultAirdropSpecial.generateDefaultConfigs();
        DefaultEntitySpawner.generateDefaultConfigs();
        DefaultSecretRoom.generateDefaultConfigs();
    }

    public static void generateDefaultLootSpawnerConfig() {
        DefaultLootSpawner.generateDefaultConfigs();
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