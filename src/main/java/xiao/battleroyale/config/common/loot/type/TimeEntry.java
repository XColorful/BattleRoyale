package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class TimeEntry implements ILootEntry {
    private final int start;
    private final int end;
    private final ILootEntry entry;

    public TimeEntry(int start, int end, ILootEntry entry) {
        this.start = start;
        this.end = end;
        this.entry = entry;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        int gameTime = GameManager.get().getGameTime();
        if (start <= gameTime && gameTime <= end) {
            try {
                return entry.generateLootData(random);
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse time entry");
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_TIME;
    }

    public static TimeEntry fromJson(JsonObject jsonObject) {
        int start = jsonObject.has(LootEntryTag.START) ? jsonObject.getAsJsonPrimitive(LootEntryTag.START).getAsInt() : 0;
        int end = jsonObject.has(LootEntryTag.END) ? jsonObject.getAsJsonPrimitive(LootEntryTag.END).getAsInt() : 0;
        ILootEntry entry = jsonObject.has(LootEntryTag.ENTRY) ? JsonUtils.deserializeLootEntry(jsonObject.getAsJsonObject(LootEntryTag.ENTRY)) : null;
        return new TimeEntry(start, end, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.START, this.start);
        jsonObject.addProperty(LootEntryTag.END, this.end);
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, this.entry.toJson());
        }
        return jsonObject;
    }
}