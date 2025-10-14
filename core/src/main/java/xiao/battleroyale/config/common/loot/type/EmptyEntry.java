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
import xiao.battleroyale.common.loot.data.EntityData;
import xiao.battleroyale.common.loot.data.ItemData;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EmptyEntry implements IItemLootEntry {

    public LootEntryType type;

    public EmptyEntry(String typeString) {
        LootEntryType entryType = LootEntryType.fromName(typeString);
        this.type = entryType != null ? entryType : LootEntryType.ITEM;
    }
    @Override public @NotNull EmptyEntry copy() {
        return new EmptyEntry(type.getName());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        switch (type) {
            case ITEM -> {
                return Collections.singletonList(new ItemData(ItemData.EMPTY_RL, new CompoundTag(), 0));
            }
            case ENTITY -> {
                return Collections.singletonList(new EntityData(EntityData.EMPTY_RL, new CompoundTag(), 0, 0, 0));
            }
            default -> {
                return Collections.singletonList(new ItemData(ItemData.EMPTY_RL, new CompoundTag(), 0));
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
        jsonObject.addProperty(LootEntryTag.TYPE, type.getName());
        return jsonObject;
    }

    @NotNull
    public static EmptyEntry fromJson(JsonObject jsonObject) {
        String type = JsonUtils.getJsonString(jsonObject, LootEntryTag.TYPE, "item");
        return new EmptyEntry(type);
    }
}