package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.config.common.loot.data.ItemData;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemEntry implements IItemLootEntry {
    private String itemString;
    private @Nullable String nbtString;
    private int count;

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
        return "item";
    }

    public static ItemEntry fromJson(JsonObject jsonObject) {
        String itemName = jsonObject.getAsJsonPrimitive("item").getAsString();
        int count = jsonObject.has("count") ? jsonObject.getAsJsonPrimitive("count").getAsInt() : 1;
        String nbtString = jsonObject.has("nbt") ? jsonObject.getAsJsonPrimitive("nbt").getAsString() : null;
        return new ItemEntry(itemName, nbtString, count);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("item", this.itemString);
        if (this.count >= 0) {
            jsonObject.addProperty("count", this.count);
        }
        if (this.nbtString != null) {
            jsonObject.addProperty("nbt", this.nbtString);
        }
        return jsonObject;
    }
}