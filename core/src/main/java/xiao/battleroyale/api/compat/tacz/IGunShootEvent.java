package xiao.battleroyale.api.compat.tacz;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEvent;

public interface IGunShootEvent extends IEvent {

    McSide getMcSide();

    LivingEntity getShooter();
}
