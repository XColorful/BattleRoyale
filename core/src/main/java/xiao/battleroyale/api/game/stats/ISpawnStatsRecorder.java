package xiao.battleroyale.api.game.stats;

import java.util.Map;

public interface ISpawnStatsRecorder {

    void onRecordSpawnInt(String key, Map<String, Integer> spawnIntWriter);

    void onRecordSpawnBool(String key, Map<String, Boolean> spawnBoolWriter);

    void onRecordSpawnDouble(String key, Map<String, Double> spawnDoubleWriter);

    void onRecordSpawnString(String key, Map<String, String> spawnStringWriter);
}
