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
import xiao.battleroyale.api.event.ILivingHurtEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoLivingHurtEvent extends NeoEvent implements ILivingHurtEvent {

    protected LivingDamageEvent.Pre livingHurtEvent;

    public NeoLivingHurtEvent(Event event) {
        super(event);
        if (event instanceof LivingDamageEvent.Pre eventIn) {
            this.livingHurtEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingHurtEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.LIVING_HURT_EVENT;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingHurtEvent.getEntity();
    }

    @Override
    public @NotNull DamageSource getSource() {
        return livingHurtEvent.getSource();
    }

    @Override
    public float getDamageAmount() {
        return livingHurtEvent.getNewDamage();
    }

    @Override
    public void setDamageAmount(float amount) {
        livingHurtEvent.setNewDamage(amount);
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