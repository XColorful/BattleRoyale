```java
package xiao.battleroyale.api.game.stats;

public interface IStatsManager extends IGameSubManager, IGameEventStatsRecorder, IStatsQuery,
IZoneStatsRecorder, IGameruleStatsRecorder, ISpawnStatsRecorder {
	Set<GamePlayer> getRecordGamePlayers();
	boolean isInRecordGamePlayers(@NotNull GamePlayer player);
    String getStatsFilePath();
    void saveStats(String filePath);  
}
```