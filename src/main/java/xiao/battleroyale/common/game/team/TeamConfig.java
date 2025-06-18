package xiao.battleroyale.common.game.team;

import xiao.battleroyale.api.game.stats.IStatsWriter;

import java.util.HashMap;
import java.util.Map;

import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;

/**
 * record类
 */
public class TeamConfig implements IStatsWriter {
    public int playerLimit = 0;
    public int teamSize = 0;
    public boolean aiTeammate = false;
    public boolean aiEnemy = false;
    public boolean autoJoinGame = false;

    public TeamConfig() {
        ;
    }

    @Override
    public Map<String, Integer> getIntWriter() {
        Map<String, Integer> intGamerule = new HashMap<>();
        intGamerule.put(BattleroyaleEntryTag.PLAYER_TOTAL, playerLimit);
        intGamerule.put(BattleroyaleEntryTag.TEAM_SIZE, teamSize);
        return intGamerule;
    }
    @Override
    public Map<String, Boolean> getBoolWriter() {
        Map<String, Boolean> boolGamerule = new HashMap<>();
        boolGamerule.put(BattleroyaleEntryTag.AI_TEAMMATE, aiTeammate);
        boolGamerule.put(BattleroyaleEntryTag.AI_ENEMY, aiEnemy);
        boolGamerule.put(BattleroyaleEntryTag.AUTO_JOIN, autoJoinGame);
        return boolGamerule;
    }
}
