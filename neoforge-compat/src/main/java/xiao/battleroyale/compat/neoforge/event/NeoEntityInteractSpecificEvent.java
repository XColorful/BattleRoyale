package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import xiao.battleroyale.api.event.IEntityInteractSpecificEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoEntityInteractSpecificEvent extends NeoEvent implements IEntityInteractSpecificEvent {

    protected PlayerInteractEvent.EntityInteractSpecific entityInteractSpecificEvent;

    public NeoEntityInteractSpecificEvent(Event event) {
        super(event);
        if (event instanceof PlayerInteractEvent.EntityInteractSpecific eventIn) {
            this.entityInteractSpecificEvent = eventIn;
        } else {
            throw new RuntimeException("Expected EntityInteractSpecific but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.ENTITY_INTERACT_SPECIFIC_EVENT;
    }

    @Override
    public Player getEntity() {
        return entityInteractSpecificEvent.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return entityInteractSpecificEvent.getHand();
    }

    @Override
    public ItemStack getItemStack() {
        return entityInteractSpecificEvent.getItemStack();
    }

    @Override
    public BlockPos getBlockPos() {
        return entityInteractSpecificEvent.getPos();
    }

    @Override
    public @Nullable Direction getFace() {
        return entityInteractSpecificEvent.getFace();
    }

    @Override
    public Level getLevel() {
        return entityInteractSpecificEvent.getLevel();
    }

    @Override
    public McSide getMcSide() {
        return entityInteractSpecificEvent.getSide().isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public Entity getTarget() {
        return entityInteractSpecificEvent.getTarget();
    }

    @Override
    public Vec3 getLocalPos() {
        return entityInteractSpecificEvent.getLocalPos();
    }

    @Override
    public InteractionResult getCancellationResult() {
        return entityInteractSpecificEvent.getCancellationResult();
    }

    @Override
    public void setCancellationResult(InteractionResult result) {
        entityInteractSpecificEvent.setCancellationResult(result);
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (getHand() != InteractionHand.MAIN_HAND) return null; // 只给 function 传主手触发的事件
        Level level = getLevel();
        if (level != null && level.isClientSide()) return null;
        Player player = this.getEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                this.getBlockPos().getCenter(),
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
        return "NeoEntityInteractSpecificEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}