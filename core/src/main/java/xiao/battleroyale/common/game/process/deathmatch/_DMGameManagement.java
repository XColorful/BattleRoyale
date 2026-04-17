package xiao.battleroyale.common.game.process.deathmatch;

import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

public class _DMGameManagement {

    protected static void finishGameAddWinner(DMGameProcessManager dmGameProcessManager, IGameManager gameManager, boolean hasWinner) {
        gameManager.setHasWinner(hasWinner);
        if (hasWinner) {
            int minWinnerTeam = gameManager.getWinnerTeamTotal();
            NavigableMap<Integer, Set<GameTeam>> sortedKills = dmGameProcessManager.deathMatchData.getTeamKillsGreaterOrEqual(dmGameProcessManager.configEntry.targetKill);

            int addedTeam = 0;
            for (Map.Entry<Integer, Set<GameTeam>> entry : sortedKills.entrySet()) {
                int kill = entry.getKey();
                Set<GameTeam> gameTeams = entry.getValue();

                for (GameTeam team : gameTeams) {
                    gameManager.addWinnerGameTeam(team);
                }

                // 胜利队伍数 >= 最小胜利队伍数
                addedTeam += gameTeams.size();
                if (addedTeam > minWinnerTeam) {
                    break;
                }
            }

            for (GameTeam team : gameManager.getWinnerGameTeams()) {
                for (GamePlayer member : team.getTeamMembers()) {
                    gameManager.addWinnerGamePlayer(member);
                }
            }
        }
    }
}
