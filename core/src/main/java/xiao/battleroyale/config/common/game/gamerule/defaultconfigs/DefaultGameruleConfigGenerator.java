package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.cbraddon.CbrAddon;

public class DefaultGameruleConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultGameruleConfigs(configDirPath);
    }

    public static void generateDefaultGameruleConfigs(String configDirPath) {
        DefaultGamerule.generateDefaultConfigs(configDirPath);
        PubgGamerule.generateDefaultConfigs(configDirPath);
        DeathMatchGamerule.generateDefaultConfigs(configDirPath);
        if (BattleRoyale.getMcRegistry().isModLoaded(CbrAddon.get().getModId())) {
            CFHCGamerule.generateDefaultConfigs(configDirPath);
        }
    }
}