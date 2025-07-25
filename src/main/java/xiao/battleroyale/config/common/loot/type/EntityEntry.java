package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.data.EntityData;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.NBTUtils;

import java.util.Collections;
import java.util.List;

public class EntityEntry implements IEntityLootEntry {
    private final String entityString;
    private final @Nullable String nbtString;
    private final @NotNull CompoundTag nbt;
    private final int count;
    private final int range;

    public EntityEntry(String rl, @Nullable String nbtString, int count, int range) {
        this.entityString = rl;
        this.nbtString = nbtString;
        this.nbt = NBTUtils.stringToNBT(nbtString);
        this.count = count;
        this.range = range;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, T target) {
        return Collections.singletonList(new EntityData(this.entityString, this.nbt, this.count, this.range));
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_ENTITY;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.ENTITY, entityString);
        if (this.count > 0) {
            jsonObject.addProperty(LootEntryTag.COUNT, count);
        }
        if (this.nbtString != null) {
            jsonObject.addProperty(LootEntryTag.NBT, this.nbtString);
        }
        if (this.range >= 0) {
            jsonObject.addProperty(LootEntryTag.RANGE, this.range);
        }
        return jsonObject;
    }

    @NotNull
    public static EntityEntry fromJson(JsonObject jsonObject) {
        String entityName = JsonUtils.getJsonString(jsonObject, LootEntryTag.ENTITY, "");
        int count = JsonUtils.getJsonInt(jsonObject, LootEntryTag.COUNT, 1);
        String nbtString = JsonUtils.getJsonString(jsonObject, LootEntryTag.NBT, null);
        int range = JsonUtils.getJsonInt(jsonObject, LootEntryTag.RANGE, 0);
        return new EntityEntry(entityName, nbtString, count, range);
    }
}