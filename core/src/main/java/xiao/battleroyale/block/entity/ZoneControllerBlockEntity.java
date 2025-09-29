package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.registry.ModBlocks;

public class ZoneControllerBlockEntity extends BlockEntity {

    public ZoneControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.ZONE_CONTROLLER_BE.get(), pos, blockState);
    }
}