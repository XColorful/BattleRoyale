package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILeftClickBlockEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.api.minecraft.HandAction;
import xiao.battleroyale.api.minecraft.TriResult;
import xiao.battleroyale.compat.neoforge.minecraft.HandActionHelper;
import xiao.battleroyale.compat.neoforge.minecraft.TriResultHelper;

public class NeoLeftClickBlockEvent extends NeoEvent implements ILeftClickBlockEvent {

    protected PlayerInteractEvent.LeftClickBlock leftClickBlockEvent;

    public NeoLeftClickBlockEvent(Event event) {
        super(event);
        if (event instanceof PlayerInteractEvent.LeftClickBlock eventIn) {
            this.leftClickBlockEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LeftClickBlock but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.LEFT_CLICK_BLOCK_EVENT;
    }

    @Override
    public Player getEntity() {
        return leftClickBlockEvent.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return leftClickBlockEvent.getHand();
    }

    @Override
    public ItemStack getItemStack() {
        return leftClickBlockEvent.getItemStack();
    }

    @Override
    public BlockPos getBlockPos() {
        return leftClickBlockEvent.getPos();
    }

    @Override
    public @Nullable Direction getFace() {
        return leftClickBlockEvent.getFace();
    }

    @Override
    public Level getLevel() {
        return leftClickBlockEvent.getLevel();
    }

    @Override
    public McSide getMcSide() {
        return leftClickBlockEvent.getSide().isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public TriResult getUseBlock() {
        return TriResultHelper.convert(leftClickBlockEvent.getUseBlock());
    }

    @Override
    public TriResult getUseItem() {
        return TriResultHelper.convert(leftClickBlockEvent.getUseItem());
    }

    @Override
    public HandAction getAction() {
        return HandActionHelper.convert(leftClickBlockEvent.getAction());
    }

    @Override
    public void setUseBlock(TriResult triggerBlock) {
        leftClickBlockEvent.setUseBlock(TriResultHelper.convert(triggerBlock));
    }

    @Override
    public void setUseItem(TriResult triggerItem) {
        leftClickBlockEvent.setUseItem(TriResultHelper.convert(triggerItem));
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
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
        return "NeoLeftClickBlockEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}