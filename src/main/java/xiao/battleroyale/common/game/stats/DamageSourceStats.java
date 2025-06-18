package xiao.battleroyale.common.game.stats;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceStats extends AbstractStats {

    DamageSource damageSource;

    public DamageSourceStats(DamageSource damageSource) {
        super();
        this.damageSource = damageSource;
    }
}
