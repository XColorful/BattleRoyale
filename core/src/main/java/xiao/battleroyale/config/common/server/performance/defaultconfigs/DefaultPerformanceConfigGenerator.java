package xiao.battleroyale.config.common.server.performance.defaultconfigs;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.cbraddon.CbrAddon;

public class DefaultPerformanceConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultPerformanceConfig(configDirPath);
    }

    public static void generateDefaultPerformanceConfig(String configDirPath) {
        DefaultPerformance.generateDefaultConfigs(configDirPath);
        if (BattleRoyale.getMcRegistry().isModLoaded(CbrAddon.get().getModId())) {
            CFHCPerformance.generateDefaultConfigs(configDirPath);
        }
    }
}