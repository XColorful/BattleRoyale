package xiao.battleroyale.compat.forge.compat.tacz;

import com.tacz.guns.api.event.common.GunMeleeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunMeleeEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class TaczGunMeleeEvent extends ForgeEvent implements IGunMeleeEvent {

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
