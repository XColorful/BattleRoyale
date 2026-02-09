package xiao.battleroyale.common.game.stats;

import xiao.battleroyale.common.game.AbstractGameManagerData;
import xiao.battleroyale.common.game.stats.game.SimpleRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class StatsData extends AbstractGameManagerData {

    private static final String DATA_NAME = "StatsData";

    // game
    protected final SimpleRecord gameruleStats = new SimpleRecord();
    protected final Map<String, SimpleRecord> spawnStats = new TreeMap<>(); // key/singleId -> spawnRecord
    protected final Map<Integer, SimpleRecord> zoneStats = new HashMap<>(); // zoneId -> ZoneRecord

    public StatsData() {
        super(DATA_NAME);
    }

    @Override
    public void clear() {
        if (locked) {
            return;
        }
        zoneStats.clear();
        spawnStats.clear();
        gameruleStats.clear();
    }

    @Override
    public void startGame() {
        if (locked) {
            return;
        }

        lockData();
    }

    @Override
    public void endGame() {
        if (locked) {
            unlockData();
        }
    }
}
