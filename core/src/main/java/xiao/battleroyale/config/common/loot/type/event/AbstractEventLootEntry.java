package xiao.battleroyale.config.common.loot.type.event;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.config.common.loot.type.AbstractLootEntry;

public abstract class AbstractEventLootEntry extends AbstractLootEntry {

    public String protocol;
    public @NotNull JsonObject jsonTag;

    public AbstractEventLootEntry(String protocol, @Nullable JsonObject jsonTag) {
        this.protocol = protocol;
        this.jsonTag = jsonTag != null ? jsonTag : new JsonObject();
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.PROTOCOL, protocol);
        jsonObject.add(LootEntryTag.JSON_TAG, jsonTag);
        return jsonObject;
    }
}
