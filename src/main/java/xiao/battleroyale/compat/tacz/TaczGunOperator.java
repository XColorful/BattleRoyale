package xiao.battleroyale.compat.tacz;

import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class TaczGunOperator {

    public static void cancelReload(@NotNull LivingEntity entity) {
        IGunOperator.fromLivingEntity(entity).cancelReload();
    }
}
