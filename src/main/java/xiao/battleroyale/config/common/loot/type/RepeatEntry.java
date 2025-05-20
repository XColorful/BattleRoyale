package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RepeatEntry<T> implements ILootEntry<T> {
    private final int min;
    private final int max;
    private final ILootEntry<?> entry;

    public RepeatEntry(int min, int max, ILootEntry<?> entry) {
        if (min < 0) {
            BattleRoyale.LOGGER.warn("RepeatEntry min ({}) is lower than 0, defaulting to 0", min);
            min = 0;
        }
        this.min = min;
        if (max < min) {
            BattleRoyale.LOGGER.warn("RepeatEntry min ({}) is greater than max ({}), defaulting to min.", this.min, max);
            max = min;
        }
        this.max = max;
        this.entry = entry;
    }

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        int repeats = min + (int) (random.get() * (max - min + 1));
        List<T> allLoot = new ArrayList<>();
        if (entry != null) {
            for (int i = 0; i < repeats; i++) {
                try {
                    allLoot.addAll((List<T>) entry.generateLoot(random));
                } catch (Exception e) {
                    BattleRoyale.LOGGER.warn("Failed to parse repeat entry");
                }
            }
        }
        return allLoot;
    }

    @Override
    public String getType() {
        return "repeat";
    }

    public static RepeatEntry<?> fromJson(JsonObject jsonObject) {
        int min = jsonObject.has("min") ? jsonObject.getAsJsonPrimitive("min").getAsInt() : 0;
        int max = jsonObject.has("max") ? jsonObject.getAsJsonPrimitive("max").getAsInt() : 0;
        if (jsonObject.has("entry")) {
            JsonObject entryObject = jsonObject.getAsJsonObject("entry");
            ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
            return new RepeatEntry<>(min, max, entry);
        } else {
            BattleRoyale.LOGGER.warn("RepeatEntry missing entry member, skipped");
            return new RepeatEntry<>(min, max, null);
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        if (min >= 0) {
            jsonObject.addProperty("min", this.min);
        }
        if (max >= 0) {
            jsonObject.addProperty("max", this.max);
        }
        if (entry != null) {
            jsonObject.add("entry", this.entry.toJson());
        }
        return jsonObject;
    }
}