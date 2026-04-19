package xiao.battleroyale.api.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface IGameSaveTeleporter {

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    default void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull Vec3 teleportPos) {
        safeTeleport(livingEntity, teleportPos.x, teleportPos.y, teleportPos.z);
    }
    void safeTeleport(@NotNull LivingEntity livingEntity, double x, double y, double z);

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     * (跨纬度版本)
     */
    default void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel, @NotNull Vec3 teleportPos, float yaw, float pitch) {
        safeTeleport(livingEntity, serverLevel, teleportPos.x, teleportPos.y, teleportPos.z, yaw, pitch);
    }
    void safeTeleport(@NotNull LivingEntity livingEntity, @NotNull ServerLevel serverLevel, double x, double y, double z, float yaw, float pitch);
}
