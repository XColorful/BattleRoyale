package xiao.battleroyale.common.game.stats.game;

import java.util.Map;
import java.util.TreeMap;

/**
 * record类，自动按键排序
 */
public class SimpleRecord {

    public final Map<String, Integer> intRecord = new TreeMap<>();
    public final Map<String, Boolean> boolRecord = new TreeMap<>();
    public final Map<String, Double> doubleRecord = new TreeMap<>();
    public final Map<String, String> stringRecord = new TreeMap<>();

    public SimpleRecord() {
        ;
    }

    public void clear() {
        intRecord.clear();
        boolRecord.clear();
        doubleRecord.clear();
        stringRecord.clear();
    }
}
