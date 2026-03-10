package xiao.battleroyale.compat.fabric.compat.tacz;

import com.tacz.guns.api.event.common.GunShootEvent;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunShootEvent;
import xiao.battleroyale.api.event.IEvent;

public class TaczGunShootEvent implements IGunShootEvent, IEvent {

    protected final GunShootEvent event;

    public TaczGunShootEvent(GunShootEvent event) {
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