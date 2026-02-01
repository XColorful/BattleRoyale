package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingAttackEvent;

public class NeoLivingAttackEvent extends NeoEvent implements ILivingAttackEvent {

    protected LivingIncomingDamageEvent livingAttackEvent;

    public NeoLivingAttackEvent(Event event) {
        super(event);
        if (event instanceof LivingIncomingDamageEvent eventIn) {
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