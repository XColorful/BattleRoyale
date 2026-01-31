package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingHurtEvent;

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