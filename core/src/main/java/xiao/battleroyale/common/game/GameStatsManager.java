package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.stats.IStatsWriter;
import xiao.battleroyale.common.game.stats.StatsManager;

import java.util.Map;

public class GameStatsManager {

    public static boolean shouldRecordStats() { return BattleRoyale.getGameManager().getStatsManager().shouldRecordStats(); }
    public static void recordIntGamerule(Map<String, Integer> intGameruleWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordIntGamerule(intGameruleWriter); }
    public static void recordBoolGamerule(Map<String, Boolean> boolGameruleWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordBoolGamerule(boolGameruleWriter); }
    public static void recordDoubleGamerule(Map<String, Double> doubleGameruleWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordDoubleGamerule(doubleGameruleWriter); }
    public static void recordStringGamerule(Map<String, String> stringGameruleWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordStringGamerule(stringGameruleWriter); }
    public static void recordGamerule(IStatsWriter gameruleWriter) {
        recordIntGamerule(gameruleWriter.getIntWriter());
        recordBoolGamerule(gameruleWriter.getBoolWriter());
        recordDoubleGamerule(gameruleWriter.getDoubleWriter());
        recordStringGamerule(gameruleWriter.getStringWriter());
    }
    public static void recordSpawnInt(String key, Map<String, Integer> spawnIntWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordSpawnInt(key, spawnIntWriter); }
    public static void recordSpawnBool(String key, Map<String, Boolean> spawnBoolWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordSpawnBool(key, spawnBoolWriter); }
    public static void recordSpawnDouble(String key, Map<String, Double> spawnDoubleWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordSpawnDouble(key, spawnDoubleWriter); }
    public static void recordSpawnString(String key, Map<String, String> spawnStringWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordSpawnString(key, spawnStringWriter); }
    public static void recordSpawn(String key, IStatsWriter spawnWriter) {
        recordSpawnInt(key, spawnWriter.getIntWriter());
        recordSpawnBool(key, spawnWriter.getBoolWriter());
        recordSpawnDouble(key, spawnWriter.getDoubleWriter());
        recordSpawnString(key, spawnWriter.getStringWriter());
    }
    public static void recordZoneInt(int zoneId, Map<String, Integer> zoneIntWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordZoneInt(zoneId, zoneIntWriter); }
    public static void recordZoneBool(int zoneId, Map<String, Boolean> zoneBoolWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordZoneBool(zoneId, zoneBoolWriter); }
    public static void recordZoneDouble(int zoneId, Map<String, Double> zoneDoubleWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordZoneDouble(zoneId, zoneDoubleWriter); }
    public static void recordZoneString(int zoneId, Map<String, String> zoneStringWriter) { BattleRoyale.getGameManager().getStatsManager().onRecordZoneString(zoneId, zoneStringWriter); }
    public static void recordZone(int zoneId, IStatsWriter zoneWriter) {
        recordZoneInt(zoneId, zoneWriter.getIntWriter());
        recordZoneBool(zoneId, zoneWriter.getBoolWriter());
        recordZoneDouble(zoneId, zoneWriter.getDoubleWriter());
        recordZoneString(zoneId, zoneWriter.getStringWriter());
    }
}
