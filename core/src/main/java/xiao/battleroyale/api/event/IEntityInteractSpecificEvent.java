package xiao.battleroyale.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;

public interface IEntityInteractSpecificEvent extends IEvent {

    Player getEntity();
    InteractionHand getHand();
    ItemStack getItemStack();
    BlockPos getBlockPos();
    @Nullable Direction getFace();
    Level getLevel();
    McSide getMcSide();

    Entity getTarget();
    Vec3 getLocalPos();
    InteractionResult getCancellationResult();
    void setCancellationResult(InteractionResult result);
}
