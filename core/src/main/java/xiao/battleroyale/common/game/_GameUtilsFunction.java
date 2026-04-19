package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.IGameManager;

import java.util.HashSet;
import java.util.Set;

public class _GameUtilsFunction {

    @ApiStatus.Internal
    public static void safeTeleport(@NotNull IGameManager gameManager, @NotNull LivingEntity livingEntity,
                                    double x, double y, double z) {
        if (gameManager.isOnServerStopping()) {
            return;
        }
        livingEntity.fallDistance = 0;
        livingEntity.teleportTo(x, y, z);
    }

    private static final Set<Relative> emptyRelativeMovement = new HashSet<>();
    @ApiStatus.Internal
    public static void safeTeleport(@NotNull IGameManager gameManager, @NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel,
                                    double x, double y, double z, float yaw, float pitch) {
        if (gameManager.isOnServerStopping()) {
            return;
        }
        livingEntity.fallDistance = 0;
        livingEntity.teleportTo(serverLevel, x, y, z, emptyRelativeMovement, yaw, pitch, true);
    }
}
