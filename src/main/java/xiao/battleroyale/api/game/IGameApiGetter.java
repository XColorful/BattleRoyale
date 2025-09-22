package xiao.battleroyale.api.game;

import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;

public interface IGameApiGetter {

    IGameTeamReadApi getGameTeamReadApi();

    IGameZoneReadApi getGameZoneReadApi();
}
