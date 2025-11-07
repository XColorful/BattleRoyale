package xiao.battleroyale.api.game.team;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.common.game.team.GamePlayer;

public interface ITeamManagement {

    /**
     * 在游戏中强制淘汰玩家，不包含发送系统消息
     * 成功淘汰后发送大厅传送消息
     */
    boolean forceEliminatePlayerSilence(GamePlayer gamePlayer);

    /**
     * 在游戏中强制淘汰玩家并向队友发送消息
     */
    void forceEliminatePlayerFromTeam(LivingEntity livingEntity);
}
