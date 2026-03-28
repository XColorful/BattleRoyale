package xiao.battleroyale.compat.forge.event;

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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IRightClickItemEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeRightClickItemEvent extends ForgeEvent implements IRightClickItemEvent {

    protected PlayerInteractEvent.RightClickItem rightClickItemEvent;

    public ForgeRightClickItemEvent(Event event) {
        super(event);
        if (event instanceof PlayerInteractEvent.RightClickItem eventIn) {
            this.rightClickItemEvent = eventIn;
        } else {
            throw new RuntimeException("Expected RightClickItem but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.RIGHT_CLICK_ITEM_EVENT;
    }

    @Override
    public Player getEntity() {
        return rightClickItemEvent.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return rightClickItemEvent.getHand();
    }

    @Override
    public ItemStack getItemStack() {
        return rightClickItemEvent.getItemStack();
    }

    @Override
    public BlockPos getBlockPos() {
        return rightClickItemEvent.getPos();
    }

    @Override
    public @Nullable Direction getFace() {
        return rightClickItemEvent.getFace();
    }

    @Override
    public Level getLevel() {
        return rightClickItemEvent.getLevel();
    }

    @Override
    public McSide getMcSide() {
        return rightClickItemEvent.getSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
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
        return "ForgeRightClickItemEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}