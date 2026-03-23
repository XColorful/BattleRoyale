package xiao.battleroyale.api.game.team;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ITeamPreManagement {

    /**
     * 游戏未开始时强制玩家加入队伍，优先加入已有队伍，其次创建新队伍
     * 适用于管理员指令或游戏初始化时的强制分配。
     * @param player 需要加入队伍的玩家
     */
    void forceJoinTeam(LivingEntity player);
    void forceJoinTeam(LivingEntity player, int teamId);

    /**
     * 游戏未开始时将玩家移出队伍
     * @return 是否移出队伍
     */
    boolean removePlayerFromTeam(@NotNull UUID playerUUID);
}
