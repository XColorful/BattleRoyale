package xiao.battleroyale.api.event;

import net.minecraft.world.entity.LivingEntity;

public interface ILivingDeathEvent extends IEvent {

    LivingEntity getEntity();
}
