package xiao.battleroyale.config.common.game.zone.defaultconfigs;

public class DefaultZoneConfigGenerator {

    public static boolean generateAllDefaultConfigs(String configDirPath) {
        generateDefaultZoneConfig(configDirPath);
        return true;
    }

    public static void generateDefaultZoneConfig(String configDirPath) {
        DefaultZone.generateDefaultConfigs(configDirPath);
        ElytraAddon.generateDefaultConfigs(configDirPath);
        Pubg8000x8000Casual.generateDefaultConfigs(configDirPath);
        Pubg8000x8000Competitive.generateDefaultConfigs(configDirPath);
        Pubg5340x5340Casual.generateDefaultConfigs(configDirPath);
        Pubg881x881Casual.generateDefaultConfigs(configDirPath);
        FunctionShowcase.generateDefaultConfigs(configDirPath);
        ModCover.generateDefaultConfigs(configDirPath);
    }
}
