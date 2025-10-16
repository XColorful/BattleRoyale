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

public class EntityEntry extends AbstractLootEntry implements IEntityLootEntry {
    public String entityString;
    public @Nullable String nbtString;
    public @NotNull CompoundTag nbt;
    public int count;
    public int range;
    public int attempts;

    public EntityEntry(String rl, @Nullable String nbtString, int count, int range) {
        this(rl, nbtString, count, range, 4);
    }
    public EntityEntry(String rl, @Nullable String nbtString, int count, int range, int attempts) {
        this.entityString = rl;
        this.nbtString = nbtString;
        this.nbt = NBTUtils.stringToNBT(nbtString);
        this.count = count;
        this.range = range;
        this.attempts = attempts;
    }
    @Override public @NotNull EntityEntry copy() {
        return new EntityEntry(entityString, nbtString, count, range, attempts);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        return Collections.singletonList(new EntityData(this.entityString, this.nbt.copy(), this.count, this.range, this.attempts));
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_ENTITY;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
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
        if (this.attempts >= 0) {
            jsonObject.addProperty(LootEntryTag.ATTEMPTS, this.attempts);
        }
        return jsonObject;
    }

    @NotNull
    public static EntityEntry fromJson(JsonObject jsonObject) {
        String entityName = JsonUtils.getJsonString(jsonObject, LootEntryTag.ENTITY, "");
        int count = JsonUtils.getJsonInt(jsonObject, LootEntryTag.COUNT, 1);
        String nbtString = JsonUtils.getJsonString(jsonObject, LootEntryTag.NBT, null);
        int range = JsonUtils.getJsonInt(jsonObject, LootEntryTag.RANGE, 0);
        int attempts = JsonUtils.getJsonInt(jsonObject, LootEntryTag.ATTEMPTS, 4);
        return new EntityEntry(entityName, nbtString, count, range, attempts);
    }
}