package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.stats.game.SimpleRecord;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.event.game.StatsEventHandler;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static xiao.battleroyale.data.AbstractDataManager.MOD_DATA_PATH;

public class StatsManager extends AbstractGameManager {

    private static class StatsManagerHolder {
        private static final StatsManager INSTANCE = new StatsManager();
    }

    public static StatsManager get() {
        return StatsManagerHolder.INSTANCE;
    }

    private StatsManager() {}

    public static void init(Dist dist) {
        ;
    }

    public static final String STATS_SUB_PATH = "stats";
    public static final String STATS_PATH = Paths.get(MOD_DATA_PATH).resolve(STATS_SUB_PATH).toString();
    private static final String STATS_TAG = "stats";
    private static final String GAME_TAG = "game";
    private static final String GAMERULE_TAG = "gamerule";
    private static final String SPAWN_TAG = "spawn";
    private static final String ZONE_TAG = "zone";
    private static final String TIMELINE_TAG = "timeline";
    private static final String RANK_TAG = "rank";
    private static final String DETAIL_TAG = "detail";

    // player
    private final Map<GamePlayer, GamePlayerStats> gamePlayerStats = new HashMap<>();
    private final Map<DamageSource, DamageSourceStats> damageSourceStats = new HashMap<>();
    // game
    private final SimpleRecord gameruleStats = new SimpleRecord();
    private final Map<String, SimpleRecord> spawnStats = new TreeMap<>(); // key/singleId -> spawnRecord
    private final Map<Integer, SimpleRecord> zoneStats = new HashMap<>(); // zoneId -> ZoneRecord

    private int timeOrder = 0;
    private int minRank = Integer.MAX_VALUE;
    private int maxRank = Integer.MIN_VALUE;
    public static int DEFAULT_RANK = -1;
    private String startSystemTime = ""; // 系统时间
    private int totalPlayers = 0;

    private boolean recordStats = false;
    public boolean shouldRecordStats() { return recordStats; }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), GameManager.get().getGameruleConfigId());
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        recordStats = brEntry.recordGameStats;

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
        StatsEventHandler.register();
        startSystemTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
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

        StatsEventHandler.unregister();
    }

    private void clearStats() {
        gamePlayerStats.clear();
        damageSourceStats.clear();
        zoneStats.clear();
        spawnStats.clear();
        gameruleStats.clear();
        timeOrder = 0;
        minRank = Integer.MAX_VALUE;
        maxRank = Integer.MIN_VALUE;
    }

    /**
     * 记录攻击方玩家造成伤害量
     * 记录被攻击方承受伤害量
     * @param event 实体受到伤害事件
     */
    public void onRecordDamage(@NotNull GamePlayer damagedGamePlayer, LivingDamageEvent event) {
        if (!gamePlayerStats.containsKey(damagedGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", damagedGamePlayer.getPlayerName(), damagedGamePlayer.getPlayerUUID());
            return;
        }


        DamageSource damageSource = event.getSource();
        float damageAmount = event.getAmount();
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
            GamePlayer attackingGamePlayer = TeamManager.get().getGamePlayerByUUID(attackingEntity.getUUID());
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
    public void onRecordInstantRevive(@NotNull GamePlayer reviveGamePlayer, LivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(reviveGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", reviveGamePlayer.getPlayerName(), reviveGamePlayer.getPlayerUUID());
            return;
        }
    }

    public void onRecordRevive(@NotNull GamePlayer reviveGamePlayer, LivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(reviveGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", reviveGamePlayer.getPlayerName(), reviveGamePlayer.getPlayerUUID());
            return;
        }
    }

    public void onRecordDown(@NotNull GamePlayer downGamePlayer, LivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(downGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", downGamePlayer.getPlayerName(), downGamePlayer.getPlayerUUID());
            return;
        }

    }

    public void onRecordKill(@NotNull GamePlayer downGamePlayer, LivingDeathEvent event) {
        if (!gamePlayerStats.containsKey(downGamePlayer)) {
            BattleRoyale.LOGGER.warn("Reject to add new game player stats for {} (UUID: {})", downGamePlayer.getPlayerName(), downGamePlayer.getPlayerUUID());
            return;
        }

    }

    /**
     * Gamerule
     */
    public void onRecordIntGamerule(Map<String, Integer> intGamerule) {
        updateRecordMap(gameruleStats.intRecord, intGamerule);
    }
    public void onRecordBoolGamerule(Map<String, Boolean> boolGamerule) {
        updateBoolRecordMap(gameruleStats.boolRecord, boolGamerule);
    }
    public void onRecordDoubleGamerule(Map<String, Double> doubleGamerule) {
        updateRecordMap(gameruleStats.doubleRecord, doubleGamerule);
    }
    public void onRecordStringGamerule(Map<String, String> stringGamerule) {
        updateRecordMap(gameruleStats.stringRecord, stringGamerule);
    }

    /**
     * Spawn
     */
    private SimpleRecord getOrCreateSpawnRecord(String key) {
        return spawnStats.computeIfAbsent(key, k -> new SimpleRecord());
    }
    public void onRecordSpawnInt(String key, Map<String, Integer> spawnInt) {
        if (spawnInt != null) {
            SimpleRecord record = getOrCreateSpawnRecord(key);
            updateRecordMap(record.intRecord, spawnInt);
        } else {
            spawnStats.remove(key);
        }
    }
    public void onRecordSpawnBool(String key, Map<String, Boolean> spawnBool) {
        if (spawnBool != null) {
            SimpleRecord record = getOrCreateSpawnRecord(key);
            updateRecordMap(record.boolRecord, spawnBool);
        } else {
            spawnStats.remove(key);
        }
    }
    public void onRecordSpawnDouble(String key, Map<String, Double> spawnDouble) {
        if (spawnDouble != null) {
            SimpleRecord record = getOrCreateSpawnRecord(key);
            updateRecordMap(record.doubleRecord, spawnDouble);
        } else {
            spawnStats.remove(key);
        }
    }
    public void onRecordSpawnString(String key, Map<String, String> spawnString) {
        if (spawnString != null) {
            SimpleRecord record = getOrCreateSpawnRecord(key);
            updateRecordMap(record.stringRecord, spawnString);
        } else {
            spawnStats.remove(key);
        }
    }

    /**
     * Zone
     */
    private SimpleRecord getOrCreateZoneRecord(Integer key) {
        return zoneStats.computeIfAbsent(key, k -> new SimpleRecord());
    }
    public void onRecordZoneInt(int zoneId, Map<String, Integer> zoneIntWriter) {
        if (zoneIntWriter != null) {
            SimpleRecord record = getOrCreateZoneRecord(zoneId);
            updateRecordMap(record.intRecord, zoneIntWriter);
        } else {
            zoneStats.remove(zoneId);
        }
    }
    public void onRecordZoneBool(int zoneId, Map<String, Boolean> zoneBool) {
        if (zoneBool != null) {
            SimpleRecord record = getOrCreateZoneRecord(zoneId);
            updateBoolRecordMap(record.boolRecord, zoneBool);
        } else {
            zoneStats.remove(zoneId);
        }
    }
    public void onRecordZoneDouble(int zoneId, Map<String, Double> zoneDouble) {
        if (zoneDouble != null) {
            SimpleRecord record = getOrCreateZoneRecord(zoneId);
            updateRecordMap(record.doubleRecord, zoneDouble);
        } else {
            zoneStats.remove(zoneId);
        }
    }
    public void onRecordZoneString(int zoneId, Map<String, String> zoneString) {
        if (zoneString != null) {
            SimpleRecord record = getOrCreateZoneRecord(zoneId);
            updateRecordMap(record.stringRecord, zoneString);
        } else {
            zoneStats.remove(zoneId);
        }
    }

    private String generateStateDirectory() {
        String fileName = startSystemTime + "_" + totalPlayers + ".json";
        return Paths.get(STATS_PATH, fileName).toString();
    }

    /**
     * 将数据写入json
     */
    private void saveStats() {
        // 按先排名，后游戏玩家id排序
        List<GamePlayerStats> gamePlayerStatsList = new ArrayList<>(gamePlayerStats.values());
        gamePlayerStatsList.sort(Comparator
                .comparingInt(GamePlayerStats::getGameRank)
                .thenComparingInt(s -> s.gameSingleId)
        );

        String filePath = generateStateDirectory();
        JsonArray jsonArray = new JsonArray();
        addGameStats(jsonArray);
        JsonUtils.writeJsonToFile(filePath, jsonArray);

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.saved_game_stats");
        } else {
            BattleRoyale.LOGGER.warn("GameManager doesn't have valid serverLevel, can't send message");
        }
        BattleRoyale.LOGGER.info("Saved game stats to {}", filePath);
    }

    private void addGameStats(@NotNull JsonArray jsonArray) {
        JsonObject statsObject = new JsonObject();

        statsObject.addProperty(STATS_TAG, GAME_TAG);
        addGameruleProperty(statsObject);
        addSpawnProperty(statsObject);
        addZoneProperty(statsObject);

        jsonArray.add(statsObject);
    }
    private void addGameruleProperty(JsonObject jsonObject) {
        JsonObject gameruleObject = new JsonObject();
        for (Map.Entry<String, Integer> entry : gameruleStats.intRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Boolean> entry : gameruleStats.boolRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : gameruleStats.doubleRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : gameruleStats.stringRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }

        jsonObject.add(GAMERULE_TAG, gameruleObject);
    }
    private void addSpawnProperty(JsonObject jsonObject) {
        JsonObject spawnObject = new JsonObject();

        for (Map.Entry<String, SimpleRecord> entry : spawnStats.entrySet()) {
            String spawnKey = entry.getKey();
            SimpleRecord record = entry.getValue();
            JsonObject singleSpawnObject = new JsonObject();
            for (Map.Entry<String, Integer> intEntry : record.intRecord.entrySet()) {
                singleSpawnObject.addProperty(intEntry.getKey(), intEntry.getValue());
            }
            for (Map.Entry<String, Boolean> boolEntry : record.boolRecord.entrySet()) {
                singleSpawnObject.addProperty(boolEntry.getKey(), boolEntry.getValue());
            }
            for (Map.Entry<String, Double> doubleEntry : record.doubleRecord.entrySet()) {
                singleSpawnObject.addProperty(doubleEntry.getKey(), doubleEntry.getValue());
            }
            for (Map.Entry<String, String> stringEntry : record.stringRecord.entrySet()) {
                singleSpawnObject.addProperty(stringEntry.getKey(), stringEntry.getValue());
            }

            spawnObject.add(spawnKey, singleSpawnObject);
        }

        jsonObject.add(SPAWN_TAG, spawnObject);
    }
    private void addZoneProperty(JsonObject jsonObject) {
        JsonObject zoneObject = new JsonObject();

        for (Map.Entry<Integer, SimpleRecord> entry : zoneStats.entrySet()) {
            String zoneKey = Integer.toString(entry.getKey());
            SimpleRecord record = entry.getValue();
            JsonObject singleZoneObject = new JsonObject();
            for (Map.Entry<String, Integer> intEntry : record.intRecord.entrySet()) {
                singleZoneObject.addProperty(intEntry.getKey(), intEntry.getValue());
            }
            for (Map.Entry<String, Boolean> boolEntry : record.boolRecord.entrySet()) {
                singleZoneObject.addProperty(boolEntry.getKey(), boolEntry.getValue());
            }
            for (Map.Entry<String, Double> doubleEntry : record.doubleRecord.entrySet()) {
                singleZoneObject.addProperty(doubleEntry.getKey(), doubleEntry.getValue());
            }
            for (Map.Entry<String, String> stringEntry : record.stringRecord.entrySet()) {
                singleZoneObject.addProperty(stringEntry.getKey(), stringEntry.getValue());
            }

            zoneObject.add(zoneKey, singleZoneObject);
        }

        jsonObject.add(ZONE_TAG, zoneObject);
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

    private <T> void updateRecordMap(Map<String, T> targetMap, Map<String, T> sourceMap) {
        if (sourceMap != null) {
            sourceMap.forEach((key, value) -> {
                if (value != null) {
                    targetMap.put(key, value);
                } else {
                    targetMap.remove(key);
                }
            });
        }
    }
    private void updateBoolRecordMap(Map<String, Boolean> targetMap, Map<String, Boolean> sourceMap) {
        if (sourceMap != null) {
            sourceMap.forEach((key, value) -> {
                if (Boolean.TRUE.equals(value)) {
                    targetMap.put(key, value);
                } else {
                    targetMap.remove(key);
                }
            });
        }
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
