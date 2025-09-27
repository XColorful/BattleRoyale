package xiao.battleroyale.api.game;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IGameIdReadApi {

    @Nullable UUID getGameId(Entity entity);
    @Nullable UUID getGameId(BlockEntity blockEntity);
    @Nullable UUID getGameId(ItemStack itemStack);
}
