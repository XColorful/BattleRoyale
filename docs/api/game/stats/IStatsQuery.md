```java
package xiao.battleroyale.api.game.stats;

public interface IStatsQuery {
    void getGamePlayerStats(int playerId);
    void getGamePlayerStats(UUID playerUUID);
    void getGamePlayerStats(String playerName);
    void getGameTeamStats(int teamId);
    void getGameruleStats(String gameruleName);
}
```