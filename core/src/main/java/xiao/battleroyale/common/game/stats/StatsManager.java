package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.event.game.finish.GameStopFinishEvent;
import xiao.battleroyale.api.event.game.game.*;
import xiao.battleroyale.api.event.game.starter.GameStartFinishEvent;
import xiao.battleroyale.api.event.game.tick.GameTickFinishEvent;
import xiao.battleroyale.api.game.stats.IStatsManager;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
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

    // player
    protected final Map<GamePlayer, GamePlayerStats> gamePlayerStats = new HashMap<>();
    protected final Map<DamageSource, DamageSourceStats> damageSourceStats = new HashMap<>();

    protected int timeOrder = 0;
    protected int minRank = Integer.MAX_VALUE;
    protected int maxRank = Integer.MIN_VALUE;
    public static int DEFAULT_RANK = -1;
    protected String startotherTime = ""; // 系统时间
    protected int totalPlayers = 0;

    protected boolean recordStats = false;
    public boolean shouldRecordStats() { return recordStats; }
    // 原版计分板
    protected boolean recordScordboard = true;
    protected boolean resetScordboardAtStart = false;
    protected float mcMaxHealth = 20; // 对应100%血
    protected float damageMultiplier = 5; // 伤害计分倍率，5就是20*5=100
    protected float ratioBase = 1000; // 对应100%
    protected int scoreboardCycleInterval = 20 * 3; // 3秒轮换一次
    protected List<String> cycleObjectiveName = new ArrayList<>();
    protected String listObjectiveAfterGame = "";
    protected String sidebarObjectiveAfterGame = "";
    protected String objectName_prefix = BattleRoyale.MOD_NAME_SHORT;
    // 原始数据
    protected String player_to_player_damage_ObjectiveName = String.format("%s_hurt", objectName_prefix);
    protected String other_to_player_damage_ObjectiveName = String.format("%s_otherHurt", objectName_prefix);
    protected String player_damage_by_player_ObjectiveName = String.format("%s_damage", objectName_prefix);
    protected String player_damage_by_other_ObjectiveName = String.format("%s_otherDamage", objectName_prefix);
    protected String player_knock_player_ObjectiveName = String.format("%s_knock", objectName_prefix);
    protected String other_knock_player_ObjectiveName = String.format("%s_otherKnock", objectName_prefix);
    protected String player_down_by_player_ObjectiveName = String.format("%s_down", objectName_prefix);
    protected String player_down_by_other_ObjectiveName = String.format("%s_otherDown", objectName_prefix);
    protected String player_revive_ObjectiveName = String.format("%s_revive", objectName_prefix);
    protected String player_kill_player_ObjectiveName = String.format("%s_kill", objectName_prefix);
    protected String other_kill_player_ObjectiveName = String.format("%s_otherKill", objectName_prefix);
    protected String player_death_by_player_ObjectiveName = String.format("%s_death", objectName_prefix);
    protected String player_death_by_other_ObjectiveName = String.format("%s_otherDeath", objectName_prefix);
    protected String player_win_ObjectiveName = String.format("%s_win", objectName_prefix);
    protected String player_lose_ObjectiveName = String.format("%s_lose", objectName_prefix);
    // 二次计算
    protected String player_attack_rate_ObjectiveName = String.format("%s_attackRate", objectName_prefix);
    protected String player_kd_ObjectiveName = String.format("%s_kd", objectName_prefix);
    protected String player_win_rate_ObjectiveName = String.format("%s_winRate", objectName_prefix);
    // 整活计算
    protected boolean enableJourneyStats = true;
    protected int journeyStatsDelay = 20 * 5; // 5秒后开始统计
    protected String player_journey_ObjectiveName = String.format("%s_journey", objectName_prefix);
    protected boolean enableMaxSpeedStats = true;
    protected int maxSpeedStatsDelay = 20 * 5; // 5秒后开始统计
    protected String player_max_speed_ObjectiveName = String.format("%s_maxSpeed", objectName_prefix);

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
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), BattleRoyale.getGameManager().getGameruleConfigId());
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        this.recordStats = brEntry.recordGameStats;

        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("StatsManager complete initGameConfig");


        if (cycleObjectiveName.isEmpty()) { // TODO 测试用，后续改为自定义配置
            cycleObjectiveName.add(player_attack_rate_ObjectiveName);
            cycleObjectiveName.add(player_kd_ObjectiveName);
            cycleObjectiveName.add(player_win_rate_ObjectiveName);
        }
        listObjectiveAfterGame = player_win_rate_ObjectiveName;
        sidebarObjectiveAfterGame = player_journey_ObjectiveName;

        // TODO 测试用
        journeyStatsDelay = 20 * 30;
        maxSpeedStatsDelay = 20 * 30;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        clearStats();

        this.ready = true;
        this.configPrepared = false;
        BattleRoyale.LOGGER.debug("StatsManager complete initGame");
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        startotherTime = StringUtils.getTimestampString();
        totalPlayers = GameTeamManager.getGamePlayers().size();
        for (GamePlayer gamePlayer : GameTeamManager.getStandingGamePlayers()) {
            gamePlayerStats.put(gamePlayer, new GamePlayerStats(gamePlayer));
        }
        return isReady();
    }

    /**
     * Stats主要基于事件立即记录，因此逻辑不放onGameTick
     * 处理其余功能
     */
    @Override
    public void onGameTick(int gameTime) {

        if (!recordScordboard) return;

        // 轮流切换scoreboard
        if (cycleObjectiveName.isEmpty()) return;
        @Nullable ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel == null) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        int cycleIndex = (gameTime / scoreboardCycleInterval) % cycleObjectiveName.size();
        ScoreUtils.setSidebarObjective(scoreboard, cycleObjectiveName.get(cycleIndex));
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.configPrepared = false;
        this.ready = false;
        if (shouldRecordStats()) {
            saveStats();
        }

        if (!recordScordboard) return;
        if (serverLevel == null) return;

        Scoreboard scoreboard = serverLevel.getScoreboard();
        ScoreUtils.setListObjective(scoreboard, listObjectiveAfterGame);
        ScoreUtils.setSidebarObjective(scoreboard, sidebarObjectiveAfterGame);
    }

    private void clearStats() {
        gamePlayerStats.clear();
        damageSourceStats.clear();
        this.statsData.clear();
        timeOrder = 0;
        minRank = Integer.MAX_VALUE;
        maxRank = Integer.MIN_VALUE;
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
        List<GamePlayerStats> gamePlayerStatsList = new ArrayList<>(gamePlayerStats.values());
        gamePlayerStatsList.sort(Comparator
                .comparingInt(GamePlayerStats::getGameRank)
                .thenComparingInt(s -> s.gameSingleId)
        );

        JsonArray jsonArray = new JsonArray();
        GameSetupStatsHelper.addGameSetupStats(this, jsonArray);
        JsonUtils.writeJsonToFile(filePath, jsonArray);

        ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (serverLevel != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.saved_game_stats");
        } else {
            BattleRoyale.LOGGER.warn("GameManager doesn't have valid serverLevel, can't send message");
        }
        BattleRoyale.LOGGER.info("Saved game stats to {}", filePath);
    }

    private void addTimelineStats(@NotNull JsonArray jsonArray) {
        ;
    }

    private void addRankStats(@NotNull JsonArray jsonArray) {
        ;
    }

    private void addDetailStats(@NotNull JsonArray jsonArray) {
        ;
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
