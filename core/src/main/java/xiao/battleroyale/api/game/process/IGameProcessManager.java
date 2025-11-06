package xiao.battleroyale.api.game.process;

import xiao.battleroyale.api.game.IGameSubManager;

public interface IGameProcessManager extends IGameSubManager, IGameManagement, IGameNotification, IGameEventHandler {

    void checkIfGameShouldEnd();
}
