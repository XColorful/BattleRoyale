package xiao.battleroyale.api.game.team;

import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.UUID;

public interface ITeamManager extends IGameSubManager, IGameTeamReadApi,
        ITeamExternal, ITeamManagement, ITeamPreManagement, ITeamNotification, IVanillaTeam {

    boolean shouldAutoJoin();

    void onBotGamePlayerChanged(GamePlayer gamePlayer, UUID uuid);

    /**
     * 找到第一个未满员队伍
     * @return 可用的队伍，如无则返回 -1
     */
    int findNotFullTeamId();

    boolean hasEnoughPlayerTeamToStart();
}
