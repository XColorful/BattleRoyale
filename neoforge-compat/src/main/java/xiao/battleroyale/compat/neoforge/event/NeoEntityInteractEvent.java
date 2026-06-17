package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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
import xiao.battleroyale.api.event.IEntityInteractEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoEntityInteractEvent extends NeoEvent implements IEntityInteractEvent {

    protected PlayerInteractEvent.EntityInteract entityInteractEvent;

    public NeoEntityInteractEvent(Event event) {
        super(event);
        if (event instanceof PlayerInteractEvent.EntityInteract eventIn) {
            this.entityInteractEvent = eventIn;
        } else {
            throw new RuntimeException("Expected EntityInteract but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.ENTITY_INTERACT_EVENT;
    }

    @Override
    public Player getEntity() {
        return entityInteractEvent.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return entityInteractEvent.getHand();
    }

    @Override
    public ItemStack getItemStack() {
        return entityInteractEvent.getItemStack();
    }

    @Override
    public BlockPos getBlockPos() {
        return entityInteractEvent.getPos();
    }

    @Override
    public @Nullable Direction getFace() {
        return entityInteractEvent.getFace();
    }

    @Override
    public Level getLevel() {
        return entityInteractEvent.getLevel();
    }

    @Override
    public McSide getMcSide() {
        return entityInteractEvent.getSide().isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public Entity getTarget() {
        return entityInteractEvent.getTarget();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (getHand() != InteractionHand.MAIN_HAND) return null; // 只给 function 传主手触发的事件
        Level level = getLevel();
        if (level != null && level.isClientSide()) return null;
        Player player = getEntity();
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
        return "NeoEntityInteractEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}