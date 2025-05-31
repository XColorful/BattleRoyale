package xiao.battleroyale.common.game.team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 所有接口均动态计算，游戏运行时 TeamManager 只负责修改 GamePlayer。
 * GameTeam 负责管理队伍成员的状态和队长信息。
 */
public class GameTeam {

    private final int gameTeamId; // 队伍的唯一 ID
    private final String gameTeamColor; // 队伍的颜色
    private final List<GamePlayer> teamMembers = new ArrayList<>(); // 队伍成员列表
    private UUID leaderUUID; // 队伍队长的 UUID


    public GameTeam(int gameTeamId, String gameTeamColor) {
        this.gameTeamId = gameTeamId;
        this.gameTeamColor = gameTeamColor;
        this.leaderUUID = null;
    }

    public int getGameTeamId() { return gameTeamId; }
    public String getGameTeamColor() { return gameTeamColor; }
    public UUID getLeaderUUID() { return leaderUUID; }
    @Nullable
    public GamePlayer getLeader() {
        for (GamePlayer member : teamMembers) {
            if (member.getPlayerUUID().equals(this.leaderUUID)) {
                return member;
            }
        }
        return null;
    }

    public void setLeader(UUID playerUUID) {
        boolean found = false;
        for (GamePlayer member : teamMembers) {
            if (member.getPlayerUUID().equals(playerUUID)) {
                member.setLeader(true);
                this.leaderUUID = playerUUID;
                found = true;
                break;
            }
        }
        if (found) {
            for (GamePlayer member : teamMembers) {
                if (!member.getPlayerUUID().equals(playerUUID)) {
                    member.setLeader(false);
                }
            }
        }
    }

    public boolean isLeader(UUID playerUUID) {
        return leaderUUID != null && leaderUUID.equals(playerUUID);
    }

    public boolean isTeamEliminated() {
        if (teamMembers.isEmpty()) {
            return true;
        }
        for (GamePlayer gamePlayer : teamMembers) {
            if (gamePlayer.isAlive() && !gamePlayer.isEliminated()) { // 不负责全体不在线的情况，这属于GameManager的范围
                return false;
            }
        }
        return true;
    }

    // 未倒地的玩家
    public List<GamePlayer> getAlivePlayers() {
        return teamMembers.stream()
                .filter(GamePlayer::isAlive)
                .collect(Collectors.toList());
    }

    // 未被淘汰的玩家
    public List<GamePlayer> getStandingPlayers() {
        return teamMembers.stream()
                .filter(gamePlayer -> !gamePlayer.isEliminated())
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getTeamMembers() { return Collections.unmodifiableList(teamMembers); }

    public int getTeamMemberCount() { return teamMembers.size(); }

    public void addPlayer(@NotNull GamePlayer gamePlayer) {
        if (teamMembers.contains(gamePlayer)) {
            return;
        }
        teamMembers.add(gamePlayer);
        if (gamePlayer.getTeam() != this) {
            gamePlayer.setTeam(this);
        }
        if (leaderUUID == null) {
            setLeader(gamePlayer.getPlayerUUID());
        }
    }

    /**
     * 调用后需要保证传入的 gamePlayer 不再使用或立即分配新队伍，保证 gamePlayer.getTeam 始终不为 null
     */
    public void removePlayer(@NotNull GamePlayer gamePlayer) {
        if (!teamMembers.remove(gamePlayer)) {
            return;
        }

        gamePlayer.setTeam(null);
        gamePlayer.setLeader(false);

        if (gamePlayer.getPlayerUUID().equals(leaderUUID)) {
            if (!teamMembers.isEmpty()) {
                setLeader(teamMembers.get(0).getPlayerUUID());
            } else {
                leaderUUID = null;
            }
        }
    }
}