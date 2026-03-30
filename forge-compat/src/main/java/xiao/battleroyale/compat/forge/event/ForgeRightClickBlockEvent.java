package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IRightClickBlockEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.api.minecraft.TriResult;
import xiao.battleroyale.compat.forge.minecraft.TriResultHelper;

public class ForgeRightClickBlockEvent extends ForgeEvent implements IRightClickBlockEvent {

    protected PlayerInteractEvent.RightClickBlock rightClickBlockEvent;

    public ForgeRightClickBlockEvent(Event event) {
        super(event);
        if (event instanceof PlayerInteractEvent.RightClickBlock eventIn) {
            this.rightClickBlockEvent = eventIn;
        } else {
            throw new RuntimeException("Expected RightClickBlock but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.RIGHT_CLICK_BLOCK_EVENT;
    }

    @Override
    public Player getEntity() {
        return rightClickBlockEvent.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return rightClickBlockEvent.getHand();
    }

    @Override
    public ItemStack getItemStack() {
        return rightClickBlockEvent.getItemStack();
    }

    @Override
    public BlockPos getBlockPos() {
        return rightClickBlockEvent.getPos();
    }

    @Override
    public @Nullable Direction getFace() {
        return rightClickBlockEvent.getFace();
    }

    @Override
    public Level getLevel() {
        return rightClickBlockEvent.getLevel();
    }

    @Override
    public McSide getMcSide() {
        return rightClickBlockEvent.getSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public TriResult getUseBlock() {
        return TriResultHelper.convert(rightClickBlockEvent.getUseBlock());
    }

    @Override
    public TriResult getUseItem() {
        return TriResultHelper.convert(rightClickBlockEvent.getUseItem());
    }

    @Override
    public BlockHitResult getHitVec() {
        return rightClickBlockEvent.getHitVec();
    }

    @Override
    public void setUseBlock(TriResult triggerBlock) {
        rightClickBlockEvent.setUseBlock(TriResultHelper.convert(triggerBlock));
    }

    @Override
    public void setUseItem(TriResult triggerItem) {
        rightClickBlockEvent.setUseItem(TriResultHelper.convert(triggerItem));
    }

    @Override
    public InteractionResult getCancellationResult() {
        return rightClickBlockEvent.getCancellationResult();
    }

    @Override
    public void setCancellationResult(InteractionResult result) {
        rightClickBlockEvent.setCancellationResult(result);
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (getHand() != InteractionHand.MAIN_HAND) return null; // 只给 function 传主手触发的事件
        Level level = getLevel();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                this.getBlockPos().getCenter(),
                Vec2.ZERO,
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                this.getEntity()
        );
    }

    @Override public String getTextName() {
        return "ForgeRightClickBlockEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}