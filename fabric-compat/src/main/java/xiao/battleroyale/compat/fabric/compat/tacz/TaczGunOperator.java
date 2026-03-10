package xiao.battleroyale.compat.fabric.compat.tacz;

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
        // 空实现，不做任何操作
    }
}