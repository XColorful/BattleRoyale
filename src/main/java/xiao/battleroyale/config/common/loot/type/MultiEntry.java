package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
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
        try {
            for (ILootEntry<?> entry : entries) {
                loot.addAll((List<T>) entry.generateLoot(random));
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to parse multi entry");
        }
        return loot;
    }

    @Override
    public String getType() {
        return "multi";
    }
    
    public static MultiEntry<?> fromJson(JsonObject jsonObject) {
        List<ILootEntry<?>> entries = new ArrayList<>();
        if (jsonObject.has("entries")) {
            JsonArray entriesArray = jsonObject.getAsJsonArray("entries");
            if (entriesArray != null) {
                for (JsonElement element : entriesArray) {
                    if (element.isJsonObject()) {
                        JsonObject entryObject = element.getAsJsonObject();
                        ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
                        if (entry != null) {
                            entries.add(entry);
                        }
                    }
                }
            }
        } else {
            BattleRoyale.LOGGER.warn("MultiEntry missing entries member, skipped");
        }
        return new MultiEntry<>(entries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        JsonArray entriesArray = new JsonArray();
        for (ILootEntry<?> entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add("entries", entriesArray);
        return jsonObject;
    }
}