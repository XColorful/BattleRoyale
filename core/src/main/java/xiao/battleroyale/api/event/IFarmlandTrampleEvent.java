package xiao.battleroyale.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IFarmlandTrampleEvent extends IEvent {

    LevelAccessor getLevelAccessor();
    default @Nullable ServerLevel getServerLevel() {
        return getLevelAccessor() instanceof ServerLevel serverLevel ? serverLevel : null;
    }
    BlockPos getBlockPos();
    BlockState getBlockState();

    Entity getEntity();
    float getFallDistance();
}
