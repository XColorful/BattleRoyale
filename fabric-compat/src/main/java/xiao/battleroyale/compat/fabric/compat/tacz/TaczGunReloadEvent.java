package xiao.battleroyale.compat.fabric.compat.tacz;

import com.tacz.guns.api.event.common.GunReloadEvent;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunReloadEvent;
import xiao.battleroyale.api.event.IEvent;

public class TaczGunReloadEvent implements IGunReloadEvent, IEvent {

    protected final GunReloadEvent event;

    public TaczGunReloadEvent(GunReloadEvent event) {
        this.event = event;
    }

    @Override
    public McSide getMcSide() {
        return this.event.getLogicalSide().isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getEntity() {
        return event.getEntity();
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