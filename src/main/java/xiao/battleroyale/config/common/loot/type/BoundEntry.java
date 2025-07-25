package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class BoundEntry implements ILootEntry {
    private final boolean countEmpty;
    private final boolean countLootTime;
    private final int min;
    private final int max;
    private final @NotNull List<ILootEntry> entries;

    public BoundEntry(boolean countEmpty, boolean countLootTime, int min, int max,
                      @NotNull List<ILootEntry> entries) {
        this.countEmpty = countEmpty;
        this.countLootTime = countLootTime;
        if (min < 0) {
            min = 0;
        }
        this.min = min;
        if (max < min) {
            max = min;
        }
        this.max = max;
        this.entries = entries;
    }

    @Override
    public @NotNull List<ILootData> generateLootData(Supplier<Float> random) {
        List<ILootData> lootData = new ArrayList<>();
        int count = 0;
        if (!entries.isEmpty()) {
            try {
                for (ILootEntry entry : entries) {
                    // 单个刷新词条生成的刷新
                    List<ILootData> loots = entry.generateLootData(random);
                    if (loots.isEmpty()) { // 无刷新
                        continue;
                    } else if (countLootTime) { // 以刷新次数为计数
                        if (!countEmpty && loots.stream().allMatch(ILootData::isEmpty)) {
                            continue;
                        }
                        if (++count > max) {
                            return Collections.emptyList();
                        }
                    } else { // 以实际刷新数量为计数
                        int added = countEmpty ? loots.size()
                                : (int) loots.stream().filter(data -> !data.isEmpty()).count();
                        if (count + added > max) {
                            return Collections.emptyList();
                        } else {
                            count += added;
                        }
                    }

                    lootData.addAll(loots);
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse bound entry");
            }
        } else {
            BattleRoyale.LOGGER.warn("BoundEntry missing entries member, skipped");
        }
        return lootData.size() >= min ? lootData : Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_BOUND;
    }

    @NotNull
    public static BoundEntry fromJson(JsonObject jsonObject) {
        boolean countEmpty = JsonUtils.getJsonBool(jsonObject, LootEntryTag.COUNT_EMPTY, false);
        boolean countLootTime = JsonUtils.getJsonBool(jsonObject, LootEntryTag.COUNT_LOOT_TIME, true);
        int min = JsonUtils.getJsonInt(jsonObject, LootEntryTag.MIN, 0);
        int max = JsonUtils.getJsonInt(jsonObject, LootEntryTag.MAX, 0);
        return new BoundEntry(countEmpty, countLootTime, min, max,
                MultiEntry.getEntries(jsonObject));
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.COUNT_EMPTY, countEmpty);
        jsonObject.addProperty(LootEntryTag.COUNT_LOOT_TIME, countLootTime);
        if (min >= 0) {
            jsonObject.addProperty(LootEntryTag.MIN, min);
        }
        if (max >= 0) {
            jsonObject.addProperty(LootEntryTag.MAX, max);
        }
        JsonArray entriesArray = new JsonArray();
        for (ILootEntry entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add(LootEntryTag.ENTRIES, entriesArray);
        return jsonObject;
    }
}
