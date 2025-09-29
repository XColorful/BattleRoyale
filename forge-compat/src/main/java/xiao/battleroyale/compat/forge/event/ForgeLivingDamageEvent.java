package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingDamageEvent;

public class ForgeLivingDamageEvent extends ForgeEvent implements ILivingDamageEvent {

    protected LivingDamageEvent livingDamageEvent;

    public ForgeLivingDamageEvent(Event event) {
        super(event);
        if (event instanceof LivingDamageEvent livingDamageEvent) {
            this.livingDamageEvent = livingDamageEvent;
        } else {
            throw new RuntimeException("Expected LivingDamageEvent but received: " + event.getClass().getName());
        }
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
        return livingDamageEvent.getAmount();
    }
}
