package xiao.battleroyale.config.common.effect.particle.defaultconfigs;

public class DefaultParticleConfigGenerator {

    public static void generateAllDefaultConfigs(String configDirPath) {
        generateDefaultParticleConfigs(configDirPath);
    }

    public static void generateDefaultParticleConfigs(String configDirPath) {
        DefaultParticle.generateDefaultConfigs(configDirPath);
    }
}
