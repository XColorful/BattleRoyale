package xiao.battleroyale.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ILivingDamageEvent extends IEvent {

    /**
     * neoforge1.21.1的LivingDamageEvent.Post不支持设置伤害值(取消事件)
     * 请移步至更早的 {@link ILivingHurtEvent}
     */
    @Deprecated(since = "neoforge1.21.1", forRemoval = false)
    @Override
    default void setCanceled(boolean cancel) {}

    @NotNull LivingEntity getEntity();

    @NotNull DamageSource getSource();

    float getDamageAmount();
}
