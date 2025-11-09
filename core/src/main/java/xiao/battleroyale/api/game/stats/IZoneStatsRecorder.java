package xiao.battleroyale.api.game.stats;

import java.util.Map;

public interface IZoneStatsRecorder {

    void onRecordZoneInt(int zoneId, Map<String, Integer> zoneIntWriter);

    void onRecordZoneBool(int zoneId, Map<String, Boolean> zoneBoolWriter);

    void onRecordZoneDouble(int zoneId, Map<String, Double> zoneDoubleWriter);

    void onRecordZoneString(int zoneId, Map<String, String> zoneStringWriter);
}
