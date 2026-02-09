package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.event.game.finish.GameStopFinishEvent;
import xiao.battleroyale.api.event.game.game.*;
import xiao.battleroyale.api.event.game.starter.GameStartFinishEvent;
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
    protected String objectName_prefix = BattleRoyale.MOD_NAME_SHORT;
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

    @Override
    public boolean registerGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), CustomEventType.GAME_START_FINISH_EVENT);
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
            case GAME_START_FINISH_EVENT -> {
                StatsEventHandler.onGameStart(this, (GameStartFinishEvent) event);
            }
            case GAME_PLAYER_DAMAGE_FINISH_EVENT -> {
                StatsEventHandler.onGamePlayerDamage(this, (GamePlayerDamageFinishEvent) event);
            }
            case GAME_PLAYER_DOWN_FINISH_EVENT -> {
                StatsEventHandler.onGamePlayerDown(this, (GamePlayerDownFinishEvent) event);
            }
            case GAME_PLAYER_REVIVE_FINISH_EVENT -> {
                StatsEventHandler.onGamePlayerRevive(this, (GamePlayerReviveFinishEvent) event);
            }
            case GAME_PLAYER_DEATH_FINISH_EVENT -> {
                StatsEventHandler.onGamePlayerDeath(this, (GamePlayerDeathFinishEvent) event);
            }
            case GAME_STOP_FINISH_EVENT -> {
                StatsEventHandler.onGameStop(this, (GameStopFinishEvent) event);
            }
            case GAME_COMPLETE_FINISH_EVENT -> {
                StatsEventHandler.onGameComplete(this, (GameCompleteFinishEvent) event);
            }
            default -> {
                onReceiveWrongEvent(customEventType);
            }
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
     * 主要基于事件立即记录，因此逻辑不放onGameTick
     */
    @Override
    public void onGameTick(int gameTime) {
        ;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.configPrepared = false;
        this.ready = false;
        if (shouldRecordStats()) {
            saveStats();
        }
    }

    private void clearStats() {
        gamePlayerStats.clear();
        damageSourceStats.clear();
        this.statsData.clear();
        timeOrder = 0;
        minRank = Integer.MAX_VALUE;
        maxRank = Integer.MIN_VALUE;
    }

    /**
     * 记录攻击方玩家造成伤害量
     * 记录被攻击方承受伤害量
     * @param event 实体受到伤害事件
     */
    public void onRecordDamage(@NotNull GamePlayer damagedGamePlayer, ILivingDamageEvent event) {
        if (!gamePlayerStats.containsKey(damagedGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", damagedGamePlayer.getPlayerName(), damagedGamePlayer.getPlayerUUID());
            return;
        }


        DamageSource damageSource = event.getSource();
        float damageAmount = event.getDamageAmount();
        onRecordDamage(damagedGamePlayer, damageSource, damageAmount);
    }

    /**
     * 记录非玩家伤害来源造成伤害量
     * 记录被攻击方被承受伤害量
     */
    public void onRecordDamage(GamePlayer damagedGamePlayer, DamageSource damageSource, float damageAmount) {
        if (!gamePlayerStats.containsKey(damagedGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", damagedGamePlayer.getPlayerName(), damagedGamePlayer.getPlayerUUID());
            return;
        }

        if (damageSource.getEntity() instanceof LivingEntity attackingEntity) {
            GamePlayer attackingGamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(attackingEntity.getUUID());
            if (attackingGamePlayer != null) {
                return;
            }
        }

        // 非游戏玩家伤害
        if (!damageSourceStats.containsKey(damageSource)) {
            damageSourceStats.put(damageSource, new DamageSourceStats(damageSource));
        }
    }

    /**
     * 立即复活（击倒失败）视为 被击倒1次 + 立即自救1次
     */
    public void onRecordInstantRevive(@NotNull GamePlayer reviveGamePlayer, ILivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(reviveGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", reviveGamePlayer.getPlayerName(), reviveGamePlayer.getPlayerUUID());
            return;
        }
    }

    public void onRecordRevive(@NotNull GamePlayer reviveGamePlayer, ILivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(reviveGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", reviveGamePlayer.getPlayerName(), reviveGamePlayer.getPlayerUUID());
            return;
        }
    }

    public void onRecordDown(@NotNull GamePlayer downGamePlayer, ILivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(downGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", downGamePlayer.getPlayerName(), downGamePlayer.getPlayerUUID());
            return;
        }

    }

    public void onRecordKill(@NotNull GamePlayer downGamePlayer, ILivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(downGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", downGamePlayer.getPlayerName(), downGamePlayer.getPlayerUUID());
            return;
        }

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
