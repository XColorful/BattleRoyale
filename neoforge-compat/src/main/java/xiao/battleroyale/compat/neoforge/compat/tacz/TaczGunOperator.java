package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.compat.tacz.ITaczGunOperator;

public class TaczGunOperator implements ITaczGunOperator {

    private static class TaczGunOperatorHolder {
        private static final TaczGunOperator INSTANCE = new TaczGunOperator();
    }

    public static TaczGunOperator get() {
        return TaczGunOperatorHolder.INSTANCE;
    }

    private TaczGunOperator() {}

    @Override
    public void cancelLivingEntityReload(LivingEntity livingEntity) {
        IGunOperator.fromLivingEntity(livingEntity).cancelReload();
    }
}