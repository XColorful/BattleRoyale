package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.team.TeamTag;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.NBTUtils;
import xiao.battleroyale.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClientTeamData {

    public int teamId;
    public Color teamColor;
    public final List<TeamMemberInfo> teamMemberInfoList = new ArrayList<>();
    public boolean inTeam;

    private static final String DEFAULT_COLOR = "#000000FF"; // 读取格式用#RRGGBBAA，实际int转为AARRGGBB
    public static final int NO_TEAM = 0;
    public static final double OFFLINE = -1;
    public static final double ELIMINATED = -2;

    public ClientTeamData() {
        clear();
    }

    public void updateFromNbt(CompoundTag nbt) {
        this.teamId = nbt.getInt(TeamTag.TEAM_ID);
        this.teamColor = ColorUtils.parseColorFromString(nbt.getString(TeamTag.TEAM_COLOR));
        this.teamMemberInfoList.clear();
        CompoundTag memberTags = nbt.getCompound(TeamTag.TEAM_MEMBER);
        for (String key : memberTags.getAllKeys()) {
            CompoundTag memberTag = memberTags.getCompound(key);
            teamMemberInfoList.add(new TeamMemberInfo(
                    Integer.parseInt(key),
                    memberTag.getString(TeamTag.MEMBER_NAME),
                    memberTag.getDouble(TeamTag.MEMBER_HEALTH),
                    memberTag.getInt(TeamTag.MEMBER_BOOST)
            ));
        }
        teamMemberInfoList.sort(Comparator.comparingInt(TeamMemberInfo::playerId));

        this.inTeam = isInTeam();
    }

    private boolean isInTeam() {
        return this.teamId > 0 && !this.teamMemberInfoList.isEmpty();
    }

    public void clear() {
        this.teamId = NO_TEAM;
        this.teamColor = Color.BLACK;
        this.teamMemberInfoList.clear();
        this.inTeam = false;
    }

    @NotNull
    public static CompoundTag toNBT(@Nullable GameTeam gameTeam, @Nullable ServerLevel serverLevel) {
        int teamId = -1;
        String teamColor = DEFAULT_COLOR;
        List<TeamMemberInfo> memberInfos = new ArrayList<>();

        // team
        if (gameTeam != null) {
            teamId = gameTeam.getGameTeamId();
            teamColor = gameTeam.getGameTeamColor();

            // team member
            for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) {
                double playerHealth;
                if (gamePlayer.isEliminated()) { // 标记淘汰则优先
                    playerHealth = ELIMINATED;
                } else if (!gamePlayer.isActiveEntity() || serverLevel == null) { // 被标记为离线或无法用serverLevel查血量
                    playerHealth = OFFLINE;
                } else {
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    playerHealth = player == null ? OFFLINE : player.getHealth();
                }
                memberInfos.add(new TeamMemberInfo(
                        gamePlayer.getGameSingleId(),
                        gamePlayer.getPlayerName(),
                        playerHealth,
                        gamePlayer.getBoost()));
            }
        }

        return NBTUtils.serializeTeamToNBT(
                teamId,
                teamColor,
                memberInfos
        );
    }

    public record TeamMemberInfo(int playerId, String name, double health, int boost) {}
}
