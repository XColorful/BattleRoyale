package xiao.battleroyale.config.common.effect.particle;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import xiao.battleroyale.api.config.sub.IConfigEntry;
import xiao.battleroyale.api.game.effect.particle.ParticleConfigTag;
import xiao.battleroyale.util.JsonUtils;

public class ParticleParameterEntry implements IConfigEntry {

    public float speed;
    public String color;
    public float scale;
    public int note;
    public CompoundTag nbt;

    public ParticleParameterEntry(float speed, String color, float scale, int note, CompoundTag nbt) {
        this.speed = speed;
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
        String color = JsonUtils.getJsonString(jsonObject, ParticleConfigTag.COLOR, "");
        float scale = (float) JsonUtils.getJsonDouble(jsonObject, ParticleConfigTag.SCALE, 1);
        int note = JsonUtils.getJsonInt(jsonObject, ParticleConfigTag.NOTE, 0);
        CompoundTag nbt = JsonUtils.getJsonNBT(jsonObject, ParticleConfigTag.NBT);

        return new ParticleParameterEntry(speed, color, scale, note, nbt);
    }
}
