package xiao.battleroyale.api.game.stats;

import xiao.battleroyale.api.game.IGameSubManager;

public interface IStatsManager extends IGameSubManager, IGameEventStatsRecorder, IStatsQuery,
        IZoneStatsRecorder, IGameruleStatsRecorder, ISpawnStatsRecorder {

    boolean shouldRecordStats();

    String getStatsFilePath();
    default void saveStats() {
        saveStats(getStatsFilePath());
    }
    void saveStats(String filePath);
}
