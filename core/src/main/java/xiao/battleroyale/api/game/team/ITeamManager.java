package xiao.battleroyale.api.game.team;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.UUID;

public interface ITeamManager extends IGameSubManager, IGameTeamReadApi {
    
    void teleportToLobby(ServerPlayer player);

    void joinTeam(ServerPlayer player);

    void joinTeamSpecific(ServerPlayer player, int teamId);

    boolean leaveTeam(ServerPlayer player);

    void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer);

    void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer);

    void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer);

    void declineInvite(ServerPlayer player, ServerPlayer senderPlayer);

    void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer);

    void acceptRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);

    void declineRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);

    void sendPlayerTeamId(ServerPlayer player);

    void onBotGamePlayerChanged(GamePlayer gamePlayer, UUID uuid);

    int getStandingTeamCount();

    boolean shouldAutoJoin();

    boolean forceEliminatePlayerSilence(GamePlayer gamePlayer);
}
