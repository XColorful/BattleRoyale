package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunMeleeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunMeleeEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunMeleeEvent extends NeoEvent implements IGunMeleeEvent {

    protected GunMeleeEvent gunMeleeEvent;

    public TaczGunMeleeEvent(GunMeleeEvent gunMeleeEvent) {
        super(gunMeleeEvent);
        this.gunMeleeEvent = gunMeleeEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunMeleeEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return gunMeleeEvent.getShooter();
    }
}