package xiao.battleroyale.api.game;

import xiao.battleroyale.api.game.lobby.IGameLobbyReadApi;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;

/**
 * GameManager单例专用
 */
public interface IGameManager extends IGameMainManager, IGameApiGetter, IGameConfigGetter,
        IGameConfigSetter, IGameStatusSetter, IGameEventReceiver {

    @Override default IGameTeamReadApi getGameTeamReadApi() {
        return getTeamManager();
    }
    @Override default IGameZoneReadApi getGameZoneReadApi() {
        return getZoneManager();
    }
    @Override default IGameLobbyReadApi getGameLobbyReadApi() {
        return getGameLobbyManager();
    }
}
