package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.data.EntityData;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;

public class GolemEntry extends AbstractLootEntry {

    public final @NotNull EntityEntry entityEntry;

    public GolemEntry(@NotNull EntityEntry entry) {
        this(entry.entityString, entry.nbtString, entry.count, entry.range, entry.attempts);
    }
    public GolemEntry(String rl, @Nullable String nbtString, int count, int range, int attempts) {
        this.entityEntry = new EntityEntry(rl, nbtString, count, range, attempts);
    }
    @Override public @NotNull GolemEntry copy() {
        return new GolemEntry(this.entityEntry.copy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        if (target == null) {
            return Collections.emptyList();
        }

        int generatedCount = LootGenerator.generateLootEntity(lootContext, new EntityData(this.entityEntry), target.getBlockPos());
        if (generatedCount <= 0) {
            BattleRoyale.LOGGER.debug("Golem entry doesn't generate any entity");
        }

        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_GOLEM;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.ENTITY, entityEntry.entityString);
        if (entityEntry.count > 0) {
            jsonObject.addProperty(LootEntryTag.COUNT, entityEntry.count);
        }
        if (entityEntry.nbtString != null) {
            jsonObject.addProperty(LootEntryTag.NBT, entityEntry.nbtString);
        }
        if (entityEntry.range >= 0) {
            jsonObject.addProperty(LootEntryTag.RANGE, entityEntry.range);
        }
        if (entityEntry.attempts >= 0) {
            jsonObject.addProperty(LootEntryTag.ATTEMPTS, entityEntry.attempts);
        }

        return jsonObject;
    }

    @NotNull
    public static GolemEntry fromJson(JsonObject jsonObject) {
        String entityName = JsonUtils.getJsonString(jsonObject, LootEntryTag.ENTITY, "");
        int count = JsonUtils.getJsonInt(jsonObject, LootEntryTag.COUNT, 1);
        String nbtString = JsonUtils.getJsonString(jsonObject, LootEntryTag.NBT, null);
        int range = JsonUtils.getJsonInt(jsonObject, LootEntryTag.RANGE, 0);
        int attempts = JsonUtils.getJsonInt(jsonObject, LootEntryTag.ATTEMPTS, 4);
        return new GolemEntry(entityName, nbtString, count, range, attempts);
    }
}
