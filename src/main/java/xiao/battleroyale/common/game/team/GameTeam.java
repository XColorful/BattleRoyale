package xiao.battleroyale.common.game.team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameTeam {

    private final int gameTeamId;
    private final String gameTeamColor;
    private int teamTotalAlive; // 总存活人数
    private final List<GamePlayer> teamMembers = new ArrayList<>();

    public GameTeam(int gameTeamId, String gameTeamColor) {
        this.gameTeamId = gameTeamId;
        this.gameTeamColor = gameTeamColor;
    }

    public int getGameTeamId() { return gameTeamId; }
    public String getGameTeamColor() { return gameTeamColor; }
    public boolean isTeamAlive() { return teamTotalAlive > 0; }
    public int getTeamTotalAlive() { return teamTotalAlive; }
    public List<GamePlayer> getTeamMembers() { return teamMembers; }
    public List<GamePlayer> getAlivePlayers() { // 对极小列表可能开销大？一般就1人为主，照顾开销的范围最多到1队4人
        return teamMembers.stream()
                .filter(GamePlayer::isAlive)
                .collect(Collectors.toList());
    }
}
