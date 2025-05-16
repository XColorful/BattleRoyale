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

public class MultiEntry<T> implements ILootEntry<T> {
    private final List<ILootEntry<?>> entries;

    public MultiEntry(List<ILootEntry<?>> entries) {
        this.entries = entries;
    }

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        List<T> loot = new ArrayList<>();
        for (ILootEntry<?> entry : entries) {
            if (entry instanceof IItemLootEntry) {
                loot.addAll((List<T>) ((IItemLootEntry) entry).generateLoot(random));
            } else if (entry instanceof IEntityLootEntry) {
                loot.addAll((List<T>) ((IEntityLootEntry) entry).generateLoot(random));
            }
        }
        return loot;
    }

    @Override
    public String getType() {
        return "multi";
    }
    
    public static MultiEntry<?> fromJson(JsonObject jsonObject) {
        JsonArray entriesArray = jsonObject.getAsJsonArray("entries");
        List<ILootEntry<?>> entries = new ArrayList<>();
        if (entriesArray != null) {
            for (com.google.gson.JsonElement element : entriesArray) {
                if (element.isJsonObject()) {
                    JsonObject entryObject = element.getAsJsonObject();
                    ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }
        }
        return new MultiEntry<>(entries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        com.google.gson.JsonArray entriesArray = new com.google.gson.JsonArray();
        for (ILootEntry<?> entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add("entries", entriesArray);
        return jsonObject;
    }
}