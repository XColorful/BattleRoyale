package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.item.IItemLootEntry;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.data.ItemData;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.NBTUtils;

import java.util.Collections;
import java.util.List;

public class ItemEntry implements IItemLootEntry {
    private final String itemString;
    private final @Nullable String nbtString;
    private final @NotNull CompoundTag nbt;
    private final int count;

    public ItemEntry(String rl, @Nullable String nbtString, int count) {
        this.itemString = rl;
        this.nbtString = nbtString;
        this.nbt = NBTUtils.stringToNBT(nbtString);
        this.count = count;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, T target) {
        return Collections.singletonList(new ItemData(this.itemString, this.nbt, this.count));
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_ITEM;
    }

    @NotNull
    public static ItemEntry fromJson(JsonObject jsonObject) {
        String itemName = JsonUtils.getJsonString(jsonObject, LootEntryTag.ITEM, "");
        int count = JsonUtils.getJsonInt(jsonObject, LootEntryTag.COUNT, 1);
        String nbtString = JsonUtils.getJsonString(jsonObject, LootEntryTag.NBT, null);
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