package xiao.battleroyale.common.loot.data;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.entity.IEntityLootData;
import xiao.battleroyale.util.NBTUtils;

import javax.annotation.Nullable;

public class EntityData implements IEntityLootData {
    private final @Nullable EntityType<?> entityType;
    private final @NotNull CompoundTag nbt;
    private final int count;
    private final int range;

    public EntityData(String rl, @Nullable String nbt, int count, int range) {
        this.entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(rl));
        if (this.entityType == null) {
            BattleRoyale.LOGGER.warn("Faild to get entity type from ResourceLocation {}", rl);
        }
        this.nbt = NBTUtils.stringToNBT(nbt);
        this.count = count;
        this.range = range;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Nullable
    @Override
    public Entity getEntity(ServerLevel level) {
        if (this.entityType == null) {
            return null;
        }
        Entity entity = this.entityType.create(level);
        if (entity != null & !this.nbt.isEmpty()) {
            entity.load(this.nbt);
        }
        return entity;
    }
}
