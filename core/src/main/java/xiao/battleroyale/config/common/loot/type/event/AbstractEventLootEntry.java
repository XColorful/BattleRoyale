package xiao.battleroyale.config.common.loot.type.event;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.util.JsonUtils;

public abstract class AbstractEventLootEntry implements ILootEntry {

    public String protocol;
    public @NotNull CompoundTag tag;

    public AbstractEventLootEntry(String protocol, @Nullable CompoundTag tag) {
        this.protocol = protocol;
        this.tag = tag != null ? tag : new CompoundTag();
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE, getType());
        jsonObject.addProperty(LootEntryTag.PROTOCOL, protocol);
        jsonObject.add(LootEntryTag.TAG, JsonUtils.writeTagToJson(tag));
        return jsonObject;
    }
}
