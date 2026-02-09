package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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

    @Override
    public LivingEntity getEntity() {
        return livingDeathEvent.getEntity();
    }

    @Override
    public @NotNull DamageSource getSource() {
        return livingDeathEvent.getSource();
    }
}