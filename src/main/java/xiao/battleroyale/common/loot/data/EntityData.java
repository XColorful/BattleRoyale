package xiao.battleroyale.common.loot.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.entity.IEntityLootData;

import javax.annotation.Nullable;

public class EntityData implements IEntityLootData {
    private @Nullable EntityType<?> entityType;
    private @Nullable CompoundTag nbt;
    private int count;
    private int range;

    public EntityData(String rl, @Nullable String nbt, int count, int range) {
        this.entityType = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(rl));
        if (this.entityType == null) {
            BattleRoyale.LOGGER.warn("无法找到 ResourceLocation {} 实体类型", rl);
        }
        if (nbt != null) {
            try {
                this.nbt = TagParser.parseTag(nbt);
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("解析实体NBT失败 {}: {}", rl, e.getMessage());
            }
        }
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
        if (entity != null & this.nbt != null) {
            entity.load(this.nbt);
        }
        return entity;
    }
}
