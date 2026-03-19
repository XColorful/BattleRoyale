package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;

public class ForgeLivingDeathEvent extends ForgeEvent implements ILivingDeathEvent {

    protected LivingDeathEvent livingDeathEvent;

    public ForgeLivingDeathEvent(Event event) {
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
}
