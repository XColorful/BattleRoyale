package xiao.battleroyale.config.common.effect.particle;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.effect.particle.IParticleEntry;
import xiao.battleroyale.api.game.effect.particle.ParticleConfigTag;
import xiao.battleroyale.common.game.effect.particle.ParticleData;
import xiao.battleroyale.util.JsonUtils;

public record ParticleDetailEntry(ResourceLocation particleType, int count, int lifeTime,
                                  @Nullable ParticleParameterEntry parameter,
                                  int initDelay, int interval, int repeat) implements IParticleEntry {

    public ParticleDetailEntry(ResourceLocation particleType, int count, int lifeTime,
                               @Nullable ParticleParameterEntry parameter,
                               int initDelay, int interval, int repeat) {
        this.particleType = particleType;
        this.count = Math.max(count, 0);
        this.lifeTime = Math.max(lifeTime, 0);

        this.parameter = parameter;

        this.initDelay = Math.max(initDelay, 0); // 初始延迟
        this.interval = Math.max(interval, 0);
        this.repeat = Math.max(repeat, 0);
    }

    @Override
    public String getType() {
        return "particleDetailEntry";
    }

    @Override
    public ParticleData createParticleData(ServerLevel serverLevel) {
        return new ParticleData(serverLevel, this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ParticleConfigTag.TYPE, particleType.toString());
        jsonObject.addProperty(ParticleConfigTag.COUNT, count);
        jsonObject.addProperty(ParticleConfigTag.LIFE, lifeTime);
        jsonObject.addProperty(ParticleConfigTag.INIT_DELAY, initDelay);
        jsonObject.addProperty(ParticleConfigTag.INTERVAL, interval);
        jsonObject.addProperty(ParticleConfigTag.REPEAT, repeat);
        if (parameter != null) {
            jsonObject.add(ParticleConfigTag.PARAMETER, parameter.toJson());
        }

        return jsonObject;
    }

    public static ParticleDetailEntry fromJson(JsonObject jsonObject) {
        ResourceLocation particleRL = ResourceLocation.tryParse(JsonUtils.getJsonString(jsonObject, ParticleConfigTag.TYPE, ""));
        if (particleRL == null || ForgeRegistries.PARTICLE_TYPES.getValue(particleRL) == null) {
            return null;
        }
        int count = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.COUNT, 1);
        int lifeTime = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.LIFE, 20);
        int initDelay = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.INIT_DELAY, 0);
        int interval = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.INTERVAL, 0);
        int repeat = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.REPEAT, 1);
        ParticleParameterEntry parameter = ParticleParameterEntry.fromJson(JsonUtils.getJsonObject(jsonObject, ParticleConfigTag.PARAMETER, null));

        return new ParticleDetailEntry(particleRL, count, lifeTime,
                parameter,
                initDelay, interval, repeat);
    }
}
