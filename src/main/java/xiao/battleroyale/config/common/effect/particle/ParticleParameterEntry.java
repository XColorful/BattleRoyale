package xiao.battleroyale.config.common.effect.particle;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.IConfigEntry;
import xiao.battleroyale.api.game.effect.particle.ParticleConfigTag;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public record ParticleParameterEntry(float speed, @NotNull Vec3 offset, @NotNull Vec3 offsetRange, boolean exactOffset,
                                     String color, float scale,
                                     int note,
                                     CompoundTag nbt) implements IConfigEntry {

    public ParticleParameterEntry(float speed, Vec3 offset, Vec3 offsetRange, boolean exactOffset,
                                  String color, float scale,
                                  int note,
                                  CompoundTag nbt) {
        this.speed = speed;
        this.offset = offset != null ? offset : Vec3.ZERO;
        this.offsetRange = offsetRange != null ? offsetRange : Vec3.ZERO;
        this.exactOffset = exactOffset;
        // dust尘埃粒子，dust_color_transition彩色尘埃过渡粒子
        this.color = color;
        this.scale = scale;
        // note音符粒子
        this.note = note;
        // 自定义粒子预留
        this.nbt = nbt != null ? nbt : new CompoundTag();
    }

    @Override
    public String getType() {
        return "particleParameterEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ParticleConfigTag.SPEED, speed);
        jsonObject.addProperty(ParticleConfigTag.OFFSET, StringUtils.vectorToString(offset));
        jsonObject.addProperty(ParticleConfigTag.OFFSET_RANGE, StringUtils.vectorToString(offsetRange));
        jsonObject.addProperty(ParticleConfigTag.COLOR, color);
        jsonObject.addProperty(ParticleConfigTag.SCALE, scale);
        jsonObject.addProperty(ParticleConfigTag.NOTE, note);
        jsonObject.addProperty(ParticleConfigTag.NBT, nbt.toString());

        return jsonObject;
    }

    public static ParticleParameterEntry fromJson(JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        float speed = (float) JsonUtils.getJsonDouble(jsonObject, ParticleConfigTag.SPEED, 0);
        Vec3 offset = JsonUtils.getJsonVec(jsonObject, ParticleConfigTag.OFFSET, Vec3.ZERO);
        Vec3 offsetRange = JsonUtils.getJsonVec(jsonObject, ParticleConfigTag.OFFSET_RANGE, Vec3.ZERO);
        boolean exactOffset = JsonUtils.getJsonBoolean(jsonObject, ParticleConfigTag.EXACT_OFFSET, false);
        String color = JsonUtils.getJsonString(jsonObject, ParticleConfigTag.COLOR, "");
        float scale = (float) JsonUtils.getJsonDouble(jsonObject, ParticleConfigTag.SCALE, 1);
        int note = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.NOTE, 0);
        CompoundTag nbt = JsonUtils.getJsonNBT(jsonObject, ParticleConfigTag.NBT, new CompoundTag());

        return new ParticleParameterEntry(speed, offset, offsetRange, exactOffset, color, scale, note, nbt);
    }
}
