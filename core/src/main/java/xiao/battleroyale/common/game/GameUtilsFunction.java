package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;

@Deprecated(forRemoval = false)
public class GameUtilsFunction {

    public static void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull Vec3 teleportPos) {
        safeTeleport(livingEntity, teleportPos.x, teleportPos.y, teleportPos.z);
    }
    public static void safeTeleport(@NotNull LivingEntity livingEntity, double x, double y, double z) {
        _GameUtilsFunction.safeTeleport(BattleRoyale.getGameManager(), livingEntity, x, y, z);
    }


    public static void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel, @NotNull Vec3 teleportPos, float yaw, float pitch) {
        safeTeleport(livingEntity, serverLevel, teleportPos.x, teleportPos.y, teleportPos.z, yaw, pitch);
    }
    public static void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel, double x, double y, double z, float yaw, float pitch) {
        _GameUtilsFunction.safeTeleport(BattleRoyale.getGameManager(), livingEntity, serverLevel, x, y, z, yaw, pitch);
    }
}
