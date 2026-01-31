package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingAttachEvent;

public class NeoLivingAttackEvent extends NeoEvent implements ILivingAttachEvent {

    protected LivingAttackEvent livingAttackEvent;

    public NeoLivingAttackEvent(Event event) {
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