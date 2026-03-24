package xiao.battleroyale.config.common.server.function.defaultconfigs;

public class DefaultFunctionConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultFunctionConfig(configDirPath);
    }

    public static void generateDefaultFunctionConfig(String configDirPath) {
        DefaultFunction.generateDefaultConfigs(configDirPath);
        AllFunction.generateDefaultConfigs(configDirPath);
    }
}