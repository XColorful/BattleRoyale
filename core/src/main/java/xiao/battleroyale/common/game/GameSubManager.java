package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;

public class GameSubManager {

    protected static void initGameConfigSubManager(IGameManager gameManager, ServerLevel serverLevel) {
        gameManager.getGameLootManager().initGameConfig(serverLevel);
        gameManager.getGameruleManager().initGameConfig(serverLevel);
        gameManager.getSpawnManager().initGameConfig(serverLevel);
        gameManager.getGameLobbyManager().initGame(serverLevel);
        gameManager.getTeamManager().initGameConfig(serverLevel);
        gameManager.getZoneManager().initGameConfig(serverLevel);
        gameManager.getStatsManager().initGameConfig(serverLevel);
    }

    protected static boolean gameConfigAllReady(IGameManager gameManager) {
        return (gameManager.getGameLootManager().isPreparedForGame() // 判定的优先级最低
                && gameManager.getGameruleManager().isPreparedForGame()
                && gameManager.getSpawnManager().isPreparedForGame()
                && gameManager.getGameLobbyManager().isPreparedForGame()
                && gameManager.getTeamManager().isPreparedForGame()
                && gameManager.getZoneManager().isPreparedForGame()
                && gameManager.getStatsManager().isPreparedForGame());
    }

    protected static void initGameSubManager(IGameManager gameManager, ServerLevel serverLevel) {
        gameManager.getStatsManager().initGame(serverLevel); // 先清空stats
        gameManager.getGameLootManager().initGame(serverLevel);
        gameManager.getTeamManager().initGame(serverLevel); // TeamManager先处理组队
        gameManager.getGameruleManager().initGame(serverLevel); // Gamerule记录游戏模式
        gameManager.getSpawnManager().initGame(serverLevel);
        gameManager.getGameLobbyManager().initGame(serverLevel); // GameLobbyManager会传送至大厅并更改游戏模式
        gameManager.getZoneManager().initGame(serverLevel);
    }
    protected static boolean startGameSubManager(IGameManager gameManager, ServerLevel serverLevel) {
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
        }
        return true;
    }
}