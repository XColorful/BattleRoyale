package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunShootEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunShootEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunShootEvent extends NeoEvent implements IGunShootEvent {

    protected GunShootEvent gunShootEvent;

    public TaczGunShootEvent(GunShootEvent gunShootEvent) {
        super(gunShootEvent);
        this.gunShootEvent = gunShootEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunShootEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return gunShootEvent.getShooter();
    }
}