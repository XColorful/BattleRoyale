package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingHurtEvent;

public class NeoLivingHurtEvent extends NeoEvent implements ILivingHurtEvent {

    protected LivingHurtEvent livingHurtEvent;

    public NeoLivingHurtEvent(Event event) {
        super(event);
        if (event instanceof LivingHurtEvent eventIn) {
            this.livingHurtEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingHurtEvent but received: " + event.getClass().getName());
        }
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
}