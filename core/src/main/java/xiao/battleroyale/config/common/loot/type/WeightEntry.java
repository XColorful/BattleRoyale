package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class WeightEntry extends AbstractLootEntry {
    public final List<WeightedEntry> weightedEntries;

    public static class WeightedEntry {
        public double weight;
        public ILootEntry entry;

        public WeightedEntry(double weight, @NotNull ILootEntry entry) {
            if (weight < 0) {
                weight = 0;
            }
            this.weight = weight;
            this.entry = entry;
        }
        public WeightedEntry copy() {
            return new WeightedEntry(weight, entry.copy());
        }
    }

    public static WeightedEntry createWeightedEntry(double weight, ILootEntry entry) {
        return new WeightedEntry(weight, entry);
    }

    public WeightEntry(List<WeightedEntry> weightedEntries) {
        this.weightedEntries = weightedEntries;
    }
    @Override public @NotNull WeightEntry copy() {
        List<WeightedEntry> weightedEntriesCopy = new ArrayList<>(weightedEntries.size());
        for (WeightedEntry weightedEntry : weightedEntries) {
            weightedEntriesCopy.add(weightedEntry.copy());
        }
        return new WeightEntry(weightedEntriesCopy);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        double totalWeight = 0;
        for (WeightedEntry weightedEntry : weightedEntries) {
            totalWeight += weightedEntry.weight;
        }
        if (totalWeight <= 0) {
            return new ArrayList<>();
        }

        double randomNumber = lootContext.random.get() * totalWeight;
        double currentWeight = 0;
        if (!weightedEntries.isEmpty()) {
            try {
                for (WeightedEntry weightedEntry : weightedEntries) {
                    currentWeight += weightedEntry.weight;
                    if (randomNumber < currentWeight) {
                        return weightedEntry.entry.generateLootData(lootContext, target);
                    }
                }
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        BattleRoyale.LOGGER.warn("Unexpected WeightEntry loot result, weightedEntries.size()={}, totalWeight={}", weightedEntries.size(), totalWeight);
        return new ArrayList<>();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_WEIGHT;
    }

    @NotNull
    public static WeightEntry fromJson(JsonObject jsonObject) {
        List<WeightedEntry> weightedEntries = new ArrayList<>();
        JsonArray itemsArray = JsonUtils.getJsonArray(jsonObject, LootEntryTag.ENTRIES, null);
        if (itemsArray != null) {
            for (JsonElement element : itemsArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject itemObject = element.getAsJsonObject();
                double weight = JsonUtils.getJsonDouble(itemObject, LootEntryTag.WEIGHT, 0);
                JsonObject entryObject = JsonUtils.getJsonObject(itemObject, LootEntryTag.ENTRY, null);
                ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
                if (entry != null) {
                    weightedEntries.add(new WeightedEntry(weight, entry));
                }
            }
        }
        return new WeightEntry(weightedEntries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        JsonArray itemsArray = new JsonArray();
        for (WeightedEntry weightedEntry : weightedEntries) {
            JsonObject itemObject = new JsonObject();
            itemObject.addProperty(LootEntryTag.WEIGHT, weightedEntry.weight);
            itemObject.add(LootEntryTag.ENTRY, weightedEntry.entry.toJson());
            itemsArray.add(itemObject);
        }
        jsonObject.add(LootEntryTag.ENTRIES, itemsArray);
        return jsonObject;
    }

    @Override
    public JsonObject toLootTable() {
        /*
            {
                "rolls": 1,
                "entries": [
                    { "type": "minecraft:item", "weight": 10, "name": "..." }
                ]
            }
        */
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        JsonArray entriesArray = new JsonArray();
        for (WeightedEntry weightedEntry : weightedEntries) {
            JsonObject result = weightedEntry.entry.toLootTable();
            if (result == null) continue;

            // 如果子项返回 root（MultiEntry）
            if (result.has("pools")) {
                JsonArray pools = result.getAsJsonArray("pools");
                for (JsonElement poolElement : pools) {
                    JsonObject entry = new JsonObject();
                    entry.addProperty("type", "minecraft:group");
                    entry.addProperty("weight", (int) weightedEntry.weight);
                    JsonArray children = new JsonArray();
                    JsonObject poolObj = poolElement.getAsJsonObject();
                    JsonArray subEntries = poolObj.getAsJsonArray("entries");
                    for (JsonElement e : subEntries) {
                        children.add(e);
                    }
                    entry.add("children", children);
                    entriesArray.add(entry);
                }
            }
            // 如果子项是 pool
            else if (result.has("rolls")) {
                JsonObject entry = new JsonObject();
                entry.addProperty("type", "minecraft:group");
                entry.addProperty("weight", (int) weightedEntry.weight);
                JsonArray children = new JsonArray();
                JsonArray subEntries = result.getAsJsonArray("entries");
                for (JsonElement e : subEntries) {
                    children.add(e);
                }
                entry.add("children", children);
                entriesArray.add(entry);
            }
            // 普通 entry
            else if (result.has("type")) {
                result.addProperty("weight", (int) weightedEntry.weight);
                entriesArray.add(result);
            }
        }
        pool.add("entries", entriesArray);
        return pool;
    }
}