package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingAttackEvent;

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
}