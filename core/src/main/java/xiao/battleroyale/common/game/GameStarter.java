package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.compat.playerrevive.BleedingHandler;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.event.game.DamageEventHandler;
import xiao.battleroyale.event.game.LogEventHandler;
import xiao.battleroyale.event.game.LoopEventHandler;
import xiao.battleroyale.event.game.PlayerDeathEventHandler;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import static xiao.battleroyale.api.data.io.TempDataTag.GAME_MANAGER;
import static xiao.battleroyale.api.data.io.TempDataTag.GLOBAL_OFFSET;

public class GameStarter {

    protected static boolean initGameConfigSetup(GameManager gameManager) {
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), gameManager.gameruleConfigId);
        if (gameruleConfig == null) {
            if (gameManager.serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(gameManager.serverLevel, "battleroyale.message.missing_gamerule_config");
            } else {
                BattleRoyale.LOGGER.error("GameManager.serverLevel is null in initGameConfigSetup: gameruleConfig == null");
            }
            return false;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (brEntry == null || gameEntry == null) {
            if (gameManager.serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(gameManager.serverLevel, "battleroyale.message.missing_gamerule_config");
            } else {
                BattleRoyale.LOGGER.error("GameManager.serverLevel is null in initGameConfigSetup: brEntry == null || gameEntry == null");
            }
            return false;
        }
        gameManager.maxGameTime = brEntry.maxGameTime;
        gameManager.winnerTeamTotal = brEntry.winnerTeamTotal;
        gameManager.requiredGameTeam = brEntry.requiredTeamToStart;
        gameManager.gameEntry = gameEntry.copy();
        BleedingHandler.setBleedDamage(gameManager.gameEntry.downDamageList);
        BleedingHandler.setBleedCooldown(gameManager.gameEntry.downDamageFrequency);
        return true;
    }
    protected static void initGameSetup(GameManager gameManager) {
        // 清除游戏效果
        EffectManager.get().forceEnd();
        gameManager.configPrepared = false;
    }
    protected static void startGameSetup(GameManager gameManager) {
        // gameManager.ready = false; // 不使用ready标记，因为Team会变动
        gameManager.gameTime = 0; // 游戏结束后不手动重置
        gameManager.winnerGameTeams.clear(); // 游戏结束后不手动重置
        gameManager.winnerGamePlayers.clear(); // 游戏结束后不手动重置
        GameStarter.registerGameEvent();
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(gameManager.globalCenterOffset));
        TempDataManager.get().startGame(gameManager.serverLevel); // 立即写入备份
        if (gameManager.gameEntry.healAllAtStart) {
            if (gameManager.serverLevel != null) {
                GameUtilsFunction.healGamePlayers(gameManager.serverLevel, GameTeamManager.getGamePlayers());
            } else {
                BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, failed to heal GamePlayers");
            }
        }
        GameStatsManager.recordGamerule(gameManager);
    }

    /**
     * 由于Team会变动，开始游戏使用isStartReady检查
     */
    protected static boolean isReady() {
        // return this.ready; // 不用ready标记，因为Team会变动
        return GameLootManager.get().isReady()
                && GameruleManager.get().isReady()
                && SpawnManager.get().isReady()
                // && TeamManager.get().isReady() // Team会变动
                && ZoneManager.get().isReady()
                && StatsManager.get().isReady();
    }
    /**
     * 开始游戏的检查
     */
    protected static boolean isStartReady() {
        return isReady() && TeamManager.get().isReady();
    }

    protected static void registerGameEvent() {
        DamageEventHandler.register();
        LoopEventHandler.register();
        PlayerDeathEventHandler.register();
        BleedingHandler.get().clear();
    }
    protected static void unregisterGameEvent() {
        DamageEventHandler.unregister();
        LoopEventHandler.unregister();
        PlayerDeathEventHandler.unregister();
        LogEventHandler.unregister();
        // ServerEventHandler.unregister(); // 不需要解除注册
        BleedingHandler.unregister();
    }
}
