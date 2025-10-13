package xiao.battleroyale.config.common.effect.particle;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.effect.particle.IParticleEntry;
import xiao.battleroyale.api.game.effect.particle.ParticleConfigTag;
import xiao.battleroyale.common.effect.particle.ParticleData;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class ParticleDetailEntry implements IParticleEntry {

    public ResourceLocation particleType;
    public int count;
    public int initDelay;
    public int interval;
    public int repeat;
    public @NotNull Vec3 offset;
    public @NotNull Vec3 offsetRange;
    public boolean exactOffset;
    public @Nullable ParticleParameterEntry parameter;

    public ParticleDetailEntry(ResourceLocation particleType, int count, int initDelay, int interval, int repeat,
                               @Nullable Vec3 offset, @Nullable Vec3 offsetRange, boolean exactOffset,
                               @Nullable ParticleParameterEntry parameter) {
        this.particleType = particleType;
        this.count = Math.max(count, 0);
        this.initDelay = Math.max(initDelay, 0); // 初始延迟
        this.interval = Math.max(interval, 0);
        this.repeat = Math.max(repeat, 0);

        this.offset = offset != null ? offset : Vec3.ZERO;
        this.offsetRange = offsetRange != null ? offsetRange : Vec3.ZERO;
        this.exactOffset = exactOffset;

        this.parameter = parameter;
    }
    @Override public @NotNull ParticleDetailEntry copy() {
        return new ParticleDetailEntry(particleType, count, initDelay, interval, repeat,
                offset, offsetRange, exactOffset,
                parameter != null ? parameter.copy() : null);
    }

    @Override
    public String getType() {
        return "particleDetailEntry";
    }

    @Override
    public ParticleData createParticleData(ServerLevel serverLevel) {
        return new ParticleData(serverLevel, this.copy());
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ParticleConfigTag.TYPE, particleType.toString());
        jsonObject.addProperty(ParticleConfigTag.COUNT, count);
        jsonObject.addProperty(ParticleConfigTag.INIT_DELAY, initDelay);
        jsonObject.addProperty(ParticleConfigTag.INTERVAL, interval);
        jsonObject.addProperty(ParticleConfigTag.REPEAT, repeat);
        jsonObject.addProperty(ParticleConfigTag.OFFSET, StringUtils.vectorToString(offset));
        jsonObject.addProperty(ParticleConfigTag.OFFSET_RANGE, StringUtils.vectorToString(offsetRange));
        jsonObject.addProperty(ParticleConfigTag.EXACT_OFFSET, exactOffset);
        if (parameter != null) {
            jsonObject.add(ParticleConfigTag.PARAMETER, parameter.toJson());
        }

        return jsonObject;
    }

    public static ParticleDetailEntry fromJson(JsonObject jsonObject) {
        ResourceLocation particleRL = BattleRoyale.getMcRegistry().createResourceLocation(JsonUtils.getJsonString(jsonObject, ParticleConfigTag.TYPE, ""));
        if (particleRL == null || BattleRoyale.getMcRegistry().getParticleType(particleRL) == null) {
            return null;
        }
        int count = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.COUNT, 1);
        int initDelay = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.INIT_DELAY, 0);
        int interval = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.INTERVAL, 0);
        int repeat = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.REPEAT, 1);
        Vec3 offset = JsonUtils.getJsonVec(jsonObject, ParticleConfigTag.OFFSET, Vec3.ZERO);
        Vec3 offsetRange = JsonUtils.getJsonVec(jsonObject, ParticleConfigTag.OFFSET_RANGE, Vec3.ZERO);
        boolean exactOffset = JsonUtils.getJsonBool(jsonObject, ParticleConfigTag.EXACT_OFFSET, false);
        ParticleParameterEntry parameter = ParticleParameterEntry.fromJson(JsonUtils.getJsonObject(jsonObject, ParticleConfigTag.PARAMETER, null));

        return new ParticleDetailEntry(particleRL, count, initDelay, interval, repeat,
                offset, offsetRange, exactOffset,
                parameter);
    }
}
