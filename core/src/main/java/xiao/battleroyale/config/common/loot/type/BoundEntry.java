package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoundEntry extends AbstractLootEntry {
    public boolean countEmpty;
    public boolean countLootTime;
    public int min;
    public int max;
    public boolean keepEmpty;
    public final @NotNull List<ILootEntry> entries;

    public BoundEntry(boolean countEmpty, boolean countLootTime, int min, int max, boolean keepEmpty,
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
        this.keepEmpty = keepEmpty;
        this.entries = entries;
    }
    @Override public @NotNull BoundEntry copy() {
        List<ILootEntry> entriesCopy = new ArrayList<>(entries.size());
        for (ILootEntry entry : entries) {
            entriesCopy.add(entry.copy());
        }
        return new BoundEntry(countEmpty, countLootTime, min, max, keepEmpty,
                entriesCopy);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>();
        int count = 0;
        if (!entries.isEmpty()) {
            try {
                for (ILootEntry entry : entries) {
                    // 单个刷新词条生成的刷新
                    List<ILootData> loots = entry.generateLootData(lootContext, target);
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

                    loots.stream()
                            .filter(data -> keepEmpty || !data.isEmpty())
                            .forEach(lootData::add);
                }
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        return count >= min ? lootData : Collections.emptyList();
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
        boolean keepEmpty = JsonUtils.getJsonBool(jsonObject, LootEntryTag.KEEP_EMPTY, false);
        return new BoundEntry(countEmpty, countLootTime, min, max, keepEmpty,
                MultiEntry.getEntries(jsonObject));
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.COUNT_EMPTY, countEmpty);
        jsonObject.addProperty(LootEntryTag.COUNT_LOOT_TIME, countLootTime);
        if (min >= 0) {
            jsonObject.addProperty(LootEntryTag.MIN, min);
        }
        if (max >= 0) {
            jsonObject.addProperty(LootEntryTag.MAX, max);
        }
        jsonObject.addProperty(LootEntryTag.KEEP_EMPTY, keepEmpty);
        JsonArray entriesArray = new JsonArray();
        for (ILootEntry entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add(LootEntryTag.ENTRIES, entriesArray);
        return jsonObject;
    }
}
