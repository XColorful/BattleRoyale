package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class TimeEntry implements ILootEntry {
    private final int start;
    private final int end;
    private final ILootEntry entry;

    public TimeEntry(int start, int end, @Nullable ILootEntry entry) {
        this.start = start;
        this.end = end;
        this.entry = entry;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        int gameTime = GameManager.get().getGameTime();
        if (entry != null && start <= gameTime && gameTime <= end) {
            try {
                return entry.generateLootData(random);
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse time entry");
            }
        } else {
            BattleRoyale.LOGGER.warn("TimeEntry missing entry member or has invalid config");
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_TIME;
    }

    public static TimeEntry fromJson(JsonObject jsonObject) {
        int start = JsonUtils.getJsonInt(jsonObject, LootEntryTag.START, 0);
        int end = JsonUtils.getJsonInt(jsonObject, LootEntryTag.END, 0);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
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