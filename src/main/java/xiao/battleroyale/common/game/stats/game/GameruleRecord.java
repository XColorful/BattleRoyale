package xiao.battleroyale.common.game.stats.game;

import java.util.Map;
import java.util.TreeMap;

/**
 * record类，自动按键排序
 */
public class GameruleRecord extends AbstractGameRecord {

    public final Map<String, Integer> intGamerule = new TreeMap<>();
    public final Map<String, Boolean> boolGamerule = new TreeMap<>();
    public final Map<String, Double> doubleGamerule = new TreeMap<>();
    public final Map<String, String> stringGamerule = new TreeMap<>();

    public GameruleRecord() {
        ;
    }

    public void clear() {
        intGamerule.clear();
        boolGamerule.clear();
        doubleGamerule.clear();
        stringGamerule.clear();
    }
}
