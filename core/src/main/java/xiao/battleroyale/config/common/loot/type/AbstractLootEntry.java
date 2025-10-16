package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;

public abstract class AbstractLootEntry implements ILootEntry {

    public AbstractLootEntry() {
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        return jsonObject;
    }
}
