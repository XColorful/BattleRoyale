package xiao.battleroyale.api.game.process;

import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.IGameSubManager;

public interface IGameProcessManager extends IGameSubManager, IGameManagement, IGameNotification, IGameEventHelper {

    @Deprecated default void checkIfGameShouldEnd() {
        checkIfGameShouldEndAndFinish();
    }

    /**
     * 完整检查所有队伍情况
     * 调用此方法将检查是否有胜利队伍
     * 如果符合条件则直接结束游戏
     */
    void checkIfGameShouldEndAndFinish();

    /**
     * 直接检查胜利条件
     */
    @ApiStatus.Internal
    void finishGameIfShouldEnd(IGameManager gameManager);
}
