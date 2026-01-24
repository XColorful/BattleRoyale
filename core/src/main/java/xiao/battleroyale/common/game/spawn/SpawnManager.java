package xiao.battleroyale.common.game.spawn;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.algorithm.CircleGridCalculator;
import xiao.battleroyale.algorithm.Distribution;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.ISpawnManager;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.JsonUtils;

import java.util.List;

import static xiao.battleroyale.api.data.TempDataTag.PRE_CALCULATE;
import static xiao.battleroyale.api.data.TempDataTag.SPAWN_MANAGER;

/**
 * 管理玩家出生方式、传送相关的Manager
 */
public class SpawnManager extends AbstractGameManager implements ISideOnly, ISpawnManager {

    private static class SpawnManagerHolder {
        private static final SpawnManager INSTANCE = new SpawnManager();
    }

    public static SpawnManager get() {
        return SpawnManagerHolder.INSTANCE;
    }

    protected SpawnManager() {}

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            BattleRoyale.LOGGER.debug("SpawnManager skipped init() at {}", mcSide.toString());
            return;
        }
        JsonObject jsonObject = TempDataManager.get().getJsonObject(PRE_CALCULATE, SPAWN_MANAGER);
        if (jsonObject == null) {
            return;
        }

        // 预计算
        // 渣机只算100要(约)27ms, 1-100要35ms, 1-1000要132ms (< 3 ticks)
        // Distribution.CircleGrid.preCalculate(1, 1000);
        int startN = JsonUtils.getJsonInt(jsonObject, "CircleGridStartN", 0);
        int endN = JsonUtils.getJsonInt(jsonObject, "CircleGridEndN", 0);
        List<Integer> nList = JsonUtils.getJsonIntList(jsonObject, "CircleGridNList");
        boolean showDebugResult = JsonUtils.getJsonBool(jsonObject, "showDebugResult", false);

        long startTime = System.nanoTime();
        Distribution.CircleGrid.get().preCalculate(startN, endN);
        Distribution.CircleGrid.get().preCalculate(nList);
        long endTime = System.nanoTime();
        BattleRoyale.LOGGER.debug("SpawnManager complete init, time:{}ms", (endTime - startTime) / 1_000_000.0);
        if (showDebugResult) {
            CircleGridCalculator.debugResult();
        }
    }

    @Override public String getManagerName() {
        return String.format("%s:SpawnManager", BattleRoyale.MOD_ID);
    }

    @Override public boolean serverSideOnly() {
        return true;
    }

    private IGameSpawner gameSpawner;
    @Override public IGameSpawner getGameSpawner() {
        return gameSpawner;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) {
            return;
        }

        int spawnConfigId = gameManager.getSpawnConfigId();
        SpawnConfig spawnConfig = (SpawnConfig) GameConfigManager.get().getConfigEntry(SpawnConfigManager.get().getNameKey(), spawnConfigId);
        if (spawnConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_spawn_config");
            return;
        }
        this.gameSpawner = spawnConfig.createGameSpawner();
        if (this.gameSpawner == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_spawn_config");
            return;
        }

        int gameId = gameManager.getGameruleConfigId();
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), gameId);
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }

        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("SpawnManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) {
            return;
        }
        if (!this.configPrepared) {
            return;
        }

        this.gameSpawner.clear();
        this.gameSpawner.init(gameManager.getRandom(), GameTeamManager.getPlayerLimit()); // 用玩家上限作为点位数量
        if (!isReady()) {
            return;
        }
        this.configPrepared = false;
        BattleRoyale.LOGGER.debug("SpawnManager complete initGame");
    }

    @Override
    public boolean isReady() {
        return this.gameSpawner.isReady();
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
            return false;
        }

        return isReady();
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.configPrepared = false;
        // this.ready = false; // isReady被重载
    }

    @Override
    public void onGameTick(int gameTime) {
        if (!gameSpawner.shouldTick()) {
            return;
        }

        gameSpawner.tick(gameTime, GameTeamManager.getGameTeams());
    }
}
