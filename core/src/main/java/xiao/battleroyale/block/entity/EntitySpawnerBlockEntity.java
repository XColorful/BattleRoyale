package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.init.registry.ModBlocks;

public class EntitySpawnerBlockEntity extends AbstractLootBlockEntity {

    public EntitySpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.ENTITY_SPAWNER_BE.get(), pos, blockState);
    }

    @Override
    public int getConfigFolderId() {
        return LootConfigTypeEnum.ENTITY_SPAWNER;
    }
}