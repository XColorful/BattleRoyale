package xiao.battleroyale.api.game.stats;

import java.util.UUID;

public interface IStatsQuery {

    void getGamePlayerStats(int playerId);

    void getGamePlayerStats(UUID playerUUID);

    void getGamePlayerStats(String playerName);

    void getGameTeamStats(int teamId);

    void getGameruleStats(String gameruleName);
}
