package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.EffectFunc;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class EffectFuncEntry extends AbstractFuncEntry {

    public final List<EffectEntry> effectEntries;

    public EffectFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset, List<EffectEntry> effectEntries) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.effectEntries = effectEntries;
    }

    @Override
    public String getType() {
        return ZoneFuncTag.EFFECT;
    }

    @Override
    public ITickableZone createTickableZone() {
        return this.build();
    }
    public EffectFunc build() {
        List<Effect> effects = new ArrayList<>();
        for (EffectEntry effectEntry : effectEntries) {
            ResourceLocation rl = ResourceLocation.tryParse(effectEntry.rlString);
            if (rl == null) {
                continue;
            }
            MobEffect effect = BattleRoyale.getMcRegistry().getMobEffect(rl);
            if (effect == null) {
                continue;
            }
            effects.add(new Effect(rl, effect, effectEntry.duration, effectEntry.level));
        }
        return new EffectFunc(moveDelay, moveTime, tickFreq, tickOffset, effects);
    }
    public record Effect(@NotNull ResourceLocation effectRL, @NotNull MobEffect type, int duration, int level) {}

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.TICK_FREQUENCY, tickFreq);
        jsonObject.addProperty(ZoneFuncTag.TICK_OFFSET, tickOffset);

        JsonArray effectArray = new JsonArray();
        for (EffectEntry effectEntry : effectEntries) {
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

        EffectFuncEntryBuilder builder = new EffectFuncEntryBuilder(moveDelay, moveTime, tickFreq, tickOffset);
        JsonArray effectArray = JsonUtils.getJsonArray(jsonObject, ZoneFuncTag.EFFECTS, null);
        if (effectArray != null) {
            for (JsonElement element : effectArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                EffectEntry entry = EffectEntry.fromJson(element.getAsJsonObject());

                builder.add(entry);
            }
        }

        return builder.build();
    }

    public record EffectEntry(String rlString, int duration, int level) {
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty(ZoneFuncTag.EFFECT_TYPE, rlString);
            jsonObject.addProperty(ZoneFuncTag.EFFECT_DURATION, duration);
            jsonObject.addProperty(ZoneFuncTag.EFFECT_LEVEL, level);

            return jsonObject;
        }
        public static EffectEntry fromJson(JsonObject jsonObject) {
            String rlString = JsonUtils.getJsonString(jsonObject, ZoneFuncTag.EFFECT_TYPE, "");
            int duration = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.EFFECT_DURATION, 20);
            int level = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.EFFECT_LEVEL, 0);
            return new EffectEntry(rlString, duration, level);
        }
    }

    public static class EffectFuncEntryBuilder {
        private final int moveDelay;
        private final int moveTime;
        private final int tickFreq;
        private final int tickOffset;
        private final List<EffectEntry> effectEntries = new ArrayList<>();
        public EffectFuncEntryBuilder(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
            this.moveDelay = moveDelay;
            this.moveTime = moveTime;
            this.tickFreq = tickFreq;
            this.tickOffset = tickOffset;
        }
        public EffectFuncEntryBuilder add(String rlString, int duration, int level) {
            this.add(new EffectEntry(rlString, duration, level));
            return this;
        }
        public EffectFuncEntryBuilder add(EffectEntry effectEntry) {
            effectEntries.add(effectEntry);
            return this;
        }
        public EffectFuncEntry build() {
            return new EffectFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, effectEntries);
        }
    }
}
