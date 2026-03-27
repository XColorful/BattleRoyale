package xiao.battleroyale.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.minecraft.HandAction;
import xiao.battleroyale.api.minecraft.TriResult;

public interface ILeftClickBlockEvent extends IEvent {

    Player getEntity();
    InteractionHand getHand();
    ItemStack getItemStack();
    BlockPos getBlockPos();
    @Nullable Direction getFace();
    Level getLevel();
    McSide getMcSide();

    TriResult getUseBlock();
    TriResult getUseItem();
    HandAction getAction();
    void setUseBlock(TriResult triggerBlock);
    void setUseItem(TriResult triggerItem);
}
