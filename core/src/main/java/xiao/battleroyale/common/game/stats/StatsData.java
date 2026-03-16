package xiao.battleroyale.common.game.stats;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.AbstractGameManagerData;
import xiao.battleroyale.common.game.stats.game.SimpleRecord;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.*;

public class StatsData extends AbstractGameManagerData {

    private static final String DATA_NAME = "StatsData";

    // Game stats
    protected final SimpleRecord gameruleStats = new SimpleRecord();
    protected final Map<String, SimpleRecord> spawnStats = new TreeMap<>(); // key/singleId -> spawnRecord
    protected final Map<Integer, SimpleRecord> zoneStats = new HashMap<>(); // zoneId -> ZoneRecord

    // GamePlayer stats
    protected final Map<GamePlayer, GamePlayerStats> gamePlayerStats = new HashMap<>();
    protected final Map<DamageSource, DamageSourceStats> damageSourceStats = new HashMap<>();

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

        gamePlayerStats.clear();
        damageSourceStats.clear();
    }

    @Override
    public void startGame() {
        if (locked) {
            return;
        }

        lockData();
    }
    public void addRecordGamePlayers(List<GamePlayer> gamePlayers) {
        if (locked) {
            return;
        }

        for (GamePlayer gamePlayer : gamePlayers) {
            gamePlayerStats.put(gamePlayer, new GamePlayerStats(gamePlayer));
        }
    }

    public @Nullable GamePlayerStats getGamePlayerStats(GamePlayer gamePlayer) {
        return gamePlayerStats.get(gamePlayer);
    }

    public Set<GamePlayer> getRecordGamePlayers() {
        return this.gamePlayerStats.keySet();
    }
    public boolean isInRecordGamePlayers(GamePlayer player) {
        return this.gamePlayerStats.containsKey(player);
    }

    public List<GamePlayerStats> getRecordGamePlayerStats() {
        return new ArrayList<>(this.gamePlayerStats.values());
    }

    @Override
    public void endGame() {
        if (locked) {
            unlockData();
        }
    }
}
