package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EntityEntry implements IEntityLootEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityEntry.class);
    private final ResourceLocation entityTypeRL;
    private final CompoundTag nbt;
    private final int count;
    private final int range;

    public EntityEntry(ResourceLocation entityTypeRL, CompoundTag nbt, int count, int range) {
        this.entityTypeRL = entityTypeRL;
        this.nbt = nbt;
        this.count = count;
        this.range = range;
    }

    @Override
    public List<Entity> generateLoot(Supplier<Float> random) {
        List<Entity> entities = new ArrayList<>();
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityTypeRL);
        if (entityType != null) {
            // 注意：这里只是创建实体实例，实际生成到世界需要 LootSpawner 处理，并传入 Level
            for (int i = 0; i < count; i++) {
                Entity entity = entityType.create((ServerLevel) null); // 在实际生成时需要 ServerLevel
                if (entity != null && nbt != null) {
                    entity.load(nbt);
                }
                entities.add(entity);
            }
        } else {
            LOGGER.warn("Unknown entity type: {}", entityTypeRL);
        }
        return entities;
    }

    @Override
    public String getType() {
        return "entity";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("entity", entityTypeRL.toString());
        if (count > 1) {
            jsonObject.addProperty("count", count);
        }
        if (nbt != null && !nbt.isEmpty()) {
            jsonObject.addProperty("nbt", nbt.toString());
        }
        if (range > 0) {
            jsonObject.addProperty("range", range);
        }
        return jsonObject;
    }

    public static EntityEntry fromJson(JsonObject jsonObject) {
        String entityName = jsonObject.getAsJsonPrimitive("entity").getAsString();
        ResourceLocation entityTypeRL = new ResourceLocation(entityName);
        int count = jsonObject.has("count") ? jsonObject.getAsJsonPrimitive("count").getAsInt() : 1;
        String nbtString = jsonObject.has("nbt") ? jsonObject.getAsJsonPrimitive("nbt").getAsString() : null;
        CompoundTag nbt = null;
        if (nbtString != null) {
            try {
                nbt = TagParser.parseTag(nbtString);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse NBT for entity {}: {}", entityName, e.getMessage());
            }
        }
        int range = jsonObject.has("range") ? jsonObject.getAsJsonPrimitive("range").getAsInt() : 0;
        return new EntityEntry(entityTypeRL, nbt, count, range);
    }
}