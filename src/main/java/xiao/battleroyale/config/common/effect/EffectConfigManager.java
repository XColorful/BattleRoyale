package xiao.battleroyale.config.common.effect;

import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager.ParticleConfig;

import java.nio.file.Paths;
import java.util.List;

public class EffectConfigManager {

    public static final String EFFECT_CONFIG_SUB_PATH = "effect";
    public static final String EFFECT_CONFIG_PATH = Paths.get(AbstractConfigManager.MOD_CONFIG_PATH).resolve(EFFECT_CONFIG_SUB_PATH).toString();

    private static class EffectConfigManagerHolder {
        private static final EffectConfigManager INSTANCE = new EffectConfigManager();
    }

    public static EffectConfigManager get() { return EffectConfigManagerHolder.INSTANCE; }

    public static void init() {
        get();
        ParticleConfigManager.init();
    }

    /**
     * IConfigManager
     */
    public String getParticleConfigEntryFileName() {
        return ParticleConfigManager.get().getConfigEntryFileName();
    }

    /**
     * IConfigDefaultable
     */
    public void generateAllDefaultConfigs() {
        generateDefaultParticleConfigs();
    }
    public void generateDefaultParticleConfigs() {
        ParticleConfigManager.get().generateDefaultConfigs();
    }
    public void getDefaultParticleConfigID() {
        ParticleConfigManager.get().getDefaultConfigId();
    }
    public void setDefaultParticleConfigId(int id) {
        ParticleConfigManager.get().setDefaultConfigId(id);
    }

    /**
     * IConfigLoadable
     */
    public String getParticleConfigPath() {
        return String.valueOf(ParticleConfigManager.get().getConfigDirPath());
    }

    /**
     * 特定类别的获取接口
     */
    public ParticleConfig getParticleConfig(int id) {
        return ParticleConfigManager.get().getParticleConfig(id);
    }
    public List<ParticleConfig> getParticleConfigList() {
        return ParticleConfigManager.get().getAllParticleConfigs();
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllConfigs() {
        reloadParticleConfigs();
    }
    public void reloadParticleConfigs() {
        ParticleConfigManager.get().reloadParticleConfigs();
    }

    public boolean switchNextParticleConfig() {
        return ParticleConfigManager.get().switchConfigFile();
    }
    public boolean switchParticleConfig(String fileName) {
        return ParticleConfigManager.get().switchConfigFile(fileName);
    }
}
