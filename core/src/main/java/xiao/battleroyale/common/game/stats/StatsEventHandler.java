package xiao.battleroyale.common.game.stats;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.event.game.finish.GameStopFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDamageFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDeathFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDownFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerReviveFinishEvent;
import xiao.battleroyale.api.event.game.starter.GameStartFinishEvent;
import xiao.battleroyale.api.event.game.tick.GameTickFinishEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.ScoreUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static xiao.battleroyale.util.ScoreUtils.*;

public class StatsEventHandler {

    protected static List<String> getObjectiveNames(StatsManager statsManager) {
        List<String> objectiveNames = new ArrayList<>(List.of(
                // 原始数据
                statsManager.player_to_player_damage_ObjectiveName,
                statsManager.other_to_player_damage_ObjectiveName,
                statsManager.player_damage_by_player_ObjectiveName,
                statsManager.player_damage_by_other_ObjectiveName,
                statsManager.player_knock_player_ObjectiveName,
                statsManager.other_knock_player_ObjectiveName,
                statsManager.player_down_by_player_ObjectiveName,
                statsManager.player_down_by_other_ObjectiveName,
                statsManager.player_revive_ObjectiveName,
                statsManager.player_kill_player_ObjectiveName,
                statsManager.other_kill_player_ObjectiveName,
                statsManager.player_death_by_player_ObjectiveName,
                statsManager.player_death_by_other_ObjectiveName,
                statsManager.player_win_ObjectiveName,
                statsManager.player_lose_ObjectiveName,
                // 二次计算
                statsManager.player_attack_rate_ObjectiveName,
                statsManager.player_kd_ObjectiveName,
                statsManager.player_game_total_ObjectiveName, // win 和 lose 都重置的话也没必要保留
                statsManager.player_win_rate_ObjectiveName
        ));
        if (statsManager.syncGameInfoToObjective) objectiveNames.add(statsManager.gameInfoObjectiveName);
        if (statsManager.enableJourneyStats) objectiveNames.add(statsManager.player_journey_ObjectiveName);
        if (statsManager.enableMaxSpeedStats) objectiveNames.add(statsManager.player_max_speed_ObjectiveName);
        return objectiveNames;
    }

    protected static void onGameStart(StatsManager statsManager, GameStartFinishEvent event) {


        if (!statsManager.recordScoreboard) return;

        IGameManager gameManager = event.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();

        // 清除内存数据 (不是记分板)
        SpecialStatsEventHandler.onNewJourney(statsManager, gameManager);
        SpecialStatsEventHandler.onNewMaxSpeed(statsManager, gameManager);

        if (!statsManager.resetScoreboardAtStart) return;

        if (serverLevel == null) return;

        // 重置记分板
        Scoreboard scoreboard = serverLevel.getScoreboard();
        List<String> objectiveNames = getObjectiveNames(statsManager);

        ScoreUtils.clearObjectives(scoreboard, objectiveNames);
        BattleRoyale.LOGGER.info("StatsEventHandler: All objectives cleared");
        ScoreUtils.addObjectivesIfNull(scoreboard, objectiveNames);
    }

    protected static void onGameTick(StatsManager statsManager, GameTickFinishEvent event) {
        IGameManager gameManager = event.getGameManager();
        if (!gameManager.isInGame()) return;

        int gameTime = event.getGameTime();
        if (statsManager.syncGameInfoToObjective) {
            syncGameInfo(statsManager, gameManager, gameTime);
        }
        if (gameTime > statsManager.journeyStatsDelay) {
            SpecialStatsEventHandler.onJourneyStats(statsManager, gameManager);
        }
        if (gameTime > statsManager.maxSpeedStatsDelay) {
            SpecialStatsEventHandler.onMaxSpeedStats(statsManager, gameManager);
        }
    }

    // 每tick检查，而不基于事件通知才更改
    // 采用基于状态对比的轮询同步，确保在 gameStep 加速或 Tick 丢失时记分板数据的最终一致性
    protected static void syncGameInfo(StatsManager statsManager, IGameManager gameManager, int gameTimeInTick) {
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped syncGameInfo");
            return;
        }
        Scoreboard scoreboard = serverLevel.getScoreboard();

        int currentPlayerTotal = getScore(scoreboard, statsManager.gameInfoObjectiveName, statsManager.playerTotalScoreName);
        int actualPlayerTotal = statsManager.totalPlayers; // 游戏开始时写死的数字
        if (currentPlayerTotal != actualPlayerTotal) {
            setScore(scoreboard, statsManager.gameInfoObjectiveName, statsManager.playerTotalScoreName, actualPlayerTotal);
        }

        int currentAlive = getScore(scoreboard, statsManager.aliveScoreName, statsManager.aliveScoreName);
        int actualAlive = gameManager.getTeamManager().getStandingGamePlayerSize();
        if (currentAlive != actualAlive) {
            setScore(scoreboard, statsManager.gameInfoObjectiveName, statsManager.aliveScoreName, actualAlive);
        }

        int currentGameTime = getScore(scoreboard, statsManager.gameInfoObjectiveName, statsManager.gameTimeScoreName);
        int actualGameTime = gameTimeInTick / 20; // 以秒为单位
        if (currentGameTime != actualGameTime) {
            setScore(scoreboard, statsManager.gameInfoObjectiveName, statsManager.gameTimeScoreName, actualGameTime);
        }
    }

    protected static void onGamePlayerDamage(StatsManager statsManager, GamePlayerDamageFinishEvent event) {
        @NotNull GamePlayer gamePlayer = event.getGamePlayer();
        String playerName = gamePlayer.getPlayerName();
        IGameManager gameManager = event.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerDamage");
            return;
        }

        @Nullable ILivingDamageEvent livingDamageEvent = event.getLivingDamageEvent(); // 玩家离线被扣血的就没有事件
        if (livingDamageEvent == null) {
            // 暂时不处理
            BattleRoyale.LOGGER.debug("StatsEventHandler: ILivingDamageEvent is null, skipped onGamePlayerDamage");
            return;
        }
        DamageSource damageSource = livingDamageEvent.getSource();
        @Nullable Entity attackerEntity = damageSource.getEntity();
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? gameManager.getTeamManager().getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        @Nullable Entity damageMethod = damageSource.getDirectEntity();
        float damageAmount = livingDamageEvent.getDamageAmount();

        if (!statsManager.recordScoreboard) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        int score = (int) (damageAmount * statsManager.damageMultiplier);
        if (attackerGamePlayer != null) {
            // 攻击者玩家(对玩家)造成的伤害
            addScore(scoreboard, statsManager.player_to_player_damage_ObjectiveName, attackerGamePlayer.getPlayerName(), score);

            // 玩家承受(攻击者玩家)的伤害
            addScore(scoreboard, statsManager.player_damage_by_player_ObjectiveName, playerName, score);

            // 更新攻击频率比例 (造成/被造成)
            updateRatioScore(scoreboard, attackerGamePlayer.getPlayerName(),
                    statsManager.player_to_player_damage_ObjectiveName, statsManager.player_damage_by_player_ObjectiveName, statsManager.player_attack_rate_ObjectiveName,
                    statsManager.ratioBase, statsManager.mcMaxHealth * statsManager.damageMultiplier);
            updateRatioScore(scoreboard, playerName,
                    statsManager.player_to_player_damage_ObjectiveName, statsManager.player_damage_by_player_ObjectiveName, statsManager.player_attack_rate_ObjectiveName,
                    statsManager.ratioBase, statsManager.mcMaxHealth * statsManager.damageMultiplier);
        } else {
            // 其他方式对玩家造成的伤害
            String otherName = damageMethod != null ? damageMethod.getScoreboardName() : "other";
            addScore(scoreboard, statsManager.other_to_player_damage_ObjectiveName, otherName, score);

            // 玩家承受(非攻击者玩家)的伤害
            addScore(scoreboard, statsManager.player_damage_by_other_ObjectiveName, playerName, score);
        }
    }

    protected static void onGamePlayerDown(StatsManager statsManager, GamePlayerDownFinishEvent event) {
        @NotNull GamePlayer gamePlayer = event.getGamePlayer();
        if (gamePlayer.isEliminated()) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: GamePlayer {} is eliminated, skipped onGamePlayerDown", gamePlayer.getNameWithId());
            return;
        }
        String playerName = gamePlayer.getPlayerName();
        IGameManager gameManager = event.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerDown");
            return;
        }

        @Nullable ILivingDeathEvent livingDeathEvent = event.getLivingDeathEvent();
        if (livingDeathEvent == null) {
            // 暂时不处理
            BattleRoyale.LOGGER.debug("StatsEventHandler: ILivingDeathEvent is null, skipped onGamePlayerDown");
            return;
        }
        DamageSource damageSource = livingDeathEvent.getSource();
        @Nullable Entity attackerEntity = damageSource.getEntity();
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? gameManager.getTeamManager().getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        @Nullable Entity damageMethod = damageSource.getDirectEntity();

        if (!statsManager.recordScoreboard) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        if (attackerGamePlayer != null) {
            // 攻击者玩家(对玩家)造成的击倒数
            addScore(scoreboard, statsManager.player_knock_player_ObjectiveName, attackerGamePlayer.getPlayerName(), 1);

            // 玩家被(攻击者玩家)击倒数
            addScore(scoreboard, statsManager.player_down_by_player_ObjectiveName, playerName, 1);
        } else {
            // 其他方式对玩家的击倒数
            String sourceName = damageMethod != null ? damageMethod.getScoreboardName() : "other";
            addScore(scoreboard, statsManager.other_knock_player_ObjectiveName, sourceName, 1);

            // 玩家被(非攻击者玩家)击倒数
            addScore(scoreboard, statsManager.player_down_by_other_ObjectiveName, playerName, 1);
        }
    }

    protected static void onGamePlayerRevive(StatsManager statsManager, GamePlayerReviveFinishEvent event) {
        @NotNull GamePlayer gamePlayer = event.getGamePlayer();
        String playerName = gamePlayer.getPlayerName();
        @Nullable ServerLevel serverLevel = event.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerRevive");
            return;
        }

        if (!statsManager.recordScoreboard) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        // 玩家被扶起次数
        addScore(scoreboard, statsManager.player_revive_ObjectiveName, playerName, 1);
    }

    protected static void onGamePlayerDeath(StatsManager statsManager, GamePlayerDeathFinishEvent event) {
        @NotNull GamePlayer gamePlayer = event.getGamePlayer();
        String playerName = gamePlayer.getPlayerName();
        IGameManager gameManager = event.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerDeath");
            return;
        }

        @Nullable ILivingDeathEvent livingDeathEvent = event.getLivingDeathEvent(); // 被游戏机制淘汰的就没有事件
        if (livingDeathEvent == null) {
            // 暂时不处理
            BattleRoyale.LOGGER.debug("StatsEventHandler: ILivingDeathEvent is null, skipped onGamePlayerDeath");
            return;
        }
        DamageSource damageSource = livingDeathEvent.getSource();
        @Nullable Entity attackerEntity = damageSource.getEntity();
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? gameManager.getTeamManager().getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        @Nullable Entity damageMethod = damageSource.getDirectEntity();

        if (!statsManager.recordScoreboard) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        if (attackerGamePlayer != null) {
            // 攻击者玩家(对玩家)造成的击杀数
            addScore(scoreboard, statsManager.player_kill_player_ObjectiveName, attackerGamePlayer.getPlayerName(), 1);

            // 玩家被(攻击者玩家)击杀数
            addScore(scoreboard, statsManager.player_death_by_player_ObjectiveName, playerName, 1);

            // 更新 KD 比例
            updateRatioScore(scoreboard, attackerGamePlayer.getPlayerName(),
                    statsManager.player_kill_player_ObjectiveName, statsManager.player_death_by_player_ObjectiveName, statsManager.player_kd_ObjectiveName,
                    statsManager.ratioBase, 1);
            updateRatioScore(scoreboard, playerName,
                    statsManager.player_kill_player_ObjectiveName, statsManager.player_death_by_player_ObjectiveName, statsManager.player_kd_ObjectiveName,
                    statsManager.ratioBase, 1);
        } else {
            // 其他方式对玩家造成的击杀数
            String sourceName = damageMethod != null ? damageMethod.getScoreboardName() : "other";
            addScore(scoreboard, statsManager.other_kill_player_ObjectiveName, sourceName, 1);

            // 玩家被(非攻击者玩家)击杀数
            addScore(scoreboard, statsManager.player_death_by_other_ObjectiveName, playerName, 1);
        }
    }

    protected static void onGameStop(StatsManager statsManager, GameStopFinishEvent event) {
        ;
    }

    protected static void onGameComplete(StatsManager statsManager, GameCompleteFinishEvent event) {
        if (!event.hasWinner()) return;

        @Nullable ServerLevel serverLevel = event.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGameComplete");
            return;
        }
        List<GamePlayer> gamePlayers = event.getGamePlayers();
        Set<GamePlayer> winnerGamePlayers = event.getWinnerGamePlayers();

        if (statsManager.enableJourneyStats) {
            SpecialStatsEventHandler.onJourneyComplete(statsManager, event);
        }
        if (statsManager.enableMaxSpeedStats) {
            SpecialStatsEventHandler.onMaxSpeedComplete(statsManager, event);
        }

        if (!statsManager.recordScoreboard) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        for (GamePlayer gamePlayer : gamePlayers) {
            String playerName = gamePlayer.getPlayerName();
            if (winnerGamePlayers.contains(gamePlayer)) {
                addScore(scoreboard, statsManager.player_win_ObjectiveName, playerName, 1);
            } else {
                addScore(scoreboard, statsManager.player_lose_ObjectiveName, playerName, 1);
            }

            // 更新总场数 [ 赢 + 输 ]
            updateTotalScore(scoreboard, playerName,
                    statsManager.player_win_ObjectiveName,
                    statsManager.player_lose_ObjectiveName,
                    statsManager.player_game_total_ObjectiveName);

            // 更新胜率比例 [ 赢 / (输 + 赢) ]
            updatePercentageScore(scoreboard, playerName,
                    statsManager.player_win_ObjectiveName, statsManager.player_lose_ObjectiveName, statsManager.player_win_rate_ObjectiveName,
                    statsManager.ratioBase);
        }
    }
}