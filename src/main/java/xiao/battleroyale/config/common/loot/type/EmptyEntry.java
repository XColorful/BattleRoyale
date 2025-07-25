package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.common.loot.data.EntityData;
import xiao.battleroyale.common.loot.data.ItemData;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EmptyEntry implements IItemLootEntry {

    private final LootEntryType type;

    public EmptyEntry(String typeString) {
        LootEntryType entryType = LootEntryType.fromName(typeString);
        this.type = entryType != null ? entryType : LootEntryType.ITEM;
    }

    @Override
    public @NotNull List<ILootData> generateLootData(Supplier<Float> random) {
        switch (type) {
            case ITEM -> {
                return Collections.singletonList(new ItemData("", "", 0));
            }
            case ENTITY -> {
                return Collections.singletonList(new EntityData("", "", 0, 0));
            }
            default -> {
                return Collections.singletonList(new ItemData("", "", 0));
            }
        }
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

    @NotNull
    public static EmptyEntry fromJson(JsonObject jsonObject) {
        String type = JsonUtils.getJsonString(jsonObject, LootEntryTag.TYPE, "item");
        return new EmptyEntry(type);
    }
}