package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingAttackEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeLivingAttackEvent extends ForgeEvent implements ILivingAttackEvent {

    protected LivingAttackEvent livingAttackEvent;

    public ForgeLivingAttackEvent(Event event) {
        super(event);
        if (event instanceof LivingAttackEvent eventIn) {
            this.livingAttackEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingAttackEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.LIVING_ATTACK_EVENT;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingAttackEvent.getEntity();
    }

    @Override
    public @NotNull DamageSource getSource() {
        return livingAttackEvent.getSource();
    }

    @Override
    public float getDamageAmount() {
        return livingAttackEvent.getAmount();
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
                entity.getServer(),
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