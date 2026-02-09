package xiao.battleroyale.common.game.stats;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
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
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.List;
import java.util.Set;

public class StatsEventHandler {

    protected static void onGameStart(StatsManager statsManager, GameStartFinishEvent event) {
        if (!statsManager.recordScordboard) return;

        // 重置计分板
        if (!statsManager.resetScordboardAtStart) return;
        ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        List<String> objectiveNames = List.of(
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
                statsManager.player_lose_ObjectiveName
        );
        for (String name : objectiveNames) {
            Objective oldObj = scoreboard.getObjective(name);
            if (oldObj != null) {
                scoreboard.removeObjective(oldObj);
            }
            scoreboard.addObjective(name, ObjectiveCriteria.DUMMY,
                    Component.literal(name),
                    ObjectiveCriteria.RenderType.INTEGER);
        }
        BattleRoyale.LOGGER.info("StatsEventHandler: All objectives re-created for a new game.");
    }

    protected static void onGamePlayerDamage(StatsManager statsManager, GamePlayerDamageFinishEvent event) {
        @NotNull GamePlayer gamePlayer = event.getGamePlayer();
        String playerName = gamePlayer.getPlayerName();
        @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerDamage");
            return;
        }

        @Nullable ILivingDamageEvent livingDamageEvent = event.getLivingDamageEvent(); // 玩家离线被扣血的就没有事件
        if (livingDamageEvent == null) {
            // 暂时不处理
            return;
        }
        DamageSource damageSource = livingDamageEvent.getSource();
        @Nullable Entity attackerEntity = damageSource.getEntity();
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? GameTeamManager.getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        @Nullable Entity damageMethod = damageSource.getDirectEntity();
        float damageAmount = livingDamageEvent.getDamageAmount();

        if (!statsManager.recordScordboard) return;
        Scoreboard scoreboard = serverLevel.getScoreboard();

        int score = (int) (damageAmount * 5);
        if (attackerGamePlayer != null) {
            // 攻击者玩家(对玩家)造成的伤害
            addScore(scoreboard, statsManager.player_to_player_damage_ObjectiveName, attackerGamePlayer.getPlayerName(), score);

            // 玩家承受(攻击者玩家)的伤害
            addScore(scoreboard, statsManager.player_damage_by_player_ObjectiveName, playerName, score);
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
        @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerDown");
            return;
        }

        @Nullable ILivingDeathEvent livingDeathEvent = event.getLivingDeathEvent();
        if (livingDeathEvent == null) {
            // 暂时不处理
            return;
        }
        DamageSource damageSource = livingDeathEvent.getSource();
        @Nullable Entity attackerEntity = damageSource.getEntity();
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? GameTeamManager.getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        @Nullable Entity damageMethod = damageSource.getDirectEntity();

        if (!statsManager.recordScordboard) return;
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
        @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerRevive");
            return;
        }

        if (!statsManager.recordScordboard) return;
        Scoreboard scoreboard = serverLevel.getScoreboard();

        // 玩家被扶起次数
        addScore(scoreboard, statsManager.player_revive_ObjectiveName, playerName, 1);
    }

    protected static void onGamePlayerDeath(StatsManager statsManager, GamePlayerDeathFinishEvent event) {
        @NotNull GamePlayer gamePlayer = event.getGamePlayer();
        String playerName = gamePlayer.getPlayerName();
        @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGamePlayerDeath");
            return;
        }

        @Nullable ILivingDeathEvent livingDeathEvent = event.getLivingDeathEvent(); // 被游戏机制淘汰的就没有事件
        if (livingDeathEvent == null) {
            // 暂时不处理
            return;
        }
        DamageSource damageSource = livingDeathEvent.getSource();
        @Nullable Entity attackerEntity = damageSource.getEntity();
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? GameTeamManager.getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        @Nullable Entity damageMethod = damageSource.getDirectEntity();

        if (!statsManager.recordScordboard) return;
        Scoreboard scoreboard = serverLevel.getScoreboard();

        if (attackerGamePlayer != null) {
            // 攻击者玩家(对玩家)造成的击杀数
            addScore(scoreboard, statsManager.player_kill_player_ObjectiveName, attackerGamePlayer.getPlayerName(), 1);

            // 玩家被(攻击者玩家)击杀数
            addScore(scoreboard, statsManager.player_death_by_player_ObjectiveName, playerName, 1);
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

        @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: Failed to get ServerLevel by GameManager, skipped onGameComplete");
            return;
        }
        List<GamePlayer> gamePlayers = event.getGamePlayers();
        Set<GamePlayer> winnerGamePlayers = event.getWinnerGamePlayers();

        if (!statsManager.recordScordboard) return;
        Scoreboard scoreboard = serverLevel.getScoreboard();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (winnerGamePlayers.contains(gamePlayer)) {
                addScore(scoreboard, statsManager.player_win_ObjectiveName, gamePlayer.getPlayerName(), 1);
            } else {
                addScore(scoreboard, statsManager.player_lose_ObjectiveName, gamePlayer.getPlayerName(), 1);
            }
        }
    }

    public static Score getSafeScore(Scoreboard scoreboard, String objectiveName, String playerName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        // 如果表不存在，需要手动创建，而不能用Scoreboard的getOrCreateObjective
        if (objective == null) {
            objective = scoreboard.addObjective(
                    objectiveName,
                    ObjectiveCriteria.DUMMY,
                    Component.literal(objectiveName),
                    ObjectiveCriteria.RenderType.INTEGER
            );
        }
        return scoreboard.getOrCreatePlayerScore(playerName, objective);
    }

    private static void addScore(Scoreboard scoreboard, String objectiveName, String playerName, int amount) {
        if (amount == 0) return;
        getSafeScore(scoreboard, objectiveName, playerName).add(amount);
    }
}