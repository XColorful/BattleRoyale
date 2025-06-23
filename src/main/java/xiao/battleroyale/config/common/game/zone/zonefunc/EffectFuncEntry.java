package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.EffectFunc;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class EffectFuncEntry extends AbstractFuncEntry {

    private final List<EffectEntry> effects;

    public EffectFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset, List<EffectEntry> effects) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.effects = effects;
    }

    @Override
    public String getType() {
        return ZoneFuncTag.EFFECT;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new EffectFunc(moveDelay, moveTime, tickFreq, tickOffset, effects);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.TICK_FREQUENCY, tickFreq);
        jsonObject.addProperty(ZoneFuncTag.TICK_OFFSET, tickOffset);

        JsonArray effectArray = new JsonArray();
        for (EffectEntry effectEntry : effects) {
            effectArray.add(effectEntry.toJson());
        }
        jsonObject.add(ZoneFuncTag.EFFECTS, effectArray);

        return jsonObject;
    }

    public static EffectFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        List<EffectEntry> effects = new ArrayList<>();
        JsonArray effectArray = JsonUtils.getJsonArray(jsonObject, ZoneFuncTag.EFFECTS, null);
        if (effectArray != null) {
            for (JsonElement element : effectArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                EffectEntry entry = EffectEntry.fromJson(element.getAsJsonObject());
                if (entry != null) {
                    effects.add(entry);
                }
            }
        }

        return new EffectFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, effects);
    }

    public record EffectEntry(@NotNull ResourceLocation effectRL, @NotNull MobEffect type, int duration, int level) {

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty(ZoneFuncTag.EFFECT_TYPE, effectRL.toString());
            jsonObject.addProperty(ZoneFuncTag.EFFECT_DURATION, duration);
            jsonObject.addProperty(ZoneFuncTag.EFFECT_LEVEL, level);

            return jsonObject;
        }

        public static EffectEntry fromJson(JsonObject jsonObject) {
            ResourceLocation rl = ResourceLocation.tryParse(JsonUtils.getJsonString(jsonObject, ZoneFuncTag.EFFECT_TYPE, ""));
            if (rl == null) {
                return null;
            }
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(rl);
            if (effect == null) {
                return null;
            }
            int duration = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.EFFECT_DURATION, 20);
            int level = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.EFFECT_LEVEL, 0);

            return new EffectEntry(rl, effect, duration, level);
        }
    }
}
