package xiao.battleroyale.compat.fabric.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingDeathEvent;

public class FabricLivingDeathEvent extends FabricEvent implements ILivingDeathEvent {
    private final LivingEntity entity;
    private final DamageSource source;

    public FabricLivingDeathEvent(LivingEntity entity, DamageSource source) {
        super(true);
        this.entity = entity;
        this.source = source;
    }

    @Override
    public LivingEntity getEntity() { return entity; }

    @Override
    public @NotNull DamageSource getSource() { return source; }
}