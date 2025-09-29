package xiao.battleroyale.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ILivingDamageEvent extends IEvent {

    @NotNull LivingEntity getEntity();

    @NotNull DamageSource getSource();

    float getDamageAmount();
}
