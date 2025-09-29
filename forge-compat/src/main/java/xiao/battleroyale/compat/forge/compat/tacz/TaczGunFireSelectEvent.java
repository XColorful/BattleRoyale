package xiao.battleroyale.compat.forge.compat.tacz;

import com.tacz.guns.api.event.common.GunFireSelectEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunFireSelectEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class TaczGunFireSelectEvent extends ForgeEvent implements IGunFireSelectEvent {

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
