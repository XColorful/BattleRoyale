package xiao.battleroyale.api.compat.tacz;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import xiao.battleroyale.api.common.McSide;

public interface IBulletHurtEvent {

    McSide getMcSide();

    float getBaseDamage();
    void setBaseDamage(float damage);

    boolean isHeadShot();
    float getHeadShotMultiplier();
    void setHeadShotMultiplier(float multiplier);

    Entity getBullet();
    Entity getHurtEntity();
    Entity getAttacker();

    ResourceLocation getGunId();
}
