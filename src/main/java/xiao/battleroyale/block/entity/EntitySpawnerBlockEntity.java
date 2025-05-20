package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.ModBlocks;
import java.util.UUID;

public class EntitySpawnerBlockEntity extends AbstractLootBlockEntity {
    public static final BlockEntityType<EntitySpawnerBlockEntity> TYPE = BlockEntityType.Builder.of(EntitySpawnerBlockEntity::new,
            ModBlocks.ENTITY_SPAWNER.get()
    ).build(null);

    public EntitySpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    @Override
    public UUID getGameId() {
        return super.getGameId();
    }

    @Override
    public void setGameId(UUID gameId) {
        super.setGameId(gameId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // EntitySpawnerBlockEntity 特有的加载逻辑

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // EntitySpawnerBlockEntity 特有的保存逻辑

    }

    // 在这里实现实体生成的逻辑
}