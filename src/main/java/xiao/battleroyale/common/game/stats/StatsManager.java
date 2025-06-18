package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.stats.game.GameruleRecord;
import xiao.battleroyale.common.game.stats.game.SpawnRecord;
import xiao.battleroyale.common.game.stats.game.ZoneRecord;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.event.game.StatsEventHandler;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StatsManager extends AbstractGameManager {

    private static class StatsManagerHolder {
        private static final StatsManager INSTANCE = new StatsManager();
    }

    public static StatsManager get() {
        return StatsManagerHolder.INSTANCE;
    }

    private StatsManager() {}

    public static void init() {
        ;
    }

    public static final String STATS_SUB_PATH = "stats";
    public static final String STATS_PATH = Paths.get(AbstractGameManager.MOD_DATA_PATH).resolve(STATS_SUB_PATH).toString();
    private static final String STATS_TAG = "stats";
    private static final String GAMERULE_TAG = "gamerule";
    private static final String TIMELINE_TAG = "timeline";
    private static final String RANK_TAG = "rank";
    private static final String DETAIL_TAG = "detail";

    // player
    private final Map<GamePlayer, GamePlayerStats> gamePlayerStats = new HashMap<>();
    private final Map<DamageSource, DamageSourceStats> damageSourceStats = new HashMap<>();
    // zone
    private final Map<Integer, ZoneRecord> zoneStats = new HashMap<>(); // zoneId -> ZoneRecord
    private final Map<Integer, SpawnRecord> spawnStats = new HashMap<>(); // gameSingleId -> SpawnRecord
    // gamerule
    private final GameruleRecord gameruleStats = new GameruleRecord();

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
        BattleroyaleEntry brEntry = GameConfigManager.get().getGameruleConfig(GameManager.get().getGameruleConfigId()).getBattleRoyaleEntry();
        recordStats = brEntry.recordGameStats;

        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        clearStats();

        this.ready = true;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (shouldRecordStats()) {
            StatsEventHandler.register();
            startSystemTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            totalPlayers = GameManager.get().getGamePlayers().size();
            for (GamePlayer gamePlayer : GameManager.get().getStandingGamePlayers()) {
                gamePlayerStats.put(gamePlayer, new GamePlayerStats(gamePlayer));
            }
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
        this.prepared = false;
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
//                if (damagedGamePlayer.getGameTeamId() != attackingGamePlayer.getGameTeamId()) { // 友伤不计入伤害量
//                    attackingGamePlayer.addDamageDealt(damageAmount);
//                }
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

    public void onRecordIntGamerule(Map<String, Integer> intGamerule) {
        if (!shouldRecordStats()) {
            return;
        }

        intGamerule.forEach((key, value) -> {
            if (value != null) {
                gameruleStats.intGamerule.put(key, value);
            } else {
                gameruleStats.intGamerule.remove(key);
            }
        });
    }
    public void onRecordBoolGamerule(Map<String, Boolean> boolGamerule) {
        if (!shouldRecordStats()) {
            return;
        }

        boolGamerule.forEach((key, value) -> {
            if (Boolean.TRUE.equals(value)) {
                gameruleStats.boolGamerule.put(key, value);
            } else {
                gameruleStats.boolGamerule.remove(key);
            }
        });
    }
    public void onRecordDoubleGamerule(Map<String, Double> doubleGamerule) {
        if (!shouldRecordStats()) {
            return;
        }

        doubleGamerule.forEach((key, value) -> {
            if (value != null) {
                gameruleStats.doubleGamerule.put(key, value);
            } else {
                gameruleStats.doubleGamerule.remove(key);
            }
        });
    }
    public void onRecordStringGamerule(Map<String, String> stringGamerule) {
        if (!shouldRecordStats()) {
            return;
        }

        stringGamerule.forEach((key, value) -> {
            if (value != null) {
                gameruleStats.stringGamerule.put(key, value);
            } else {
                gameruleStats.stringGamerule.remove(key);
            }
        });
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
        addGameruleStats(jsonArray);
        JsonUtils.writeJsonToFile(filePath, jsonArray);

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.saved_game_stats");
        } else {
            BattleRoyale.LOGGER.warn("GameManager doesn't have valid serverLevel, can't send message");
        }
        BattleRoyale.LOGGER.info("Saved game stats to {}", filePath);
    }

    private void addGameruleStats(@NotNull JsonArray jsonArray) {
        JsonObject statsObject = new JsonObject();
        statsObject.addProperty(STATS_TAG, GAMERULE_TAG);
        JsonObject gameruleObject = new JsonObject();

        for (Map.Entry<String, Integer> entry : gameruleStats.intGamerule.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Boolean> entry : gameruleStats.boolGamerule.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : gameruleStats.doubleGamerule.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : gameruleStats.stringGamerule.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }

        statsObject.add(GAMERULE_TAG, gameruleObject);
        jsonArray.add(statsObject);
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
