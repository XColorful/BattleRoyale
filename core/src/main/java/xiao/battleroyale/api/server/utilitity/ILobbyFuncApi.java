package xiao.battleroyale.api.server.utilitity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface ILobbyFuncApi {

    void healPlayer(@NotNull LivingEntity livingEntity);

    boolean teleportToLobby(@NotNull LivingEntity livingEntity);

    boolean setLobby(Vec3 centerPos, Vec3 dimension,
                     boolean shouldMuteki, boolean shouldHeal, boolean changeGamemode,
                     boolean teleportDropInventory, boolean teleportClearInventory);

    boolean setLobby(Vec3 coords, double radius);
}
