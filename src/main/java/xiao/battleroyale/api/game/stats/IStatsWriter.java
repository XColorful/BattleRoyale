package xiao.battleroyale.api.game.stats;

import java.util.HashMap;
import java.util.Map;

public interface IStatsWriter {

    default
    Map<String, Integer> getIntWriter() {
        return new HashMap<>();
    }
    default
    Map<String, Boolean> getBoolWriter() {
        return new HashMap<>();
    }
    default
    Map<String, Double> getDoubleWriter() {
        return new HashMap<>();
    }
    default
    Map<String, String> getStringWriter() {
        return new HashMap<>();
    }
}
