package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
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
                BattleRoyale.LOGGER.warn("WeightedEntry weight ({}) is lower than 0, defaulting to 0", weight);
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
        return "weight";
    }

    public static WeightEntry fromJson(JsonObject jsonObject) {
        List<WeightedEntry> weightedEntries = new ArrayList<>();
        if (jsonObject.has("entries")) {
            JsonArray itemsArray = jsonObject.getAsJsonArray("entries");
            if (itemsArray != null) {
                for (JsonElement element : itemsArray) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject itemObject = element.getAsJsonObject();
                    double weight = itemObject.has("weight") ? itemObject.getAsJsonPrimitive("weight").getAsDouble() : 0;
                    if (itemObject.has("entry")) {
                        JsonObject entryObject = itemObject.getAsJsonObject("entry");
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
        jsonObject.addProperty("type", getType());
        JsonArray itemsArray = new JsonArray();
        for (WeightedEntry weightedEntry : weightedEntries) {
            JsonObject itemObject = new JsonObject();
            itemObject.addProperty("weight", weightedEntry.weight);
            if (weightedEntry.entry != null) {
                itemObject.add("entry", weightedEntry.entry.toJson());
            }
            itemsArray.add(itemObject);
        }
        jsonObject.add("entries", itemsArray);
        return jsonObject;
    }
}