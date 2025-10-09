package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleEntry implements ILootEntry {
    public boolean keepEmpty;
    public int min;
    public int max;
    public ILootEntry entry;

    public ShuffleEntry(boolean keepEmpty, int min, int max,
                        ILootEntry entry) {
        this.keepEmpty = keepEmpty;
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
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>();
        if (entry != null) {
            try {
                List<ILootData> queuedData = new ArrayList<>(entry.generateLootData(lootContext, target));
                Collections.shuffle(queuedData);
                int select = Math.min(min + (int) ((max - min) * lootContext.random.get()), queuedData.size());

                queuedData.stream()
                        .filter(data -> (this.keepEmpty || !data.isEmpty()))
                        .limit(select)
                        .forEach(lootData::add);
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        return lootData;
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_SHUFFLE;
    }

    @NotNull
    public static ShuffleEntry fromJson(JsonObject jsonObject) {
        boolean keepEmpty = JsonUtils.getJsonBool(jsonObject, LootEntryTag.KEEP_EMPTY, false);
        int min = JsonUtils.getJsonInt(jsonObject, LootEntryTag.MIN, 0);
        int max = JsonUtils.getJsonInt(jsonObject, LootEntryTag.MAX, 0);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new ShuffleEntry(keepEmpty, min, max,
                entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.KEEP_EMPTY, keepEmpty);
        jsonObject.addProperty(LootEntryTag.MIN, min);
        jsonObject.addProperty(LootEntryTag.MAX, max);
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        }
        return jsonObject;
    }
}