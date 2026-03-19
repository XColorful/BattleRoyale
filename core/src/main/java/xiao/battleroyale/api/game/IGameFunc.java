package xiao.battleroyale.api.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.server.IServerStopHandler;

public interface IGameFunc extends IServerStopHandler {

    void sendGameSpectateMessage(@NotNull ServerPlayer player);
    boolean teleportToLobby(@NotNull LivingEntity livingEntity);
    boolean spectateGame(ServerPlayer player);
    void checkIfGameShouldEnd();

    void finishGame(boolean hasWinner);

    boolean isOnServerStopping();
    @ApiStatus.Internal
    @Override void onServerStopping();

    @ApiStatus.Internal
    void addGameTimeAndTick();
}
