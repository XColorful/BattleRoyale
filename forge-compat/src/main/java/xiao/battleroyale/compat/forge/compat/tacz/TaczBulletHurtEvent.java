package xiao.battleroyale.compat.forge.compat.tacz;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IBulletHurtEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class TaczBulletHurtEvent extends ForgeEvent implements IBulletHurtEvent {

    protected EntityHurtByGunEvent.Pre bulletHurtEvent;

    public TaczBulletHurtEvent(EntityHurtByGunEvent.Pre entityHurtByGunEvent) {
        super(entityHurtByGunEvent);
        this.bulletHurtEvent = entityHurtByGunEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.bulletHurtEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public float getBaseDamage() {
        return bulletHurtEvent.getBaseAmount();
    }

    @Override
    public void setBaseDamage(float damage) {
        bulletHurtEvent.setBaseAmount(damage);
    }

    @Override
    public boolean isHeadShot() {
        return bulletHurtEvent.isHeadShot();
    }

    @Override
    public float getHeadShotMultiplier() {
        return bulletHurtEvent.getHeadshotMultiplier();
    }

    @Override
    public void setHeadShotMultiplier(float multiplier) {
        bulletHurtEvent.setHeadshotMultiplier(multiplier);
    }

    @Override
    public Entity getBullet() {
        return bulletHurtEvent.getBullet();
    }

    @Override
    public Entity getHurtEntity() {
        return bulletHurtEvent.getHurtEntity();
    }

    @Override
    public Entity getAttacker() {
        return bulletHurtEvent.getAttacker();
    }

    @Override
    public ResourceLocation getGunId() {
        return bulletHurtEvent.getGunId();
    }
}
