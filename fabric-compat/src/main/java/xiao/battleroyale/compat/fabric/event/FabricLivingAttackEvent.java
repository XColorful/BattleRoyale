package xiao.battleroyale.compat.fabric.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingAttackEvent;

public class FabricLivingAttackEvent extends FabricEvent implements ILivingAttackEvent {
    private final LivingEntity entity;
    private final DamageSource source;
    private final float amount;

    public FabricLivingAttackEvent(LivingEntity entity, DamageSource source, float amount) {
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
}