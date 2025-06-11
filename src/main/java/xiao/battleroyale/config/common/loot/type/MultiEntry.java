package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MultiEntry implements ILootEntry {
    private final List<ILootEntry> entries;

    public MultiEntry(List<ILootEntry> entries) {
        this.entries = entries;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        List<ILootData> lootData = new ArrayList<>();
        if (!entries.isEmpty()) {
            try {
                for (ILootEntry entry : entries) {
                    lootData.addAll(entry.generateLootData(random));
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse multi entry");
            }
        } else {
            BattleRoyale.LOGGER.warn("MultiEntry missing entries member, skipped");
        }
        return lootData;
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_MULTI;
    }
    
    public static MultiEntry fromJson(JsonObject jsonObject) {
        List<ILootEntry> entries = new ArrayList<>();
        if (jsonObject.has(LootEntryTag.ENTRIES)) {
            JsonArray entriesArray = jsonObject.getAsJsonArray(LootEntryTag.ENTRIES);
            if (entriesArray != null) {
                for (JsonElement element : entriesArray) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject entryObject = element.getAsJsonObject();
                    ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }
        }
        return new MultiEntry(entries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        JsonArray entriesArray = new JsonArray();
        for (ILootEntry entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add(LootEntryTag.ENTRIES, entriesArray);
        return jsonObject;
    }
}