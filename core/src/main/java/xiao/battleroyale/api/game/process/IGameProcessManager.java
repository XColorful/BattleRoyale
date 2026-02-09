package xiao.battleroyale.api.game.process;

import xiao.battleroyale.api.game.IGameSubManager;

public interface IGameProcessManager extends IGameSubManager, IGameManagement, IGameNotification, IGameEventHelper {

    /**
     * 完整检查所有队伍情况
     * 调用此方法将检查是否有胜利队伍
     * 如果符合条件则直接结束游戏
     */
    void checkIfGameShouldEnd();
}
