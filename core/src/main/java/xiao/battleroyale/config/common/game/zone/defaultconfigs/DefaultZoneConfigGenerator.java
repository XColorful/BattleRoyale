package xiao.battleroyale.config.common.game.zone.defaultconfigs;

public class DefaultZoneConfigGenerator {

    public static void generateAllDefaultConfigs() {
        generateDefaultZoneConfig();
    }

    public static void generateDefaultZoneConfig() {
        DefaultZone.generateDefaultConfigs();
        Pubg8000x8000Casual.generateDefaultConfigs();
        Pubg8000x8000Competitive.generateDefaultConfigs();
        Pubg5340x5340Casual.generateDefaultConfigs();
        Pubg881x881Casual.generateDefaultConfigs();
        FunctionShowcase.generateDefaultConfigs();
    }
}
