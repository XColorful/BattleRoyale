package xiao.battleroyale.common.game.team;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.util.ColorUtils;

import java.util.*;

/**
 * 该类仅用于抽离TeamManager的功能实现，简化TeamManager
 * 类似.h和.cpp的设计
 */
public class TeamUtils {

    /**
     * 返回非人机队伍数量
     */
    public static int getNonBotTeamCount() {
        TeamManager teamManager = TeamManager.get();
        int count = 0;
        Set<Integer> playerTeamId = new HashSet<>();
        for (GamePlayer gamePlayer : teamManager.getGamePlayers()) {
            if (!gamePlayer.isBot()) {
                int teamId = gamePlayer.getGameTeamId();
                if (!playerTeamId.contains(teamId)) {
                    playerTeamId.add(teamId);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 返回未被淘汰的非人机队伍数量
     */
    public static int getStandingPlayerTeamCount() {
        TeamManager teamManager = TeamManager.get();
        int count = 0;
        Set<Integer> playerTeamId = new HashSet<>();
        for (GamePlayer gamePlayer : teamManager.getStandingGamePlayers()) {
            if (!gamePlayer.isBot()) {
                int teamId = gamePlayer.getGameTeamId();
                if (!playerTeamId.contains(teamId)) {
                    playerTeamId.add(teamId);
                    count++;
                }
            }
        }
        return count;
    }

    public static int getStandingTeamCount() {
        return TeamManager.get().teamData.getTotalStandingTeamCount();
    }

    public static boolean isPlayerLeader(UUID playerUUID) {
        TeamManager teamManager = TeamManager.get();
        GamePlayer gamePlayer = teamManager.teamData.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) {
            return false;
        }
        GameTeam gameTeam = gamePlayer.getTeam();
        return gameTeam.isLeader(playerUUID);
    }

    /**
     * 找到第一个未满员队伍
     * @return 可用的队伍，如无则返回 -1
     */
    public static int findNotFullTeamId() {
        TeamManager teamManager = TeamManager.get();
        if (teamManager.teamData.getTotalPlayerCount() >= teamManager.teamConfig.playerLimit) {
            return -1;
        }

        // 寻找已有的未满员队伍
        List<Integer> idList = new ArrayList<>();
        for (GameTeam team : teamManager.teamData.getGameTeamsList()) {
            if (team.getTeamMembers().size() < teamManager.teamConfig.teamSize) {
                idList.add(team.getGameTeamId());
            }
        }
        Collections.shuffle(idList);
        if (idList.isEmpty()) {
            return -1;
        }
        return idList.get(0);
    }

    /**
     * 判断是否有足够队伍开始游戏
     */
    public static boolean hasEnoughPlayerTeamToStart() {
        return hasEnoughPlayerToStart() && hasEnoughTeamToStart();
    }
    public static boolean hasEnoughPlayerToStart() {
        TeamManager teamManager = TeamManager.get();
        int totalPlayerAndBots = teamManager.getTotalMembers();
        int minTeam = GameManager.get().getRequiredGameTeam();
        return totalPlayerAndBots >= minTeam // 真人玩家满足最小单人队限制
                || teamManager.teamConfig.aiEnemy; // TODO 人机填充
    }
    public static boolean hasEnoughTeamToStart() {
        TeamManager teamManager = TeamManager.get();
        if (!GameManager.get().getGameEntry().allowRemainingBot) { // 不允许剩余人机打架 -> 开局不能直接只剩人机队
            List<GameTeam> gameTeams = teamManager.getGameTeams();
            for (GameTeam gameTeam : gameTeams) {
                if (!gameTeam.onlyRemainBot()) {
                    return true;
                }
            }
            return false;
        } else { // 允许人机打架
            int totalPlayerTeam = getNonBotTeamCount();
            int minTeam = GameManager.get().getRequiredGameTeam();
            return totalPlayerTeam >= minTeam // 满足最小队伍限制
                    || teamManager.teamConfig.aiEnemy; // TODO 人机填充
        }
    }

    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 构建原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     * @param hideName 是否向其他队伍隐藏名称
     */
    public static void buildVanillaTeamForAllGameTeams(@NotNull ServerLevel serverLevel, boolean hideName) {
        TeamManager teamManager = TeamManager.get();

        try {
            Scoreboard scoreboard = serverLevel.getScoreboard();
            for (GameTeam gameTeam : teamManager.getGameTeams()) {
                PlayerTeam vanillaTeam = getClearedVanillaTeam(scoreboard, hideName, gameTeam);
                for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) { // 原版队伍没有队长，直接遍历
                    ServerPlayer memberPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (memberPlayer == null) {
                        BattleRoyale.LOGGER.warn("Failed to get GamePlayer[{}][{}]{}, skipped build vanilla team", gamePlayer.getGameTeamId(), gamePlayer.getGameSingleId(), gamePlayer.getPlayerName());
                        continue;
                    }
                    String playerName = memberPlayer.getName().getString();
//                    // 离开队伍
//                    scoreboard.removePlayerFromTeam(playerName);
                    // 加入队伍（原版已经处理了离开队伍）
                    scoreboard.addPlayerToTeam(playerName, vanillaTeam);
                }
            }
            BattleRoyale.LOGGER.debug("TeamManager finished build vanilla team");
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error in TeamUtils::buildVanillaTeamForAllGameTeams, in build vanilla team: {}", e.getMessage());
        }
    }

    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 退出原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     */
    public static void clearVanillaTeam(@NotNull ServerLevel serverLevel) {
        TeamManager teamManager = TeamManager.get();
        Scoreboard scoreboard = serverLevel.getScoreboard();
        for (GamePlayer gamePlayer : teamManager.getGamePlayers()) {
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player == null) {
                BattleRoyale.LOGGER.warn("Failed to get GamePlayer[{}][{}]{}, skipped clear vanilla team", gamePlayer.getGameTeamId(), gamePlayer.getGameSingleId(), gamePlayer.getPlayerName());
                continue;
            }
            String playerName = player.getName().getString();
            scoreboard.removePlayerFromTeam(playerName);
        }
    }

    public static @NotNull PlayerTeam getOrCreateVanillaTeam(Scoreboard scoreboard, boolean hideName, GameTeam gameTeam) {
        String vanillaTeamName = gameTeam.getVanillaTeamName();

        PlayerTeam existingTeam = scoreboard.getPlayerTeam(vanillaTeamName);
        return Objects.requireNonNullElseGet(existingTeam, () -> getClearedVanillaTeam(scoreboard, hideName, gameTeam));
    }

    public static @NotNull PlayerTeam getClearedVanillaTeam(Scoreboard scoreboard, boolean hideName, GameTeam gameTeam) {
        String vanillaTeamName = gameTeam.getVanillaTeamName();

        // 移除同名Vanilla队伍
        PlayerTeam existingTeam = scoreboard.getPlayerTeam(vanillaTeamName);
        if (existingTeam != null) {
            scoreboard.removePlayerTeam(existingTeam);
            BattleRoyale.LOGGER.debug("Removed vanilla team {}", vanillaTeamName);
        }

        // 创建Vanilla队伍
        PlayerTeam vanillaTeam = scoreboard.getPlayerTeam(vanillaTeamName);
        if (vanillaTeam == null) {
            vanillaTeam = scoreboard.addPlayerTeam(vanillaTeamName);
        }

        // 友伤由模组监听的事件处理免伤
        vanillaTeam.setAllowFriendlyFire(true);
        if (hideName) { // 对其他队伍隐藏名称
            vanillaTeam.setNameTagVisibility(Team.Visibility.HIDE_FOR_OTHER_TEAMS);
        } else {
            vanillaTeam.setNameTagVisibility(Team.Visibility.ALWAYS);
        }

        // 队伍颜色取与GameTeam最近的（原版api限制）
        vanillaTeam.setColor(ColorUtils.getClosestChatFormatting(gameTeam.getGameTeamColor()));
        return vanillaTeam;
    }
}
