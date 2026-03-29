package xiao.battleroyale.api.server.utilitity;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILobbyReadApi {

    boolean isLobbyCreated();
    @Nullable ResourceKey<Level> lobbyLevelKey();
    Vec3 lobbyPos();
    Vec3 lobbyDimension();
    boolean lobbyMuteki();
    boolean lobbyHeal();
    boolean lobbyChangeGamemode();
    boolean teleportDropInventory();
    boolean teleportClearInventory();

    @ApiStatus.Internal
    @Deprecated boolean isInLobbyRange(Vec3 pos);
    boolean isInLobbyRange(Vec3 pos, @Nullable ServerLevel serverLevel);
    boolean canMuteki(@NotNull LivingEntity livingEntity);

    void sendLobbyInfo(ServerPlayer player);
    void sendLobbyInfo(ServerLevel serverLevel);
}