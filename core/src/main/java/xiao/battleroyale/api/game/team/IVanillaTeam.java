package xiao.battleroyale.api.game.team;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface IVanillaTeam {

    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 构建原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     * @param hideName 是否向其他队伍隐藏名称
     */
    void buildVanillaTeam(@Nullable ServerLevel serverLevel, boolean hideName);

    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 退出原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     */
    void clearVanillaTeam(@Nullable ServerLevel serverLevel);
}
