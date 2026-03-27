package xiao.battleroyale.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockBreakEvent extends IEvent {

    LevelAccessor getLevelAccessor();
    default @Nullable ServerLevel getServerLevel() {
        return getLevelAccessor() instanceof ServerLevel serverLevel ? serverLevel : null;
    }
    BlockPos getBlockPos();
    BlockState getBlockState();

    Player getPlayer();
}
