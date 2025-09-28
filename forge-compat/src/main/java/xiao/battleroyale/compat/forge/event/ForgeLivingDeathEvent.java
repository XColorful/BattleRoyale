package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.ILivingDeathEvent;

public class ForgeLivingDeathEvent extends ForgeEvent implements ILivingDeathEvent {

    protected LivingDeathEvent livingDeathEvent;

    public ForgeLivingDeathEvent(Event event) {
        super(event);
        if (event instanceof LivingDeathEvent livingDeathEvent) {
            this.livingDeathEvent = livingDeathEvent;
        } else {
            throw new RuntimeException("Expected LivingDeathEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public LivingEntity getEntity() {
        return livingDeathEvent.getEntity();
    }
}
