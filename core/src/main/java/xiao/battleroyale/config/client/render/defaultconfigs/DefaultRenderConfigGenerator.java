package xiao.battleroyale.config.client.render.defaultconfigs;

public class DefaultRenderConfigGenerator {

    public static void generateAllDefaultConfig(String configDitPath) {
        generateDefaultRenderConfigs(configDitPath);
    }

    public static void generateDefaultRenderConfigs(String configDirPath) {
        DefaultRender.generateDefaultConfigs(configDirPath);
    }
}