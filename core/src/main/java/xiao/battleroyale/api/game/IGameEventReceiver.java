package xiao.battleroyale.api.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.common.game.team.GamePlayer;

public interface IGameEventReceiver {

    @ApiStatus.Internal
    void onPlayerLoggedIn(ServerPlayer player);

    @ApiStatus.Internal
    void onPlayerLoggedOut(ServerPlayer player);

    @ApiStatus.Internal
    void onPlayerDown(@NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event);

    @ApiStatus.Internal
    void onPlayerRevived(@NotNull GamePlayer gamePlayer);

    @ApiStatus.Internal
    void onPlayerDeath(@NotNull GamePlayer gamePlayer, ILivingDeathEvent event);
}
