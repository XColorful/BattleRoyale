package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.item.IItemLootEntry;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EmptyEntry implements IItemLootEntry {

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_EMPTY;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        return jsonObject;
    }

    public static EmptyEntry fromJson(JsonObject jsonObject) {
        return new EmptyEntry();
    }
}