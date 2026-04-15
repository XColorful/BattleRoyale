package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ICriticalHitEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.api.minecraft.TriResult;
import xiao.battleroyale.compat.neoforge.minecraft.TriResultHelper;

public class NeoCriticalHitEvent extends NeoEvent implements ICriticalHitEvent {

    protected CriticalHitEvent criticalHitEvent;

    public NeoCriticalHitEvent(Event event) {
        super(event);
        if (event instanceof CriticalHitEvent eventIn) {
            this.criticalHitEvent = eventIn;
        } else {
            throw new RuntimeException("Expected CriticalHitEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.CRITICAL_HIT_EVENT;
    }

    @Override public boolean isCanceled() {
        return super.isCanceled() || !this.isCriticalHit();
    }

    @Override public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
        if (cancel) {
            this.setCriticalHit(false);
        }
    }

    @Override
    public Player getEntity() {
        return criticalHitEvent.getEntity();
    }

    @Override
    public Entity getTarget() {
        return criticalHitEvent.getTarget();
    }

    @Override
    public float getDamageMultiplier() {
        return criticalHitEvent.getDamageModifier();
    }

    @Override
    public void setDamageMultiplier(float multiplier) {
        criticalHitEvent.setDamageModifier(multiplier);
    }

    @Override
    public boolean isCriticalHit() {
        return switch (TriResultHelper.convert(criticalHitEvent.getResult())) {
            case ALLOW -> true;
            case DENY -> false;
            default -> this.isVanillaCritical();
        };
    }

    @Override
    public void setCriticalHit(boolean isCriticalHit) {
        criticalHitEvent.setResult(isCriticalHit ? TriResultHelper.convert(TriResult.ALLOW) : TriResultHelper.convert(TriResult.DENY));
    }

    @Override
    public boolean isVanillaCritical() {
        return criticalHitEvent.isVanillaCritical();
    }

    @Override
    public void setDisableSweep(boolean disableSweep) {
    }

    @Override
    public boolean isDisableSweep() {
        return true;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Player player = this.getEntity();
        Level level = player.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                player.position(),
                player.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                player
        );
    }

    @Override
    public String getTextName() {
        return this.getEntity().getName().getString();
    }

    @Override
    public Component getDisplayName() {
        return this.getEntity().getDisplayName();
    }
}