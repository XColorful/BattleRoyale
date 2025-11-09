package xiao.battleroyale.api.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IGameFunc {

    void sendGameSpectateMessage(@NotNull ServerPlayer player);
    boolean teleportToLobby(@NotNull LivingEntity livingEntity);
    boolean spectateGame(ServerPlayer player);
    void checkIfGameShouldEnd();

    void finishGame(boolean hasWinner);

    boolean isOnServerStopping();
    @ApiStatus.Internal
    void onServerStopping();

    @ApiStatus.Internal
    void addGameTimeAndTick();
}
