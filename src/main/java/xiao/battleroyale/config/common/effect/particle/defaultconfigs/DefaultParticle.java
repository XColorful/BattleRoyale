package xiao.battleroyale.config.common.effect.particle.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.common.effect.EffectConfigManager;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultParticle {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray particleConfigJson = new JsonArray();
        particleConfigJson.add(generateDefaultParticleConfig0());

        writeJsonToFile(Paths.get(EffectConfigManager.get().getParticleConfigPath(), DEFAULT_FILE_NAME).toString(), particleConfigJson);
    }

    private static JsonObject generateDefaultParticleConfig0() {
        JsonObject config = new JsonObject();

        return config;
    }
}
