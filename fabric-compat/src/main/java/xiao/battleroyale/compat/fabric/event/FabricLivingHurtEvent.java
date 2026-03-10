package xiao.battleroyale.compat.fabric.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingHurtEvent;

public class FabricLivingHurtEvent extends FabricEvent implements ILivingHurtEvent {
    private final LivingEntity entity;
    private final DamageSource source;
    private float amount;

    public FabricLivingHurtEvent(LivingEntity entity, DamageSource source, float amount) {
        super(true);
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }

    @Override
    public @NotNull LivingEntity getEntity() { return entity; }

    @Override
    public @NotNull DamageSource getSource() { return source; }

    @Override
    public float getDamageAmount() { return amount; }

    @Override
    public void setDamageAmount(float amount) { this.amount = amount; }
}