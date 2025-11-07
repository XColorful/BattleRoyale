package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.compat.playerrevive.BleedingHandler;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.event.handler.game.DamageEventHandler;
import xiao.battleroyale.event.handler.game.LogEventHandler;
import xiao.battleroyale.event.handler.game.LoopEventHandler;
import xiao.battleroyale.event.handler.game.PlayerDeathEventHandler;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import static xiao.battleroyale.api.data.io.TempDataTag.GAME_MANAGER;
import static xiao.battleroyale.api.data.io.TempDataTag.GLOBAL_OFFSET;

public class GameStarter {

    @ApiStatus.Internal
    public static boolean initGameConfigSetup(GameManager gameManager) {
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), gameManager.getGameruleConfigId());
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (gameruleConfig == null) {
            if (serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            } else {
                BattleRoyale.LOGGER.error("GameManager.serverLevel is null in initGameConfigSetup: gameruleConfig == null");
            }
            return false;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (brEntry == null || gameEntry == null) {
            if (serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
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
    @ApiStatus.Internal
    public static void initGameSetup(GameManager gameManager) {
        // 清除游戏效果
        EffectManager.get().forceEnd();
        gameManager.configPrepared = false;
    }
    @ApiStatus.Internal
    public static void startGameSetup(GameManager gameManager) {
        // gameManager.ready = false; // 不使用ready标记，因为Team会变动
        gameManager.setGameTime(0); // 游戏结束后不手动重置
        gameManager.clearWinnerGamePlayers(); // 游戏结束后不手动重置
        gameManager.clearWinnerGameTeams(); // 游戏结束后不手动重置
        GameStarter.registerGameEvent();
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(gameManager.globalCenterOffset));
        ServerLevel serverLevel = gameManager.getServerLevel();
        TempDataManager.get().startGame(serverLevel); // 立即写入备份
        if (gameManager.gameEntry.healAllAtStart) {
            if (serverLevel != null) {
                gameManager.getGameProcessManager().healGamePlayers(serverLevel, GameTeamManager.getGamePlayers());
            } else {
                BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, failed to heal GamePlayers");
            }
        }
        GameStatsManager.recordGamerule(gameManager);
    }

    /**
     * 由于Team会变动，开始游戏使用isStartReady检查
     */
    protected static boolean isReady(IGameManager gameManager) {
        // return this.ready; // 不用ready标记，因为Team会变动
        return gameManager.getGameLootManager().isReady()
                && gameManager.getGameruleManager().isReady()
                && gameManager.getSpawnManager().isReady()
                // && gameManager.getTeamManager().isReady() // Team会变动
                && gameManager.getZoneManager().isReady()
                && gameManager.getStatsManager().isReady();
    }
    /**
     * 开始游戏的检查
     */
    protected static boolean isStartReady(IGameManager gameManager) {
        return isReady(gameManager) && gameManager.getTeamManager().isReady();
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
