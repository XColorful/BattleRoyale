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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IRightClickItemEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoRightClickItemEvent extends NeoEvent implements IRightClickItemEvent {

    protected PlayerInteractEvent.RightClickItem rightClickItemEvent;

    public NeoRightClickItemEvent(Event event) {
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
        return rightClickItemEvent.getSide().isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (getHand() != InteractionHand.MAIN_HAND) return null; // 只给 function 传主手触发的事件
        Level level = getLevel();
        if (level != null && level.isClientSide()) return null;
        Player player = this.getEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                Vec3.atCenterOf(this.getBlockPos()),
                player.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                player
        );
    }

    @Override public String getTextName() {
        return "NeoRightClickItemEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}