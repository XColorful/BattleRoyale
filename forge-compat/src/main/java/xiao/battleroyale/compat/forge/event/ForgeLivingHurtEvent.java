package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingHurtEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeLivingHurtEvent extends ForgeEvent implements ILivingHurtEvent {

    protected LivingHurtEvent livingHurtEvent;

    public ForgeLivingHurtEvent(Event event) {
        super(event);
        if (event instanceof LivingHurtEvent eventIn) {
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
        return livingHurtEvent.getAmount();
    }

    @Override
    public void setDamageAmount(float amount) {
        this.livingHurtEvent.setAmount(amount);
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @NotNull LivingEntity entity = this.getEntity();
        Level level = entity.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
                entity.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
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