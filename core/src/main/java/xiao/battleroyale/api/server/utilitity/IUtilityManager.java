package xiao.battleroyale.api.server.utilitity;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.server.IServerSubManager;

public interface IUtilityManager extends IServerSubManager {

    @NotNull ISurvivalLobbyManager getSurvivalLobby();
}
