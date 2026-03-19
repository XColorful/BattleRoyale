package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;

public class NeoLivingDeathEvent extends NeoEvent implements ILivingDeathEvent {

    protected LivingDeathEvent livingDeathEvent;

    public NeoLivingDeathEvent(Event event) {
        super(event);
        if (event instanceof LivingDeathEvent eventIn) {
            this.livingDeathEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingDeathEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.LIVING_DEATH_EVENT;
    }

    @Override
    public LivingEntity getEntity() {
        return livingDeathEvent.getEntity();
    }

    @Override
    public @NotNull DamageSource getSource() {
        return livingDeathEvent.getSource();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @NotNull LivingEntity entity = this.getEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
                Vec2.ZERO,
                (ServerLevel) entity.level(),
                4,
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