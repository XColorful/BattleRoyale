package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.cbraddon.CbrAddon;

public class DefaultSpawnConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultSpawnConfigs(configDirPath);
    }

    public static void generateDefaultSpawnConfigs(String configDirPath) {
        DefaultSpawn.generateDefaultConfigs(configDirPath);
        AirSpawn.generateDefaultConfigs(configDirPath);
        PubgSpawn.generateDefaultConfigs(configDirPath);
        FunctionShowcase.generateDefaultConfigs(configDirPath);
        if (BattleRoyale.getMcRegistry().isModLoaded(CbrAddon.get().getModId())) {
            CFHC1000x1000Spawn.generateDefaultConfigs(configDirPath);
        }
    }
}
