package xiao.battleroyale.api.game.team;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.UUID;

public interface IGameTeamReadApi {

    int getPlayerLimit();
    @Nullable GamePlayer getGamePlayerByUUID(UUID uuid);
    @Nullable GamePlayer getGamePlayerBySingleId(int playerId);
    boolean hasStandingGamePlayer(UUID uuid);
    List<GameTeam> getGameTeams();
    @Nullable GameTeam getGameTeamById(int teamId);
    List<GamePlayer> getGamePlayers();
    List<GamePlayer> getStandingGamePlayers();
    @Nullable GamePlayer getRandomStandingGamePlayer();
    int getTotalMembers();
}
