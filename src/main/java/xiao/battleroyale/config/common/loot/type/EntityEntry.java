package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.common.loot.data.EntityData;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EntityEntry implements IEntityLootEntry {
    private String entityString;
    private @Nullable String nbtString;
    private int count;
    private int range;

    public EntityEntry(String rl, @Nullable String nbtString, int count, int range) {
        this.entityString = rl;
        this.nbtString = nbtString;
        this.count = count;
        this.range = range;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
        return Collections.singletonList(new EntityData(this.entityString, this.nbtString, this.count, this.range));
    }

    @Override
    public String getType() {
        return "entity";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty("entity", entityString);
        if (this.count > 0) {
            jsonObject.addProperty("count", count);
        }
        if (this.nbtString != null) {
            jsonObject.addProperty("nbt", this.nbtString);
        }
        if (this.range >= 0) {
            jsonObject.addProperty("range", this.range);
        }
        return jsonObject;
    }

    public static EntityEntry fromJson(JsonObject jsonObject) {
        String entityName = jsonObject.has("entity") ? jsonObject.getAsJsonPrimitive("entity").getAsString() : "";
        int count = jsonObject.has("count") ? jsonObject.getAsJsonPrimitive("count").getAsInt() : 1;
        String nbtString = jsonObject.has("nbt") ? jsonObject.getAsJsonPrimitive("nbt").getAsString() : null;
        int range = jsonObject.has("range") ? jsonObject.getAsJsonPrimitive("range").getAsInt() : 0;
        return new EntityEntry(entityName, nbtString, count, range);
    }
}