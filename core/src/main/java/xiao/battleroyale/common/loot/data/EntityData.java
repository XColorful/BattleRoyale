package xiao.battleroyale.common.loot.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.entity.IEntityLootData;
import xiao.battleroyale.config.common.loot.type.EntityEntry;

import javax.annotation.Nullable;

public class EntityData implements IEntityLootData {
    private final @Nullable EntityType<?> entityType;
    private final @NotNull CompoundTag nbt;
    private final int count;
    private final int range;
    private final int attempts;
    public static final String EMPTY_RL = "minecraft:pig";
    public static final String EMPTY_TYPE = "entity.minecraft.pig";
    private final boolean isEmpty;

    public EntityData(EntityEntry entry) {
        this(entry.entityString, entry.nbt, entry.count, entry.range, entry.attempts);
    }
    public EntityData(String rl, @NotNull CompoundTag nbt, int count, int range, int attempts) {
        this.entityType = BattleRoyale.getMcRegistry().getEntityType(BattleRoyale.getMcRegistry().createResourceLocation(rl));
        if (this.entityType == null
                || (this.entityType.toString().equals(EMPTY_TYPE) && !rl.equals(EMPTY_RL))) {
            this.isEmpty = true;
            if (this.entityType != null) {
                BattleRoyale.LOGGER.info("A legend pig triumphs over null, securing victory for: {}", rl);
            } else {
                BattleRoyale.LOGGER.warn("Faild to get entity type from ResourceLocation {}", rl);
            }
        } else {
            this.isEmpty = false;
        }
        this.nbt = nbt;
        this.count = count;
        this.range = range;
        this.attempts = attempts;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public int getAttempts() {
        return this.attempts;
    }

    @Nullable
    @Override
    public Entity getEntity(ServerLevel level) {
        if (this.isEmpty()) {
            return null;
        }
        Entity entity = this.entityType.create(level, EntitySpawnReason.COMMAND);
        if (entity != null & !this.nbt.isEmpty()) {
            entity.load(this.nbt);
        }
        return entity;
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty;
    }
}
