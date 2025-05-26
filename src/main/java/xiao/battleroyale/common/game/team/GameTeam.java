package xiao.battleroyale.common.game.team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 所有接口均动态计算，游戏运行时 TeamManager 只负责修改 GamePlayer
 */
public class GameTeam {

    private final int gameTeamId;
    private final String gameTeamColor;
    private final List<GamePlayer> teamMembers = new ArrayList<>();

    public GameTeam(int gameTeamId, String gameTeamColor) {
        this.gameTeamId = gameTeamId;
        this.gameTeamColor = gameTeamColor;
    }

    public int getGameTeamId() { return gameTeamId; }
    public String getGameTeamColor() { return gameTeamColor; }

    /**
     * 只判断队伍是否存活，有1人未被淘汰则存活（即未被eliminated）
     * @return 判定结果
     */
    public boolean isTeamAlive() {
        for (GamePlayer gamePlayer : teamMembers) {
            if (!gamePlayer.isEliminated()) { // 只要有一个玩家未被淘汰，队伍就视为存活
                return true;
            }
        }
        return false;
    }
    /**
     * 获取未倒地的玩家数 (即 isAlive() 为 true 的玩家)
     * @return 队伍可活动人数
     */
    public List<GamePlayer> getAlivePlayers() {
        return teamMembers.stream()
                .filter(GamePlayer::isAlive) // 筛选出 alive 的玩家
                .collect(Collectors.toList());
    }
    /**
     * 获取未被淘汰的玩家数 (即 isEliminated() 为 false 的玩家)
     * @return 队伍总存活人数
     */
    public List<GamePlayer> getStandingPlayers() {
        return teamMembers.stream()
                .filter(gamePlayer -> !gamePlayer.isEliminated()) // 筛选出未被淘汰的玩家
                .collect(Collectors.toList());
    }
    public List<GamePlayer> getTeamMembers() { return teamMembers; }
    public int getTeamMemberCount() { return teamMembers.size(); }
    public void addPlayer(GamePlayer gamePlayer) { teamMembers.add(gamePlayer); }
    public void removePlayer(GamePlayer gamePlayer) {
        teamMembers.remove(gamePlayer); // 移除指定玩家
    }
}