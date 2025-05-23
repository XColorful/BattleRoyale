package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NoneEntry implements ILootEntry {

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        return Collections.emptyList(); // 返回一个空的列表，表示不生成任何东西
    }

    @Override
    public String getType() {
        return "none";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        return jsonObject;
    }

    public static NoneEntry fromJson(JsonObject jsonObject) {
        return new NoneEntry();
    }
}