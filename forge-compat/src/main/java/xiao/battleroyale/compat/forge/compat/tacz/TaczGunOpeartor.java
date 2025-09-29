package xiao.battleroyale.compat.forge.compat.tacz;

import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.compat.tacz.ITaczGunOperator;

public class TaczGunOpeartor implements ITaczGunOperator {

    private static class TaczGunOperatorHolder {
        private static final TaczGunOpeartor INSTANCE = new TaczGunOpeartor();
    }

    public static TaczGunOpeartor get() {
        return TaczGunOperatorHolder.INSTANCE;
    }

    private TaczGunOpeartor() {}

    @Override
    public void cancelLivingEntityReload(LivingEntity livingEntity) {
        IGunOperator.fromLivingEntity(livingEntity).cancelReload();
    }
}
