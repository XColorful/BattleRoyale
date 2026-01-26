```java
package xiao.battleroyale.api.game.stats;

public interface IStatsManager extends IGameSubManager, IGameEventStatsRecorder, IStatsQuery,
IZoneStatsRecorder, IGameruleStatsRecorder, ISpawnStatsRecorder {
    String getStatsFilePath();
    void saveStats(String filePath);  
}
```