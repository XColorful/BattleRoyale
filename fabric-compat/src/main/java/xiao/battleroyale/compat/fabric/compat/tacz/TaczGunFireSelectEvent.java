package xiao.battleroyale.compat.fabric.compat.tacz;

import com.tacz.guns.api.event.common.GunFireSelectEvent;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunFireSelectEvent;
import xiao.battleroyale.api.event.IEvent;

public class TaczGunFireSelectEvent implements IGunFireSelectEvent, IEvent {

    protected final GunFireSelectEvent event;

    public TaczGunFireSelectEvent(GunFireSelectEvent event) {
        this.event = event;
    }

    @Override
    public McSide getMcSide() {
        return this.event.getLogicalSide().isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return event.getShooter();
    }

    @Override
    public boolean isCanceled() {
        return event.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        event.setCanceled(cancel);
    }
}