package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingDamageEvent;

public class NeoLivingDamageEvent extends NeoEvent implements ILivingDamageEvent {

    protected LivingDamageEvent.Pre livingDamageEvent;

    public NeoLivingDamageEvent(Event event) {
        super(event);
        if (event instanceof LivingDamageEvent.Pre livingDamageEvent) {
            this.livingDamageEvent = livingDamageEvent;
        } else {
            throw new RuntimeException("Expected LivingDamageEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public void setCanceled(boolean cancel) {
        if (cancel) {
            livingDamageEvent.setNewDamage(0);
        }
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
}