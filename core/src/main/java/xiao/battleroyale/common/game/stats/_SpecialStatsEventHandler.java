package xiao.battleroyale.common.game.stats;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.ClassUtils;
import xiao.battleroyale.util.ScoreUtils;

import java.util.ArrayList;
import java.util.List;

public class _SpecialStatsEventHandler {

    private static final ClassUtils.ArrayMap<GamePlayer, JourneyStats> currentJourney = new ClassUtils.ArrayMap<>(JourneyStats::getGamePlayer);
    private static final List<JourneyStats> completedJourney = new ArrayList<>();

    // --------重置数据--------

    protected static void onNewJourney(StatsManager statsManager, IGameManager gameManager) {
        currentJourney.clear();
        completedJourney.clear();
        for (GamePlayer gamePlayer : statsManager.getRecordGamePlayers()) {
            currentJourney.add(new JourneyStats(gamePlayer));
        }
    }
    protected static void onNewMaxSpeed(StatsManager statsManager, IGameManager gameManager) {
    }

    // --------维护数据--------

    protected static void onJourneyStats(StatsManager statsManager, IGameManager gameManager) {
        List<GamePlayer> completedGamePlayers = new ArrayList<>();
        for (JourneyStats journeyStats : currentJourney) {
            GamePlayer gamePlayer = journeyStats.getGamePlayer();
            journeyStats.addJourney(gamePlayer.getLastPos());
            if (gamePlayer.isEliminated()) {
                journeyStats.complete();
                completedJourney.add(journeyStats);
                completedGamePlayers.add(gamePlayer);
            }
        }
        for (GamePlayer gamePlayer : completedGamePlayers) {
            currentJourney.remove(gamePlayer);
        }
    }
    protected static void onMaxSpeedStats(StatsManager statsManager, IGameManager gameManager) {

        // ----记分板----
        if (statsManager.recordScoreboard) {
        }
    }

    // --------写入记分板--------

    protected static void onJourneyComplete(StatsManager statsManager, GameCompleteFinishEvent event) {
        if (!currentJourney.isEmpty()) {
            for (JourneyStats journeyStats : currentJourney) {
                journeyStats.complete();
                completedJourney.add(journeyStats);
            }
            currentJourney.clear();
        }

        IGameManager gameManager = event.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("SpecialStatsEventHandler: Failed to get ServerLevel by GameManager, skipped onJourneyComplete");
            return;
        }

        // ----记分板----
        if (statsManager.recordScoreboard) {
            Scoreboard scoreboard = serverLevel.getScoreboard();
            for (JourneyStats journeyStats : completedJourney) {
                ScoreUtils.addScore(scoreboard, statsManager.player_journey_ObjectiveName, journeyStats.getPlayerName(), (int) journeyStats.journeyTotal);
            }
        }
    }
    protected static void onMaxSpeedComplete(StatsManager statsManager, GameCompleteFinishEvent event) {
    }

    public static class JourneyStats {
        private final GamePlayer gamePlayer;
        private boolean isComplete = false;
        private Vec3 previousPos;
        private double journeyTotal = 0;

        public JourneyStats(GamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
        }

        public GamePlayer getGamePlayer() {
            return this.gamePlayer;
        }
        public String getPlayerName() {
            return this.gamePlayer.getPlayerName();
        }

        public void complete() {
            isComplete = true;
        }

        public void addJourney(Vec3 currentPos) {
            if (isComplete) return;
            if (currentPos == previousPos) return; // 引用相同

            if (previousPos == null) { // 第一次记录
                previousPos = currentPos;
                return;
            }

            journeyTotal += previousPos.distanceTo(currentPos);
            previousPos = currentPos;
        }
    }
}
