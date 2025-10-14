package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.data.EntityData;

import java.util.Collections;
import java.util.List;

public class GolemEntry implements ILootEntry {

    public final @NotNull EntityEntry entityEntry;

    public GolemEntry(@NotNull EntityEntry entry) {
        this(entry.entityString, entry.nbtString, entry.count, entry.range, entry.attempts);
    }
    public GolemEntry(String rl, @Nullable String nbtString, int count, int range, int attempts) {
        this.entityEntry = new EntityEntry(rl, nbtString, count, range, attempts);
    }
    @Override public @NotNull GolemEntry copy() {
        return new GolemEntry(this.entityEntry);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        if (target == null) {
            return Collections.emptyList();
        }

        LootGenerator.generateLootEntity(lootContext, new EntityData(this.entityEntry), target.getBlockPos());

        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_GOLEM;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = entityEntry.toJson();

        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());

        return jsonObject;
    }

    @NotNull
    public static GolemEntry fromJson(JsonObject jsonObject) {
        @NotNull EntityEntry entityEntry = EntityEntry.fromJson(jsonObject);

        return new GolemEntry(entityEntry);
    }
}
