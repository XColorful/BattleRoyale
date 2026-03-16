package xiao.battleroyale.api.game.stats;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.Set;

public interface IStatsManager extends IGameSubManager, IGameEventStatsRecorder, IStatsQuery,
        IZoneStatsRecorder, IGameruleStatsRecorder, ISpawnStatsRecorder {

    boolean shouldRecordStats();

    // 参与记录的 GamePlayer 列表
    Set<GamePlayer> getRecordGamePlayers();
    boolean isInRecordGamePlayers(@NotNull GamePlayer player);

    String getStatsFilePath();
    default void saveStats() {
        saveStats(getStatsFilePath());
    }
    void saveStats(String filePath);
}
