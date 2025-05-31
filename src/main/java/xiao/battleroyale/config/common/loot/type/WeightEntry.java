package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeightEntry implements ILootEntry {
    private final List<WeightedEntry> weightedEntries;

    public static class WeightedEntry {
        private final double weight;
        private final ILootEntry entry;

        public WeightedEntry(double weight, ILootEntry entry) {
            if (weight < 0) {
                weight = 0;
            }
            this.weight = weight;
            this.entry = entry;
        }
    }

    public static WeightedEntry createWeightedEntry(double weight, ILootEntry entry) {
        return new WeightedEntry(weight, entry);
    }

    public WeightEntry(List<WeightedEntry> weightedEntries) {
        this.weightedEntries = weightedEntries;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        double totalWeight = 0;
        for (WeightedEntry weightedEntry : weightedEntries) {
            totalWeight += weightedEntry.weight;
        }
        if (totalWeight <= 0) {
            return new ArrayList<>();
        }

        double randomNumber = random.get() * totalWeight;
        double currentWeight = 0;
        if (!weightedEntries.isEmpty()) {
            try {
                for (WeightedEntry weightedEntry : weightedEntries) {
                    currentWeight += weightedEntry.weight;
                    if (randomNumber < currentWeight) {
                        return weightedEntry.entry.generateLootData(random);
                    }
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse weight entry");
            }
        } else {
            BattleRoyale.LOGGER.warn("WeightEntry missing entries member, skipped");
        }
        BattleRoyale.LOGGER.warn("Unexpected WeightEntry loot result");
        return new ArrayList<>();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_WEIGHT;
    }

    public static WeightEntry fromJson(JsonObject jsonObject) {
        List<WeightedEntry> weightedEntries = new ArrayList<>();
        if (jsonObject.has(LootEntryTag.ENTRIES)) {
            JsonArray itemsArray = jsonObject.getAsJsonArray(LootEntryTag.ENTRIES);
            if (itemsArray != null) {
                for (JsonElement element : itemsArray) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject itemObject = element.getAsJsonObject();
                    double weight = itemObject.has(LootEntryTag.WEIGHT) ? itemObject.getAsJsonPrimitive(LootEntryTag.WEIGHT).getAsDouble() : 0;
                    if (itemObject.has(LootEntryTag.ENTRY)) {
                        JsonObject entryObject = itemObject.getAsJsonObject(LootEntryTag.ENTRY);
                        ILootEntry entry = JsonUtils.deserializeLootEntry(entryObject);
                        if (entry != null) {
                            weightedEntries.add(new WeightedEntry(weight, entry));
                        }
                    }
                }
            }
        }
        return new WeightEntry(weightedEntries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        JsonArray itemsArray = new JsonArray();
        for (WeightedEntry weightedEntry : weightedEntries) {
            JsonObject itemObject = new JsonObject();
            itemObject.addProperty(LootEntryTag.WEIGHT, weightedEntry.weight);
            if (weightedEntry.entry != null) {
                itemObject.add(LootEntryTag.ENTRY, weightedEntry.entry.toJson());
            }
            itemsArray.add(itemObject);
        }
        jsonObject.add(LootEntryTag.ENTRIES, itemsArray);
        return jsonObject;
    }
}