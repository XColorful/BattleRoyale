package xiao.battleroyale.config.common.effect.particle.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager.ParticleConfig;
import xiao.battleroyale.config.common.effect.particle.ParticleDetailEntry;
import xiao.battleroyale.config.common.effect.particle.ParticleParameterEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultParticle {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray particleConfigJson = new JsonArray();
        particleConfigJson.add(generateDefaultParticleConfig0());
        particleConfigJson.add(generateDefaultParticleConfig1());
        writeJsonToFile(Paths.get(EffectConfigManager.get().getParticleConfigPath(), DEFAULT_FILE_NAME).toString(), particleConfigJson);
    }

    private static JsonObject generateDefaultParticleConfig0() {
        ParticleDetailEntry detailEntry = new ParticleDetailEntry(new ResourceLocation("minecraft:explosion"),
                10, 200, null, 20, 20, 10);

        ParticleConfig particleConfig = new ParticleConfig(0, "Simple particle", "#FFFFFF", detailEntry);

        return particleConfig.toJson();
    }

    private static JsonObject generateDefaultParticleConfig1() {
        ParticleParameterEntry parameterEntry = new ParticleParameterEntry(0.1F, new Vec3(1, 0, 1),
                "#0000FF", 1, 0, new Vec3(0, 1, 0), null);

        ParticleDetailEntry detailEntry = new ParticleDetailEntry(new ResourceLocation("minecraft:dust"),
                10, 200, parameterEntry, 20, 20, 10);

        ParticleConfig particleConfig = new ParticleConfig(1, "Particle with parameter", "#FFFFFF", detailEntry);

        return particleConfig.toJson();
    }
}
