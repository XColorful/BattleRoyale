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
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import static xiao.battleroyale.api.data.TempDataTag.GAME_MANAGER;
import static xiao.battleroyale.api.data.TempDataTag.GLOBAL_OFFSET;

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
        gameManager.setGameEntry(gameEntry);
        gameEntry = gameManager.getGameEntry();
        BleedingHandler.setBleedDamage(gameEntry.downDamageList);
        BleedingHandler.setBleedCooldown(gameEntry.downDamageFrequency);
        return true;
    }
    @ApiStatus.Internal
    public static void initGameConfigSubManager(IGameManager gameManager, ServerLevel serverLevel) {
        // TODO 目前还是各IGameSubManager各拿各的，没加入IGameManager统一持有的@NotNull配置
        gameManager.getGameProcessManager().initGameConfig(serverLevel);
        gameManager.getGameLootManager().initGameConfig(serverLevel);
        gameManager.getGameruleManager().initGameConfig(serverLevel);
        gameManager.getSpawnManager().initGameConfig(serverLevel);
        gameManager.getGameLobbyManager().initGameConfig(serverLevel);
        gameManager.getTeamManager().initGameConfig(serverLevel);
        gameManager.getZoneManager().initGameConfig(serverLevel);
        gameManager.getStatsManager().initGameConfig(serverLevel);
    }
    @ApiStatus.Internal
    public static boolean gameConfigAllReady(IGameManager gameManager) {
        if (!gameManager.getGameLootManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("GameLootManager isn't config prepared");
            return false;
        } else if (!gameManager.getGameruleManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("GameruleManager isn't config prepared");
            return false;
        } else if (!gameManager.getSpawnManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("SpawnManager isn't config prepared");
            return false;
        } else if (!gameManager.getGameLobbyManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("GameLobbyManager isn't config prepared");
            return false;
        } else if (!gameManager.getTeamManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("TeamManager isn't config prepared");
            return false;
        } else if (!gameManager.getZoneManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("ZoneManager isn't config prepared");
            return false;
        } else if (!gameManager.getStatsManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("StatsManager isn't config prepared");
            return false;
        } else if (!gameManager.getGameProcessManager().isConfigPrepared()) {
            BattleRoyale.LOGGER.debug("GameProcessManager isn't config prepared");
            return false;
        }
        return true;
    }

    @ApiStatus.Internal
    public static void initGameSetup(GameManager gameManager) {
        // 清除游戏效果
        EffectManager.get().forceEnd();
        gameManager.configPrepared = false;
    }
    @ApiStatus.Internal
    public static void initGameSubManager(IGameManager gameManager, ServerLevel serverLevel) {
        gameManager.getStatsManager().initGame(serverLevel); // 先清空stats
        gameManager.getGameLootManager().initGame(serverLevel);
        gameManager.getTeamManager().initGame(serverLevel); // TeamManager先处理组队
        gameManager.getGameruleManager().initGame(serverLevel); // Gamerule记录游戏模式
        gameManager.getSpawnManager().initGame(serverLevel);
        gameManager.getGameLobbyManager().initGame(serverLevel); // GameLobbyManager会传送至大厅并更改游戏模式
        gameManager.getZoneManager().initGame(serverLevel);
        gameManager.getGameProcessManager().initGame(serverLevel);
    }

    @ApiStatus.Internal
    public static void startGameSetup(GameManager gameManager) {
        // gameManager.ready = false; // 不使用ready标记，因为Team会变动
        gameManager.setGameTime(0); // 游戏结束后不手动重置
        gameManager.clearWinnerGamePlayers(); // 游戏结束后不手动重置
        gameManager.clearWinnerGameTeams(); // 游戏结束后不手动重置
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(gameManager.globalCenterOffset));
        ServerLevel serverLevel = gameManager.getServerLevel();
        TempDataManager.get().startGame(serverLevel); // 立即写入备份
        if (gameManager.getGameEntry().healAllAtStart) {
            if (serverLevel != null) {
                gameManager.getGameProcessManager().healGamePlayers(serverLevel, GameTeamManager.getGamePlayers());
            } else {
                BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, failed to heal GamePlayers");
            }
        }
        GameStatsManager.recordGamerule(gameManager);
    }
    @ApiStatus.Internal
    public static boolean startGameSubManager(IGameManager gameManager, ServerLevel serverLevel) {
        if (!gameManager.getGameLootManager().startGame(serverLevel)) { // 判定的优先级最高
            BattleRoyale.LOGGER.warn("GameLootManager failed to start game");
            return false;
        } else if (!gameManager.getTeamManager().startGame(serverLevel)) { // 先执行 TeamManager 得到 StandingGamePlayers，并确保无队伍玩家均被清理
            BattleRoyale.LOGGER.warn("TeamManager failed to start game");
            return false;
        } else if (!gameManager.getGameruleManager().startGame(serverLevel)) { // 依赖 TeamManager 的 StandingGamePlayers
            BattleRoyale.LOGGER.warn("GameruleManager failed to start game");
            return false;
        } else if (!gameManager.getZoneManager().startGame(serverLevel)) { // 有圈则行
            BattleRoyale.LOGGER.warn("ZoneManager failed to start game");
            return false;
        } else if (!gameManager.getSpawnManager().startGame(serverLevel)) { // SpawnManager在onGameTick处理出生，提前处理过就行
            BattleRoyale.LOGGER.warn("SpawnManager failed to start game");
            return false;
        } else if (!gameManager.getGameLobbyManager().startGame(serverLevel)) {
            BattleRoyale.LOGGER.warn("GameLobbyManager failed to start game");
            return false;
        } else if (!gameManager.getStatsManager().startGame(serverLevel)) {
            BattleRoyale.LOGGER.warn("StatsManager failed to start game");
            return false;
        } else if (!gameManager.getGameProcessManager().startGame(serverLevel)) {
            BattleRoyale.LOGGER.warn("GameProcessManager failed to start game");
            return false;
        }
        return true;
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
}
