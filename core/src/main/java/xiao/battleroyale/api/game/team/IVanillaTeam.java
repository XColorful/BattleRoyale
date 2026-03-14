package xiao.battleroyale.api.game.team;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GameTeam;

public interface IVanillaTeam {

    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 构建原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     * @param hideName 是否向其他队伍隐藏名称
     */
    boolean buildVanillaTeam(@Nullable ServerLevel serverLevel, String vanillaTeamFormat, boolean hideName, boolean allowBuildInGame);
    @Deprecated default boolean buildVanillaTeam(@Nullable ServerLevel serverLevel, boolean hideName) {
        return buildVanillaTeam(serverLevel, GameTeam.DEFAULT_VANILLA_TEAM_FORMAT, hideName, false);
    }

    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 退出原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     */
    void clearVanillaTeam(@Nullable ServerLevel serverLevel);

    /**
     * 在传入的 ServerLevel 下移除原版队伍
     * @param gameTeamOnly 是否仅移除 GameTeam 的原版队伍
     * @return 移除队伍的数量
     */
    int removeVanillaTeam(@NotNull ServerLevel serverLevel, boolean gameTeamOnly);
}
