package xiao.battleroyale.api.game;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public interface IGameIdWriteApi {

    void addGameId(ItemStack itemStack, UUID gameId);
    void addGameId(Entity entity, UUID gameId);
    void addGameId(BlockEntity blockEntity, UUID gameId);
}
