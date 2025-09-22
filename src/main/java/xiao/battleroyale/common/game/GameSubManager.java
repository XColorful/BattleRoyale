package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;

import java.util.HashMap;
import java.util.Map;

public class GameSubManager {

    private static final GameLootManager gameLootManagerInstance = GameLootManager.get();
    private static final GameruleManager gameruleManagerInstance = GameruleManager.get();
    private static final SpawnManager spawnManagerInstance = SpawnManager.get();
    private static final TeamManager teamManagerInstance = TeamManager.get();
    private static final ZoneManager zoneManagerInstance = ZoneManager.get();
    private static final StatsManager statsManagerInstance = StatsManager.get();

    protected static void initGameConfigSubManager(ServerLevel serverLevel) {
        gameLootManagerInstance.initGameConfig(serverLevel);
        gameruleManagerInstance.initGameConfig(serverLevel);
        spawnManagerInstance.initGameConfig(serverLevel);
        teamManagerInstance.initGameConfig(serverLevel);
        zoneManagerInstance.initGameConfig(serverLevel);
        statsManagerInstance.initGameConfig(serverLevel);
    }

    protected static boolean gameConfigAllReady() {
        return (gameLootManagerInstance.isPreparedForGame() // 判定的优先级最低
                && gameruleManagerInstance.isPreparedForGame()
                && spawnManagerInstance.isPreparedForGame()
                && teamManagerInstance.isPreparedForGame()
                && zoneManagerInstance.isPreparedForGame()
                && statsManagerInstance.isPreparedForGame());
    }

    protected static void initGameSubManager(ServerLevel serverLevel) {
        statsManagerInstance.initGame(serverLevel); // 先清空stats
        gameLootManagerInstance.initGame(serverLevel);
        teamManagerInstance.initGame(serverLevel); // TeamManager先处理组队
        gameruleManagerInstance.initGame(serverLevel); // Gamerule记录游戏模式
        spawnManagerInstance.initGame(serverLevel); // SpawnManager会传送至大厅并更改游戏模式
        zoneManagerInstance.initGame(serverLevel);
    }
    protected static boolean startGameSubManager(ServerLevel serverLevel) {
        if (!gameLootManagerInstance.startGame(serverLevel)) { // 判定的优先级最高
            BattleRoyale.LOGGER.warn("GameLootManager failed to start game");
            return false;
        } else if (!teamManagerInstance.startGame(serverLevel)) { // 先执行 TeamManager 得到 StandingGamePlayers，并确保无队伍玩家均被清理
            BattleRoyale.LOGGER.warn("TeamManager failed to start game");
            return false;
        } else if (!gameruleManagerInstance.startGame(serverLevel)) { // 依赖 TeamManager 的 StandingGamePlayers
            BattleRoyale.LOGGER.warn("GameruleManager failed to start game");
            return false;
        } else if (!zoneManagerInstance.startGame(serverLevel)) { // 有圈则行
            BattleRoyale.LOGGER.warn("ZoneManager failed to start game");
            return false;
        } else if (!spawnManagerInstance.startGame(serverLevel)) { // SpawnManager在onGameTick处理出生，提前处理过就行
            BattleRoyale.LOGGER.warn("SpawnManager failed to start game");
            return false;
        } else if (!statsManagerInstance.startGame(serverLevel)) {
            BattleRoyale.LOGGER.warn("StatsManager failed to start game");
            return false;
        }
        return true;
    }
}