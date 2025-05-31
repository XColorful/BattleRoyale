package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.common.loot.data.ItemData;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemEntry implements IItemLootEntry {
    private final String itemString;
    private final @Nullable String nbtString;
    private final int count;

    public ItemEntry(String rl, @Nullable String nbtString, int count) {
        this.itemString = rl;
        this.nbtString = nbtString;
        this.count = count;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        return Collections.singletonList(new ItemData(this.itemString, this.nbtString, this.count));
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_ITEM;
    }

    public static ItemEntry fromJson(JsonObject jsonObject) {
        String itemName = jsonObject.has(LootEntryTag.ITEM) ? jsonObject.getAsJsonPrimitive(LootEntryTag.ITEM).getAsString() : "";
        int count = jsonObject.has(LootEntryTag.COUNT) ? jsonObject.getAsJsonPrimitive(LootEntryTag.COUNT).getAsInt() : 1;
        String nbtString = jsonObject.has(LootEntryTag.NBT) ? jsonObject.getAsJsonPrimitive(LootEntryTag.NBT).getAsString() : null;
        return new ItemEntry(itemName, nbtString, count);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.ITEM, this.itemString);
        if (this.count >= 0) {
            jsonObject.addProperty(LootEntryTag.COUNT, this.count);
        }
        if (this.nbtString != null) {
            jsonObject.addProperty(LootEntryTag.NBT, this.nbtString);
        }
        return jsonObject;
    }
}