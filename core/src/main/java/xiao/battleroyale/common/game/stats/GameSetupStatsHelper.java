package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.stats.StatsTag;
import xiao.battleroyale.common.game.stats.game.SimpleRecord;

import java.util.Map;
import java.util.function.Function;

public class GameSetupStatsHelper {

    // ----Gamerule----
    protected static void onRecordIntGamerule(StatsManager statsManager, Map<String, Integer> intGamerule) {
        updateRecordMap(statsManager.statsData.gameruleStats.intRecord, intGamerule);
    }

    protected static void onRecordBoolGamerule(StatsManager statsManager, Map<String, Boolean> boolGamerule) {
        updateBoolRecordMap(statsManager.statsData.gameruleStats.boolRecord, boolGamerule);
    }

    protected static void onRecordDoubleGamerule(StatsManager statsManager, Map<String, Double> doubleGamerule) {
        updateRecordMap(statsManager.statsData.gameruleStats.doubleRecord, doubleGamerule);
    }

    protected static void onRecordStringGamerule(StatsManager statsManager, Map<String, String> stringGamerule) {
        updateRecordMap(statsManager.statsData.gameruleStats.stringRecord, stringGamerule);
    }
    // ----Spawn----
    protected static void onRecordSpawnInt(StatsManager statsManager, String key, Map<String, Integer> spawnInt) {
        updateSpawnRecord(statsManager, key, spawnInt, record -> record.intRecord, false);
    }

    protected static void onRecordSpawnBool(StatsManager statsManager, String key, Map<String, Boolean> spawnBool) {
        updateSpawnRecord(statsManager, key, spawnBool, record -> record.boolRecord, true);
    }

    protected static void onRecordSpawnDouble(StatsManager statsManager, String key, Map<String, Double> spawnDouble) {
        updateSpawnRecord(statsManager, key, spawnDouble, record -> record.doubleRecord, false);
    }

    protected static void onRecordSpawnString(StatsManager statsManager, String key, Map<String, String> spawnString) {
        updateSpawnRecord(statsManager, key, spawnString, record -> record.stringRecord, false);
    }
    // ----Zone----
    protected static void onRecordZoneInt(StatsManager statsManager, int zoneId, Map<String, Integer> zoneInt) {
        updateZoneRecord(statsManager, zoneId, zoneInt, record -> record.intRecord, false);
    }

    protected static void onRecordZoneBool(StatsManager statsManager, int zoneId, Map<String, Boolean> zoneBool) {
        updateZoneRecord(statsManager, zoneId, zoneBool, record -> record.boolRecord, true);
    }

    protected static void onRecordZoneDouble(StatsManager statsManager, int zoneId, Map<String, Double> zoneDouble) {
        updateZoneRecord(statsManager, zoneId, zoneDouble, record -> record.doubleRecord, false);
    }

    protected static void onRecordZoneString(StatsManager statsManager, int zoneId, Map<String, String> zoneString) {
        updateZoneRecord(statsManager, zoneId, zoneString, record -> record.stringRecord, false);
    }

    private static <V> void updateSpawnRecord(StatsManager statsManager, String key, Map<String, V> data, Function<SimpleRecord, Map<String, V>> mapSelector, boolean isBool) {
        if (data != null) {
            SimpleRecord record = statsManager.statsData.spawnStats.computeIfAbsent(key, k -> new SimpleRecord());
            if (isBool) updateBoolRecordMap((Map<String, Boolean>) mapSelector.apply(record), (Map<String, Boolean>) data);
            else updateRecordMap(mapSelector.apply(record), data);
        } else {
            statsManager.statsData.spawnStats.remove(key);
        }
    }

    private static <V> void updateZoneRecord(StatsManager statsManager, int zoneId, Map<String, V> data, Function<SimpleRecord, Map<String, V>> mapSelector, boolean isBool) {
        if (data != null) {
            SimpleRecord record = statsManager.statsData.zoneStats.computeIfAbsent(zoneId, k -> new SimpleRecord());
            if (isBool) updateBoolRecordMap((Map<String, Boolean>) mapSelector.apply(record), (Map<String, Boolean>) data);
            else updateRecordMap(mapSelector.apply(record), data);
        } else {
            statsManager.statsData.zoneStats.remove(zoneId);
        }
    }

    private static <T> void updateRecordMap(Map<String, T> targetMap, Map<String, T> sourceMap) {
        if (sourceMap != null) {
            sourceMap.forEach((key, value) -> {
                if (value != null) targetMap.put(key, value);
                else targetMap.remove(key);
            });
        }
    }

    private static void updateBoolRecordMap(Map<String, Boolean> targetMap, Map<String, Boolean> sourceMap) {
        if (sourceMap != null) {
            sourceMap.forEach((key, value) -> {
                if (Boolean.TRUE.equals(value)) targetMap.put(key, value);
                else targetMap.remove(key);
            });
        }
    }

    protected static void addGameSetupStats(StatsManager statsManager, @NotNull JsonArray jsonArray) {
        JsonObject statsObject = new JsonObject();
        statsObject.addProperty(StatsTag.STATS_TAG, StatsTag.GAME_TAG);

        // Gamerule
        statsObject.add(StatsTag.GAMERULE_TAG, serializeRecord(statsManager.statsData.gameruleStats));

        // Spawn
        JsonObject spawnObject = new JsonObject();
        statsManager.statsData.spawnStats.forEach((key, record) -> spawnObject.add(key, serializeRecord(record)));
        statsObject.add(StatsTag.SPAWN_TAG, spawnObject);

        // Zone
        JsonObject zoneObject = new JsonObject();
        statsManager.statsData.zoneStats.forEach((key, record) -> zoneObject.add(String.valueOf(key), serializeRecord(record)));
        statsObject.add(StatsTag.ZONE_TAG, zoneObject);

        jsonArray.add(statsObject);
    }

    private static JsonObject serializeRecord(SimpleRecord record) {
        JsonObject jsonObject = new JsonObject();
        record.intRecord.forEach(jsonObject::addProperty);
        record.boolRecord.forEach(jsonObject::addProperty);
        record.doubleRecord.forEach(jsonObject::addProperty);
        record.stringRecord.forEach(jsonObject::addProperty);
        return jsonObject;
    }
}