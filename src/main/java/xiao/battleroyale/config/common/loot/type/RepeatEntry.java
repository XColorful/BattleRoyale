package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RepeatEntry<T> implements ILootEntry<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatEntry.class);
    private final int min;
    private final int max;
    private final ILootEntry<?> entry;

    public RepeatEntry(int min, int max, ILootEntry<?> entry) {
        this.min = min;
        this.max = max;
        this.entry = entry;
    }

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        int repeats = min;
        if (max > min) {
            repeats += (int) (random.get() * (max - min + 1));
        } else if (max < min) {
            LOGGER.warn("RepeatEntry min ({}) is greater than max ({}), defaulting to min.", min, max);
        }

        List<T> allLoot = new ArrayList<>();
        for (int i = 0; i < repeats; i++) {
            if (entry instanceof IItemLootEntry) {
                allLoot.addAll((List<T>) ((IItemLootEntry) entry).generateLoot(random));
            } else if (entry instanceof IEntityLootEntry) {
                allLoot.addAll((List<T>) ((IEntityLootEntry) entry).generateLoot(random));
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
        JsonObject entryObject = jsonObject.getAsJsonObject("entry");
        ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
        return new RepeatEntry<>(min, max, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        if (min > 0) {
            jsonObject.addProperty("min", this.min);
        }
        if (max > 0) {
            jsonObject.addProperty("max", this.max);
        }
        jsonObject.add("entry", this.entry.toJson());
        return jsonObject;
    }
}