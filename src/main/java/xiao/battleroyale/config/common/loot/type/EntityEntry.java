package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.common.loot.data.EntityData;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EntityEntry implements IEntityLootEntry {
    private final String entityString;
    private final @Nullable String nbtString;
    private final int count;
    private final int range;

    public EntityEntry(String rl, @Nullable String nbtString, int count, int range) {
        this.entityString = rl;
        this.nbtString = nbtString;
        this.count = count;
        this.range = range;
    }

    @Override
    public @NotNull List<ILootData> generateLootData(Supplier<Float> random) {
        return Collections.singletonList(new EntityData(this.entityString, this.nbtString, this.count, this.range));
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