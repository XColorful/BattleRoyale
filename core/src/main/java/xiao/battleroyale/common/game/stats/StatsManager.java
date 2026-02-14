package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.event.game.finish.GameStopFinishEvent;
import xiao.battleroyale.api.event.game.game.*;
import xiao.battleroyale.api.event.game.starter.GameStartFinishEvent;
import xiao.battleroyale.api.event.game.tick.GameTickFinishEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.stats.IStatsManager;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.stats.StatsConfigHelper.DefaultObjectiveName;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.stats.StatsConfigManager;
import xiao.battleroyale.config.common.game.stats.StatsConfigManager.StatsConfig;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.ScoreUtils;
import xiao.battleroyale.util.StringUtils;

import java.nio.file.Paths;
import java.util.*;

import static xiao.battleroyale.data.AbstractDataManager.MOD_DATA_PATH;

public class StatsManager extends AbstractGameManager implements IStatsManager, ICustomEventHandler {

    private static class StatsManagerHolder {
        private static final StatsManager INSTANCE = new StatsManager();
    }

    public static StatsManager get() {
        return StatsManagerHolder.INSTANCE;
    }

    protected StatsManager() {}

    public static void init(McSide mcSide) {
        ;
    }

    @Override public String getManagerName() {
        return String.format("%s:StatsManager", BattleRoyale.MOD_ID);
    }

    public static final String STATS_SUB_PATH = "stats";
    public static final String STATS_PATH = Paths.get(MOD_DATA_PATH).resolve(STATS_SUB_PATH).toString();
    protected static final String STATS_TAG = "stats";
    protected static final String GAME_TAG = "game";
    protected static final String GAMERULE_TAG = "gamerule";
    protected static final String SPAWN_TAG = "spawn";
    protected static final String ZONE_TAG = "zone";
    protected static final String TIMELINE_TAG = "timeline";
    protected static final String RANK_TAG = "rank";
    protected static final String DETAIL_TAG = "detail";

    protected final StatsData statsData = new StatsData();

    protected int timeOrder = 0;
    protected int minRank = Integer.MAX_VALUE;
    protected int maxRank = Integer.MIN_VALUE;
    public static int DEFAULT_RANK = -1;
    protected String startotherTime = ""; // 系统时间
    protected int totalPlayers = 0;
    protected boolean recordStats = false;
    public boolean shouldRecordStats() { return recordStats; }
    public Set<GamePlayer> getRecordGamePlayers() { return this.statsData.getRecordGamePlayers(); }

    // 原版记分板
    protected boolean recordScoreboard = true;
    protected boolean resetScoreboardAtStart = false;
    protected float mcMaxHealth = 20; // 对应100%血
    protected float damageMultiplier = 5; // 伤害计分倍率，5就是20*5=100
    protected float ratioBase = 1000; // 对应100%
    protected boolean syncGameInfoToObjective = true;
    protected @NotNull String gameInfoObjectiveName = DefaultObjectiveName.GAME_INFO;
    protected @NotNull String playerTotalScoreName = DefaultObjectiveName.PLAYER_TOTAL;
    protected @NotNull String aliveScoreName = DefaultObjectiveName.ALIVE;
    protected @NotNull String gameTimeScoreName = DefaultObjectiveName.GAME_TIME;
    protected int scoreboardCycleInterval = 20 * 3; // 3秒轮换一次
    protected List<String> cycleObjectiveName = new ArrayList<>();

    // 原始数据
    protected @NotNull String player_to_player_damage_ObjectiveName = DefaultObjectiveName.PLAYER_TO_PLAYER_DAMAGE;
    protected @NotNull String other_to_player_damage_ObjectiveName = DefaultObjectiveName.OTHER_TO_PLAYER_DAMAGE;
    protected @NotNull String player_damage_by_player_ObjectiveName = DefaultObjectiveName.PLAYER_DAMAGE_BY_PLAYER;
    protected @NotNull String player_damage_by_other_ObjectiveName = DefaultObjectiveName.PLAYER_DAMAGE_BY_OTHER;

    protected @NotNull String player_knock_player_ObjectiveName = DefaultObjectiveName.PLAYER_KNOCK_PLAYER;
    protected @NotNull String other_knock_player_ObjectiveName = DefaultObjectiveName.OTHER_KNOCK_PLAYER;
    protected @NotNull String player_down_by_player_ObjectiveName = DefaultObjectiveName.PLAYER_DOWN_BY_PLAYER;
    protected @NotNull String player_down_by_other_ObjectiveName = DefaultObjectiveName.PLAYER_DOWN_BY_OTHER;

    protected @NotNull String player_revive_ObjectiveName = DefaultObjectiveName.PLAYER_REVIVE;

    protected @NotNull String player_kill_player_ObjectiveName = DefaultObjectiveName.PLAYER_KILL_PLAYER;
    protected @NotNull String other_kill_player_ObjectiveName = DefaultObjectiveName.OTHER_KILL_PLAYER;
    protected @NotNull String player_death_by_player_ObjectiveName = DefaultObjectiveName.PLAYER_DEATH_BY_PLAYER;
    protected @NotNull String player_death_by_other_ObjectiveName = DefaultObjectiveName.PLAYER_DEATH_BY_OTHER;

    protected @NotNull String player_win_ObjectiveName = DefaultObjectiveName.PLAYER_WIN;
    protected @NotNull String player_lose_ObjectiveName = DefaultObjectiveName.PLAYER_LOSE;

    // 二次计算
    protected @NotNull String player_attack_rate_ObjectiveName = DefaultObjectiveName.PLAYER_ATTACK_RATE;
    protected @NotNull String player_kd_ObjectiveName = DefaultObjectiveName.PLAYER_KD;
    protected @NotNull String player_game_total_ObjectiveName = DefaultObjectiveName.PLAYER_GAME_TOTAL;
    protected @NotNull String player_win_rate_ObjectiveName = DefaultObjectiveName.PLAYER_WIN_RATE;

    // 整活计算
    protected boolean enableJourneyStats = true;
    protected int journeyStatsDelay = 20 * 5; // 5秒后开始统计
    protected @NotNull String player_journey_ObjectiveName = DefaultObjectiveName.PLAYER_JOURNEY;

    protected boolean enableMaxSpeedStats = true;
    protected int maxSpeedStatsDelay = 20 * 5; // 5秒后开始统计
    protected @NotNull String player_max_speed_ObjectiveName = DefaultObjectiveName.PLAYER_MAX_SPEED;

    // 游戏结束后
    protected @NotNull String listObjectiveAfterGame = "";
    protected @NotNull String sidebarObjectiveAfterGame = "";

    @Override
    public boolean registerGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), CustomEventType.GAME_START_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_TICK_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_PLAYER_DAMAGE_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_PLAYER_REVIVE_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_PLAYER_DEATH_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_STOP_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_COMPLETE_FINISH_EVENT);
        return true;
    }
    @Override
    public boolean unregisterGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), CustomEventType.GAME_START_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_TICK_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_PLAYER_DAMAGE_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_PLAYER_REVIVE_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_PLAYER_DEATH_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_STOP_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_COMPLETE_FINISH_EVENT);
        return true;
    }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:StatsManager", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        switch (customEventType) {
            case GAME_START_FINISH_EVENT -> onRecordStart((GameStartFinishEvent) event);
            case GAME_TICK_FINISH_EVENT -> onRecordGameTick((GameTickFinishEvent) event);
            case GAME_PLAYER_DAMAGE_FINISH_EVENT -> onRecordPlayerDamage((GamePlayerDamageFinishEvent) event);
            case GAME_PLAYER_DOWN_FINISH_EVENT -> onRecordPlayerDown((GamePlayerDownFinishEvent) event);
            case GAME_PLAYER_REVIVE_FINISH_EVENT -> onRecordPlayerRevive((GamePlayerReviveFinishEvent) event);
            case GAME_PLAYER_DEATH_FINISH_EVENT -> onRecordPlayerDeath((GamePlayerDeathFinishEvent) event);
            case GAME_STOP_FINISH_EVENT -> onRecordStop((GameStopFinishEvent) event);
            case GAME_COMPLETE_FINISH_EVENT -> onRecordComplete((GameCompleteFinishEvent) event);
            default -> onReceiveWrongEvent(customEventType);
        }
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) return;

        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        IConfigSubManager<?> gameruleConfigManager = modConfigManager.getConfigSubManager(GameConfigManager.get().getNameKey(), GameruleConfigManager.get().getNameKey());
        if (gameruleConfigManager == null || !(gameruleConfigManager.getConfigEntry(gameManager.getGameruleConfigId()) instanceof GameruleConfig gameruleConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        this.recordStats = brEntry.recordGameStats;

        IConfigSubManager<?> statsConfigManager = modConfigManager.getConfigSubManager(GameConfigManager.get().getNameKey(), StatsConfigManager.get().getNameKey());
        if (statsConfigManager == null || !(statsConfigManager.getConfigEntry(gameManager.getStatsConfigId()) instanceof StatsConfig statsConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_stats_config");
            return;
        }

        if (!StatsConfigHelper.updateStats(this, statsConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_stats_config");
            return;
        }

        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("StatsManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        clearStats();

        this.ready = true;
        this.configPrepared = false;
        BattleRoyale.LOGGER.debug("StatsManager complete initGame");
    }
    private void clearStats() {
        this.statsData.clear();
        timeOrder = 0;
        minRank = Integer.MAX_VALUE;
        maxRank = Integer.MIN_VALUE;
    }
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        startotherTime = StringUtils.getTimestampString();
        totalPlayers = GameTeamManager.getGamePlayers().size();
        this.statsData.addRecordGamePlayers(GameTeamManager.getStandingGamePlayers());

        this.statsData.startGame(); // 上锁
        return isReady();
    }

    /**
     * Stats主要基于事件立即记录，因此逻辑不放onGameTick
     * 处理其余功能
     */
    @Override
    public void onGameTick(int gameTime) {

        // 轮流切换scoreboard
        if (scoreboardCycleInterval > 0) {
            if (cycleObjectiveName.isEmpty()) return;
            @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
            if (serverLevel == null) return;

            Scoreboard scoreboard = serverLevel.getScoreboard();
            int cycleIndex = (gameTime / scoreboardCycleInterval) % cycleObjectiveName.size();
            ScoreUtils.setSidebarObjective(scoreboard, cycleObjectiveName.get(cycleIndex));
        }
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.configPrepared = false;
        this.ready = false;
        if (shouldRecordStats()) {
            saveStats();
        }
        this.statsData.endGame(); // 解锁

        // 设置 sidebar 和 list 位置显示的记分板
        if (recordScoreboard) {
            if (serverLevel == null) return;

            Scoreboard scoreboard = serverLevel.getScoreboard();
            ScoreUtils.setListObjective(scoreboard, listObjectiveAfterGame);
            ScoreUtils.setSidebarObjective(scoreboard, sidebarObjectiveAfterGame);
        }
    }

    @Override public void onRecordStart(GameStartFinishEvent event) {
        StatsEventHandler.onGameStart(this, event);
    }

    @Override public void onRecordGameTick(GameTickFinishEvent event) {
        StatsEventHandler.onGameTick(this, event);
    }

    @Override public void onRecordPlayerDamage(GamePlayerDamageFinishEvent event) {
        StatsEventHandler.onGamePlayerDamage(this, event);
    }

    @Override public void onRecordPlayerDown(GamePlayerDownFinishEvent event) {
        StatsEventHandler.onGamePlayerDown(this, event);
    }

    @Override public void onRecordPlayerRevive(GamePlayerReviveFinishEvent event) {
        StatsEventHandler.onGamePlayerRevive(this, event);
    }

    @Override public void onRecordPlayerDeath(GamePlayerDeathFinishEvent event) {
        StatsEventHandler.onGamePlayerDeath(this, event);
    }

    @Override public void onRecordStop(GameStopFinishEvent event) {
        StatsEventHandler.onGameStop(this, event);
    }

    @Override public void onRecordComplete(GameCompleteFinishEvent event) {
        StatsEventHandler.onGameComplete(this, event);
    }

    // ----Gamerule----
    public void onRecordIntGamerule(Map<String, Integer> intGamerule) {
        GameSetupStatsHelper.onRecordIntGamerule(this, intGamerule);
    }
    public void onRecordBoolGamerule(Map<String, Boolean> boolGamerule) {
        GameSetupStatsHelper.onRecordBoolGamerule(this, boolGamerule);
    }
    public void onRecordDoubleGamerule(Map<String, Double> doubleGamerule) {
        GameSetupStatsHelper.onRecordDoubleGamerule(this, doubleGamerule);
    }
    public void onRecordStringGamerule(Map<String, String> stringGamerule) {
        GameSetupStatsHelper.onRecordStringGamerule(this, stringGamerule);
    }
    // ----Spawn----
    public void onRecordSpawnInt(String key, Map<String, Integer> spawnInt) {
        GameSetupStatsHelper.onRecordSpawnInt(this, key, spawnInt);
    }
    public void onRecordSpawnBool(String key, Map<String, Boolean> spawnBool) {
        GameSetupStatsHelper.onRecordSpawnBool(this, key, spawnBool);
    }
    public void onRecordSpawnDouble(String key, Map<String, Double> spawnDouble) {
        GameSetupStatsHelper.onRecordSpawnDouble(this, key, spawnDouble);
    }
    public void onRecordSpawnString(String key, Map<String, String> spawnString) {
        GameSetupStatsHelper.onRecordSpawnString(this, key, spawnString);
    }
    // ----Zone----
    public void onRecordZoneInt(int zoneId, Map<String, Integer> zoneInt) {
        GameSetupStatsHelper.onRecordZoneInt(this, zoneId, zoneInt);
    }
    public void onRecordZoneBool(int zoneId, Map<String, Boolean> zoneBool) {
        GameSetupStatsHelper.onRecordZoneBool(this, zoneId, zoneBool);
    }
    public void onRecordZoneDouble(int zoneId, Map<String, Double> zoneDouble) {
        GameSetupStatsHelper.onRecordZoneDouble(this, zoneId, zoneDouble);
    }
    public void onRecordZoneString(int zoneId, Map<String, String> zoneString) {
        GameSetupStatsHelper.onRecordZoneString(this, zoneId, zoneString);
    }

    private String generateStateDirectory() {
        String fileName = startotherTime + "_" + totalPlayers + ".json";
        return Paths.get(STATS_PATH, fileName).toString();
    }
    public String getStatsFilePath() {
        return generateStateDirectory();
    }

    /**
     * 将数据写入json
     */
    public void saveStats(String filePath) {
        // 按先排名，后游戏玩家id排序
        List<GamePlayerStats> gamePlayerStatsList = this.statsData.getRecordGamePlayerStats();
        gamePlayerStatsList.sort(Comparator
                .comparingInt(GamePlayerStats::getGameRank)
                .thenComparingInt(s -> s.getGamePlayer().getGameSingleId())
        );

        JsonArray jsonArray = new JsonArray();
        GameSetupStatsHelper.addGameSetupStats(this, jsonArray);
        GameEventStatsHelper.addTimelineStats(this, jsonArray);
        GameEventStatsHelper.addRankStats(this, jsonArray);
        GameEventStatsHelper.addDetailStats(this, jsonArray);
        JsonUtils.writeJsonToFile(filePath, jsonArray);

        ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.saved_game_stats");
        } else {
            BattleRoyale.LOGGER.warn("GameManager doesn't have valid serverLevel, can't send message");
        }
        BattleRoyale.LOGGER.info("Saved game stats to {}", filePath);
    }

    protected GamePlayerStats getGamePlayerStats(GamePlayer gamePlayer) {
        return this.statsData.getGamePlayerStats(gamePlayer);
    }

    /**
     * 提供查询玩家Stats的接口
     */
    public void getGamePlayerStats(int playerId) {
        ;
    }
    public void getGamePlayerStats(UUID playerUUID) {
        ;
    }
    public void getGamePlayerStats(String playerName) {
        ;
    }
    public void getGameTeamStats(int teamId) {
        ;
    }

    /**
     * 提供查询其他统计数据的接口
     */
    public void getGameruleStats(String gameruleName) {
        ;
    }
}
