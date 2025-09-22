package xiao.battleroyale.common.game;

import xiao.battleroyale.api.game.stats.IStatsWriter;
import xiao.battleroyale.common.game.stats.StatsManager;

import java.util.Map;

public class GameStatsManager {
    
    private static final StatsManager statsManagerInstance = StatsManager.get();

    public static boolean shouldRecordStats() { return statsManagerInstance.shouldRecordStats(); }
    public static void recordIntGamerule(Map<String, Integer> intGameruleWriter) { statsManagerInstance.onRecordIntGamerule(intGameruleWriter); }
    public static void recordBoolGamerule(Map<String, Boolean> boolGameruleWriter) { statsManagerInstance.onRecordBoolGamerule(boolGameruleWriter); }
    public static void recordDoubleGamerule(Map<String, Double> doubleGameruleWriter) { statsManagerInstance.onRecordDoubleGamerule(doubleGameruleWriter); }
    public static void recordStringGamerule(Map<String, String> stringGameruleWriter) { statsManagerInstance.onRecordStringGamerule(stringGameruleWriter); }
    public static void recordGamerule(IStatsWriter gameruleWriter) {
        recordIntGamerule(gameruleWriter.getIntWriter());
        recordBoolGamerule(gameruleWriter.getBoolWriter());
        recordDoubleGamerule(gameruleWriter.getDoubleWriter());
        recordStringGamerule(gameruleWriter.getStringWriter());
    }
    public static void recordSpawnInt(String key, Map<String, Integer> spawnIntWriter) { statsManagerInstance.onRecordSpawnInt(key, spawnIntWriter); }
    public static void recordSpawnBool(String key, Map<String, Boolean> spawnBoolWriter) { statsManagerInstance.onRecordSpawnBool(key, spawnBoolWriter); }
    public static void recordSpawnDouble(String key, Map<String, Double> spawnDoubleWriter) { statsManagerInstance.onRecordSpawnDouble(key, spawnDoubleWriter); }
    public static void recordSpawnString(String key, Map<String, String> spawnStringWriter) { statsManagerInstance.onRecordSpawnString(key, spawnStringWriter); }
    public static void recordSpawn(String key, IStatsWriter spawnWriter) {
        recordSpawnInt(key, spawnWriter.getIntWriter());
        recordSpawnBool(key, spawnWriter.getBoolWriter());
        recordSpawnDouble(key, spawnWriter.getDoubleWriter());
        recordSpawnString(key, spawnWriter.getStringWriter());
    }
    public static void recordZoneInt(int zoneId, Map<String, Integer> zoneIntWriter) { statsManagerInstance.onRecordZoneInt(zoneId, zoneIntWriter); }
    public static void recordZoneBool(int zoneId, Map<String, Boolean> zoneBoolWriter) { statsManagerInstance.onRecordZoneBool(zoneId, zoneBoolWriter); }
    public static void recordZoneDouble(int zoneId, Map<String, Double> zoneDoubleWriter) { statsManagerInstance.onRecordZoneDouble(zoneId, zoneDoubleWriter); }
    public static void recordZoneString(int zoneId, Map<String, String> zoneStringWriter) { statsManagerInstance.onRecordZoneString(zoneId, zoneStringWriter); }
    public static void recordZone(int zoneId, IStatsWriter zoneWriter) {
        recordZoneInt(zoneId, zoneWriter.getIntWriter());
        recordZoneBool(zoneId, zoneWriter.getBoolWriter());
        recordZoneDouble(zoneId, zoneWriter.getDoubleWriter());
        recordZoneString(zoneId, zoneWriter.getStringWriter());
    }
}
