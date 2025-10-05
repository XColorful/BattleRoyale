package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunFireSelectEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunFireSelectEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunFireSelectEvent extends NeoEvent implements IGunFireSelectEvent {

    protected GunFireSelectEvent gunFireSelectEvent;

    public TaczGunFireSelectEvent(GunFireSelectEvent gunFireSelectEvent) {
        super(gunFireSelectEvent);
        this.gunFireSelectEvent = gunFireSelectEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunFireSelectEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return gunFireSelectEvent.getShooter();
    }
}