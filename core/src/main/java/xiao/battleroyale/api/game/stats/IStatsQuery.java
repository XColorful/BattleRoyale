package xiao.battleroyale.api.game.stats;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.UUID;

public interface IStatsQuery {

    @Nullable IGamePlayerStats getGamePlayerStats(GamePlayer gamePLayer);

    void getGamePlayerStats(int playerId);

    void getGamePlayerStats(UUID playerUUID);

    void getGamePlayerStats(String playerName);

    void getGameTeamStats(int teamId);

    void getGameruleStats(String gameruleName);
}
