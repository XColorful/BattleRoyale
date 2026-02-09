package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.stats.game.SimpleRecord;

import java.util.Map;

import static xiao.battleroyale.common.game.stats.StatsManager.*;

public class GameSetupStatsHelper {

    /**
     * Gamerule 相关
     */
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

    /**
     * Spawn 相关
     */
    protected static void onRecordSpawnInt(StatsManager statsManager, String key, Map<String, Integer> spawnInt) {
        updateSpawnRecord(statsManager, key, spawnInt, (record, data) -> updateRecordMap(record.intRecord, data));
    }

    protected static void onRecordSpawnBool(StatsManager statsManager, String key, Map<String, Boolean> spawnBool) {
        updateSpawnRecord(statsManager, key, spawnBool, (record, data) -> updateBoolRecordMap(record.boolRecord, data));
    }

    protected static void onRecordSpawnDouble(StatsManager statsManager, String key, Map<String, Double> spawnDouble) {
        updateSpawnRecord(statsManager, key, spawnDouble, (record, data) -> updateRecordMap(record.doubleRecord, data));
    }

    protected static void onRecordSpawnString(StatsManager statsManager, String key, Map<String, String> spawnString) {
        updateSpawnRecord(statsManager, key, spawnString, (record, data) -> updateRecordMap(record.stringRecord, data));
    }

    /**
     * Zone 相关
     */
    protected static void onRecordZoneInt(StatsManager statsManager, int zoneId, Map<String, Integer> zoneInt) {
        updateZoneRecord(statsManager, zoneId, zoneInt, (record, data) -> updateRecordMap(record.intRecord, data));
    }

    protected static void onRecordZoneBool(StatsManager statsManager, int zoneId, Map<String, Boolean> zoneBool) {
        updateZoneRecord(statsManager, zoneId, zoneBool, (record, data) -> updateBoolRecordMap(record.boolRecord, data));
    }

    protected static void onRecordZoneDouble(StatsManager statsManager, int zoneId, Map<String, Double> zoneDouble) {
        updateZoneRecord(statsManager, zoneId, zoneDouble, (record, data) -> updateRecordMap(record.doubleRecord, data));
    }

    protected static void onRecordZoneString(StatsManager statsManager, int zoneId, Map<String, String> zoneString) {
        updateZoneRecord(statsManager, zoneId, zoneString, (record, data) -> updateRecordMap(record.stringRecord, data));
    }

    private static <K, V> void updateSpawnRecord(StatsManager statsManager, String key, Map<String, V> data, RecordUpdater<V> updater) {
        if (data != null) {
            SimpleRecord record = statsManager.statsData.spawnStats.computeIfAbsent(key, k -> new SimpleRecord());
            updater.update(record, data);
        } else {
            statsManager.statsData.spawnStats.remove(key);
        }
    }

    private static <K, V> void updateZoneRecord(StatsManager statsManager, int zoneId, Map<String, V> data, RecordUpdater<V> updater) {
        if (data != null) {
            SimpleRecord record = statsManager.statsData.zoneStats.computeIfAbsent(zoneId, k -> new SimpleRecord());
            updater.update(record, data);
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

    @FunctionalInterface
    private interface RecordUpdater<V> {
        void update(SimpleRecord record, Map<String, V> data);
    }

    protected static void addGameSetupStats(StatsManager statsManager, @NotNull JsonArray jsonArray) {
        JsonObject statsObject = new JsonObject();

        statsObject.addProperty(STATS_TAG, GAME_TAG);
        addGameruleProperty(statsManager, statsObject);
        addSpawnProperty(statsManager, statsObject);
        addZoneProperty(statsManager, statsObject);

        jsonArray.add(statsObject);
    }
    private static void addGameruleProperty(StatsManager statsManager, JsonObject jsonObject) {
        JsonObject gameruleObject = new JsonObject();
        for (Map.Entry<String, Integer> entry : statsManager.statsData.gameruleStats.intRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Boolean> entry : statsManager.statsData.gameruleStats.boolRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : statsManager.statsData.gameruleStats.doubleRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : statsManager.statsData.gameruleStats.stringRecord.entrySet()) {
            gameruleObject.addProperty(entry.getKey(), entry.getValue());
        }

        jsonObject.add(GAMERULE_TAG, gameruleObject);
    }
    private static void addSpawnProperty(StatsManager statsManager, JsonObject jsonObject) {
        JsonObject spawnObject = new JsonObject();

        for (Map.Entry<String, SimpleRecord> entry : statsManager.statsData.spawnStats.entrySet()) {
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
    private static void addZoneProperty(StatsManager statsManager, JsonObject jsonObject) {
        JsonObject zoneObject = new JsonObject();

        for (Map.Entry<Integer, SimpleRecord> entry : statsManager.statsData.zoneStats.entrySet()) {
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
}