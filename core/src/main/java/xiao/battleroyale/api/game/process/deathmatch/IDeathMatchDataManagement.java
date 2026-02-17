package xiao.battleroyale.api.game.process.deathmatch;

import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

public interface IDeathMatchDataManagement {

    /**
     * 计入游戏玩家击杀数，调用后不会自动结束游戏
     * 如果需要非玩家击杀也能造成贡献，请自行重载
     * @param gamePlayer 造成击杀的游戏玩家
     * @param kill 击杀数
     * @return 是否计入成功
     */
    @Deprecated
    default boolean addGamePlayerKill(GamePlayer gamePlayer, int kill) {
        return addGameTeamKill(gamePlayer.getTeam(), kill);
    }

    /**
     * 计入游戏队伍击杀数，调用后不会自动结束游戏
     * @param gameTeam 该击杀对贡献的游戏队伍
     * @param kill 击杀数
     * @return 是否计入成功
     */
    boolean addGameTeamKill(GameTeam gameTeam, int kill);

    /**
     * 将游戏玩家添加至待复活列表
     * @param gamePlayer 待复活的玩家
     * @return 是否为第一次有效添加
     */
    boolean addAndTrackRestandingGamePlayer(GamePlayer gamePlayer);
}
