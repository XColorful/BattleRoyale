package xiao.battleroyale.api.compat.tacz;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEvent;

public interface IGunReloadEvent extends IEvent {

    McSide getMcSide();

    LivingEntity getEntity();
}
