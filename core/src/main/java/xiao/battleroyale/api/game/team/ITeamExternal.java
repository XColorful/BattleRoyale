package xiao.battleroyale.api.game.team;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 玩家指令调用
 */
public interface ITeamExternal {

    /**
     * 玩家加入游戏，优先创建队伍，无法创建队伍则发送申请
     * @param player 需要加入队伍的玩家
     */
    void joinTeam(ServerPlayer player);

    /**
     * 玩家尝试创建一个指定的队伍 (已存在则改为申请)。
     * @param player 需要加入队伍的玩家
     * @param teamId 加入队伍的 teamId
     */
    void joinTeamSpecific(ServerPlayer player, int teamId);

    // 踢出队伍
    void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer);

    // 邀请玩家加入自己队伍
    void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer);
    void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer);
    void declineInvite(ServerPlayer player, ServerPlayer senderPlayer);

    // 申请玩家加入他人队伍
    void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
    void acceptRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
    void declineRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);

    /**
     * 返回玩家是否成功离队
     * 在游戏中调用该函数只淘汰不离队
     */
    boolean leaveTeam(ServerPlayer player);

    /**
     * 强制将玩家加入队伍
     */
    boolean addToTeam(@Nullable CommandSourceStack source, LivingEntity player, int teamId);

    /**
     * 强制为玩家创建队伍
     * @param players 玩家列表
     * @param targetSize 期望队伍人数上限
     * @param forceRebuild 是否强制退出原先队伍
     * @return 成功操作的玩家数量
     */
    int buildTeamForAll(@Nullable CommandSourceStack source, List<LivingEntity> players, int targetSize, boolean forceRebuild);
}
