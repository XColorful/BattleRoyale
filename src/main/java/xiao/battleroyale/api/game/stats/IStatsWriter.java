package xiao.battleroyale.api.game.stats;

import java.util.HashMap;
import java.util.Map;

public interface IStatsWriter {

    default
    Map<String, Integer> getIntGamerule() {
        return new HashMap<>();
    }
    default
    Map<String, Boolean> getBoolGamerule() {
        return new HashMap<>();
    }
    default
    Map<String, Double> getDoubleGamerule() {
        return new HashMap<>();
    }
    default
    Map<String, String> getStringGamerule() {
        return new HashMap<>();
    }
}
