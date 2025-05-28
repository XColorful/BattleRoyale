package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.ModBlocks;

public class EntitySpawnerBlockEntity extends AbstractLootBlockEntity {

    public EntitySpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.ENTITY_SPAWNER_BE.get(), pos, blockState);
    }
}