package xiao.battleroyale.compat.forge.compat.tacz;

import com.tacz.guns.api.event.common.GunReloadEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunReloadEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class TaczGunReloadEvent extends ForgeEvent implements IGunReloadEvent {

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
