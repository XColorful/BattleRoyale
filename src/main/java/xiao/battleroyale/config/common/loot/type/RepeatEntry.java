package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RepeatEntry implements ILootEntry {
    private final int min;
    private final int max;
    private final ILootEntry entry;

    public RepeatEntry(int min, int max, ILootEntry entry) {
        if (min < 0) {
            min = 0;
        }
        this.min = min;
        if (max < min) {
            max = min;
        }
        this.max = max;
        this.entry = entry;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        int repeats = min + (int) (random.get() * (max - min + 1));
        List<ILootData> lootData = new ArrayList<>();
        if (entry != null) {
            try {
                for (int i = 0; i < repeats; i++) {
                    lootData.addAll(entry.generateLootData(random));
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse repeat entry");
            }
        } else {
            BattleRoyale.LOGGER.warn("RepeatEntry missing entry member, skipped");
        }
        return lootData;
    }

    @Override
    public String getType() {
        return "repeat";
    }

    public static RepeatEntry fromJson(JsonObject jsonObject) {
        int min = jsonObject.has("min") ? jsonObject.getAsJsonPrimitive("min").getAsInt() : 0;
        int max = jsonObject.has("max") ? jsonObject.getAsJsonPrimitive("max").getAsInt() : 0;
        ILootEntry entry = jsonObject.has("entry") ? JsonUtils.deserializeLootEntry(jsonObject.getAsJsonObject("entry")) : null;
        return new RepeatEntry(min, max, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
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