package xiao.battleroyale.config.common.loot.defaultconfigs;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.cbraddon.CbrAddon;
import xiao.battleroyale.compat.murdermystery.MurderMystery;
import xiao.battleroyale.compat.tacz.Tacz;

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
        LootSpecificBlock.generateDefaultConfigs(configDirPath);
        if (BattleRoyale.getMcRegistry().isModLoaded(CbrAddon.get().getModId())) {
            CFHCLootSpawner.generateDefaultConfigs(configDirPath);
        }
        if (BattleRoyale.getMcRegistry().isModLoaded(MurderMystery.get().getModId())) {
            MurderMysteryLootSpawner.generateDefaultConfigs(configDirPath);
        }
        if (BattleRoyale.getMcRegistry().isModLoaded(Tacz.get().getModId())) {
            TaczLootSpawner.generateDefaultConfigs(configDirPath);
            TaczLootSpawner.generateExtraConfigs(configDirPath);
            CbrgLootSpawner.generateDefaultConfigs(configDirPath);
            CbrgLootSpawner.generateExtraConfigs(configDirPath);
        }
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
        if (BattleRoyale.getMcRegistry().isModLoaded(CbrAddon.get().getModId())) {
            CFHCEntitySpawner.generateDefaultConfigs(configDirPath);
        }
    }

    public static void generateDefaultSecretRoomConfig(String configDirPath) {
        DefaultSecretRoom.generateDefaultConfigs(configDirPath);
    }
}