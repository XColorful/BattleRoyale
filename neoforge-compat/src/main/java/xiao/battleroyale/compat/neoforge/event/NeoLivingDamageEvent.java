package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoLivingDamageEvent extends NeoEvent implements ILivingDamageEvent {

    protected LivingDamageEvent.Post livingDamageEvent;

    public NeoLivingDamageEvent(Event event) {
        super(event);
        if (event instanceof LivingDamageEvent.Post eventIn) {
            this.livingDamageEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingDamageEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.LIVING_DAMAGE_EVENT;
    }

    @Deprecated
    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingDamageEvent.getEntity();
    }

    @Override
    public @NotNull DamageSource getSource() {
        return livingDamageEvent.getSource();
    }

    @Override
    public float getDamageAmount() {
        return livingDamageEvent.getNewDamage();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @NotNull LivingEntity entity = this.getEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
                Vec2.ZERO,
                (ServerLevel) entity.level(),
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                entity.level().getServer(),
                entity
        );
    }

    @Override public String getTextName() {
        return this.getEntity().getName().getString();
    }
    @Override public Component getDisplayName() {
        return this.getEntity().getDisplayName();
    }
}