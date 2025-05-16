package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeightEntry<T> implements ILootEntry<T> {
    private final List<WeightedEntry> weightedEntries;

    public static class WeightedEntry {
        double weight;
        ILootEntry<?> entry;

        public WeightedEntry(double weight, ILootEntry<?> entry) {
            this.weight = weight;
            this.entry = entry;
        }
    }

    public static WeightedEntry createWeightedEntry(double weight, ILootEntry<?> entry) {
        return new WeightedEntry(weight, entry);
    }

    public WeightEntry(List<WeightedEntry> weightedEntries) {
        this.weightedEntries = weightedEntries;
    }

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        double totalWeight = 0;
        for (WeightedEntry weightedEntry : weightedEntries) {
            totalWeight += weightedEntry.weight;
        }

        if (totalWeight <= 0) {
            return new ArrayList<>();
        }

        double randomNumber = random.get() * totalWeight;
        double currentWeight = 0;

        for (WeightedEntry weightedEntry : weightedEntries) {
            currentWeight += weightedEntry.weight;
            if (randomNumber < currentWeight) {
                if (weightedEntry.entry instanceof IItemLootEntry) {
                    return (List<T>) ((IItemLootEntry) weightedEntry.entry).generateLoot(random);
                } else if (weightedEntry.entry instanceof IEntityLootEntry) {
                    return (List<T>) ((IEntityLootEntry) weightedEntry.entry).generateLoot(random);
                }
                break;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String getType() {
        return "weight";
    }

    public static WeightEntry<?> fromJson(JsonObject jsonObject) {
        JsonArray itemsArray = jsonObject.getAsJsonArray("entries");
        List<WeightedEntry> weightedEntries = new ArrayList<>();
        if (itemsArray != null) {
            for (com.google.gson.JsonElement element : itemsArray) {
                if (element.isJsonObject()) {
                    JsonObject itemObject = element.getAsJsonObject();
                    double weight = itemObject.getAsJsonPrimitive("weight").getAsDouble();
                    JsonObject entryObject = itemObject.getAsJsonObject("entry");
                    ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
                    if (entry != null) {
                        weightedEntries.add(new WeightedEntry(weight, entry));
                    }
                }
            }
        }
        return new WeightEntry<>(weightedEntries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        com.google.gson.JsonArray itemsArray = new com.google.gson.JsonArray();
        for (WeightedEntry weightedEntry : weightedEntries) {
            JsonObject itemObject = new JsonObject();
            itemObject.addProperty("weight", weightedEntry.weight);
            itemObject.add("entry", weightedEntry.entry.toJson());
            itemsArray.add(itemObject);
        }
        jsonObject.add("entries", itemsArray);
        return jsonObject;
    }
}