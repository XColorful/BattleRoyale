package xiao.battleroyale.api.event;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ILivingHealEvent extends IEvent {

    @NotNull LivingEntity getEntity();

    float getHealAmount();

    void setHealAmount(float amount);
}