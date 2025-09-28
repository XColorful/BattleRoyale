package xiao.battleroyale.config.common.loot.defaultconfigs;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.tacz.Tacz;

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
        if (BattleRoyale.getMcRegistry().isModLoaded(Tacz.get().getModId())) {
            TaczLootSpawner.generateDefaultConfigs();
            TaczLootSpawner.generateExtraConfigs();
            CbrgLootSpawner.generateDefaultConfigs();
            CbrgLootSpawner.generateExtraConfigs();
        }
    }

    public static void generateDefaultAirdropConfig() {
        DefaultAirdrop.generateDefaultConfigs();
    }

    public static void generateDefaultAirdropSpecialConfig() {
        DefaultAirdropSpecial.generateDefaultConfigs();
    }

    public static void generateDefaultEntitySpawnerConfig() {
        DefaultEntitySpawner.generateDefaultConfigs();
        HorseVehicleEntitySpawner.generateDefaultConfigs();
    }

    public static void generateDefaultSecretRoomConfig() {
        DefaultSecretRoom.generateDefaultConfigs();
    }
}