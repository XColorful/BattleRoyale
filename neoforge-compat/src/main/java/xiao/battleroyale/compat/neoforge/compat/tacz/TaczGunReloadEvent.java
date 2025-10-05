package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunReloadEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunReloadEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunReloadEvent extends NeoEvent implements IGunReloadEvent {

    protected GunReloadEvent gunReloadEvent;

    public TaczGunReloadEvent(GunReloadEvent gunReloadEvent) {
        super(gunReloadEvent);
        this.gunReloadEvent = gunReloadEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunReloadEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getEntity() {
        return gunReloadEvent.getEntity();
    }
}