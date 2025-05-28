package xiao.battleroyale.common.game.team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;

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

    /**
     * 构造函数。
     * @param gameTeamId 队伍 ID。
     * @param gameTeamColor 队伍颜色。
     */
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

    /**
     * 设置队长。
     * 如果指定的玩家不是队伍成员，则队长会被设置为 null。
     * @param playerUUID 队长 UUID。
     */
    public void setLeader(UUID playerUUID) {
        boolean found = false;
        // 遍历所有成员，确保只有指定玩家是队长，其他玩家则不是
        for (GamePlayer member : teamMembers) {
            if (member.getPlayerUUID().equals(playerUUID)) {
                member.setLeader(true);
                this.leaderUUID = playerUUID;
                found = true;
            } else {
                member.setLeader(false);
            }
        }
        // 如果指定的玩家不是队伍成员，则清空队长
        if (!found) {
            this.leaderUUID = null;
        }
    }

    public boolean isLeader(UUID playerUUID) {
        return leaderUUID != null && leaderUUID.equals(playerUUID);
    }

    public boolean isTeamAlive() {
        if (teamMembers.isEmpty()) {
            return true;
        }
        for (GamePlayer gamePlayer : teamMembers) {
            if (gamePlayer.isAlive() && !gamePlayer.isEliminated()) {
                return false;
            }
        }
        return true; // 所有玩家都已淘汰
    }

    public List<GamePlayer> getAlivePlayers() {
        return teamMembers.stream()
                .filter(GamePlayer::isAlive) // 筛选出 isAlive 的玩家
                .collect(Collectors.toList());
    }

    /**
     * 获取队伍中**未被淘汰**的玩家列表（即 isEliminated() 为 false 的玩家）。
     * 这些玩家仍在游戏循环中，即使倒地也可以被扶起。
     * @return 未被淘汰玩家的列表。
     */
    public List<GamePlayer> getStandingPlayers() {
        return teamMembers.stream()
                .filter(gamePlayer -> !gamePlayer.isEliminated()) // 筛选出未被淘汰的玩家
                .collect(Collectors.toList());
    }

    /**
     * 获取队伍成员的不可修改列表。
     * @return 队伍成员的不可修改列表。
     */
    public List<GamePlayer> getTeamMembers() { return Collections.unmodifiableList(teamMembers); }

    /**
     * 获取队伍成员数量。
     * @return 队伍成员的数量。
     */
    public int getTeamMemberCount() { return teamMembers.size(); }

    /**
     * 将玩家加入队伍。
     * 如果队伍当前没有队长，则第一个加入的玩家会自动成为队长。
     * @param gamePlayer 要加入的玩家信息。
     */
    public void addPlayer(@NotNull GamePlayer gamePlayer) {
        if (!teamMembers.contains(gamePlayer)) {
            teamMembers.add(gamePlayer);
            if (gamePlayer.getTeam() != this) {
                gamePlayer.setTeam(this); // 更正玩家所属队伍
                BattleRoyale.LOGGER.warn("GameTeam add a GamePlayer with different GameTeam, switched");
            }
            if (leaderUUID == null) { // 如果队伍没有队长，则新加入的玩家成为队长
                setLeader(gamePlayer.getPlayerUUID());
            }
        }
    }

    /**
     * 将玩家移出队伍。
     * 如果被移除的玩家是队长，会自动将队伍中第一个成员设置为新队长（如果存在）。
     * @param gamePlayer 要移除的玩家信息。
     */
    public void removePlayer(@NotNull GamePlayer gamePlayer) {
        if (teamMembers.remove(gamePlayer)) { // 尝试移除玩家
            gamePlayer.setTeam(null); // 清空玩家的队伍引用
            gamePlayer.setLeader(false); // 移除后不再是队长

            // 如果被移除的玩家是队长
            if (gamePlayer.getPlayerUUID().equals(leaderUUID)) {
                if (!teamMembers.isEmpty()) {
                    // 重新指定第一个成员为新队长
                    setLeader(teamMembers.get(0).getPlayerUUID());
                } else {
                    // 如果队伍为空，则清空队长
                    leaderUUID = null;
                }
            }
        }
    }
}