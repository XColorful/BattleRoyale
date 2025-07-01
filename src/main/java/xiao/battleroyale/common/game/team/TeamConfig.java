package xiao.battleroyale.common.game.team;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.stats.IStatsWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;

/**
 * record类
 */
public class TeamConfig implements IStatsWriter {
    public int playerLimit = 0;
    public int teamSize = 0;
    public boolean aiTeammate = false;
    public boolean aiEnemy = false;
    public boolean autoJoinGame = false;

    public int teamMsgExpireTimeMillis = 300 * 1000;
    public void setTeamMsgExpireTimeSeconds(int seconds) { teamMsgExpireTimeMillis = Math.max(seconds * 1000, 0); }
    private final List<String> teamColors = new ArrayList<>();

    public TeamConfig() {
        teamColors.addAll(GameEntry.DEFAULT_TEAM_COLORS);
    }

    public void setTeamColors(List<String> colors) {
        // colors空也清除
        teamColors.clear();
        teamColors.addAll(colors);
    }

    @NotNull
    public String getTeamColor(int teamId) {
        if (teamColors.isEmpty()) {
            return "#FFFFFFFF";
        } else {
            return teamColors.get(teamId % teamColors.size());
        }
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
