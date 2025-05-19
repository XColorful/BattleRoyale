package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.ModBlocks;

public class EntitySpawnerBlockEntity extends AbstractLootBlockEntity {
    public static final BlockEntityType<EntitySpawnerBlockEntity> TYPE = BlockEntityType.Builder.of(EntitySpawnerBlockEntity::new,
            ModBlocks.ENTITY_SPAWNER.get()
    ).build(null);

    public EntitySpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    // 在这里实现实体生成的逻辑
}