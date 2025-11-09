package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;

import java.util.HashSet;
import java.util.Set;

public class GameUtilsFunction {

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public static void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull Vec3 teleportPos) {
        safeTeleport(livingEntity, teleportPos.x, teleportPos.y, teleportPos.z);
    }
    public static void safeTeleport(@NotNull LivingEntity livingEntity, double x, double y, double z) {
        if (BattleRoyale.getGameManager().isOnServerStopping()) {
            return;
        }
        livingEntity.fallDistance = 0;
        livingEntity.teleportTo(x, y, z);
    }
    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     * (跨纬度版本)
     */
    public static void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel, @NotNull Vec3 teleportPos, float yaw, float pitch) {
        safeTeleport(livingEntity, serverLevel, teleportPos.x, teleportPos.y, teleportPos.z, yaw, pitch);
    }
    private static final Set<RelativeMovement> emptyRelativeMovement = new HashSet<>();
    public static void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel, double x, double y, double z, float yaw, float pitch) {
        if (BattleRoyale.getGameManager().isOnServerStopping()) {
            return;
        }
        livingEntity.fallDistance = 0;
        livingEntity.teleportTo(serverLevel, x, y, z, emptyRelativeMovement, yaw, pitch);
    }
}
