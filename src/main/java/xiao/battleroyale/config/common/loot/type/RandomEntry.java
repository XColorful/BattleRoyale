package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class RandomEntry<T> implements ILootEntry<T> {
    private final double chance;
    private final ILootEntry<?> entry;

    public RandomEntry(double chance, ILootEntry<?> entry) {
        this.chance = chance;
        this.entry = entry;
    }

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        if (random.get() < chance) {
            if (entry instanceof IItemLootEntry) {
                return (List<T>) ((IItemLootEntry) entry).generateLoot(random);
            } else if (entry instanceof IEntityLootEntry) {
                return (List<T>) ((IEntityLootEntry) entry).generateLoot(random);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return "random";
    }

    public static RandomEntry<?> fromJson(JsonObject jsonObject) {
        double chance = jsonObject.getAsJsonPrimitive("chance").getAsDouble();
        JsonObject entryObject = jsonObject.getAsJsonObject("entry");
        ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
        return new RandomEntry<>(chance, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("chance", this.chance);
        jsonObject.add("entry", this.entry.toJson());
        return jsonObject;
    }
}