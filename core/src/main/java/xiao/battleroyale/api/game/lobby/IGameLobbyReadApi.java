package xiao.battleroyale.api.game.lobby;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.server.utilitity.ILobbyReadApi;

public interface IGameLobbyReadApi extends ILobbyReadApi {

    void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner);
}
