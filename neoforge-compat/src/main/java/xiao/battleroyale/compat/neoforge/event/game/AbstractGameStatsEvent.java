package xiao.battleroyale.compat.neoforge.event.game;

import net.neoforged.bus.api.Event;
import xiao.battleroyale.api.game.IGameManager;

/**
 * 游戏统计事件，不可取消，直接继承 NeoForge Event。
 */
public abstract class AbstractGameStatsEvent extends Event {

    protected final IGameManager gameManager;

    public AbstractGameStatsEvent(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public IGameManager getGameManager() {
        return gameManager;
    }

    // isCancelable() 方法保留，用于自定义 API 的逻辑。
    // 但在 NeoForge 层面，由于未实现 ICancellableEvent，它本身是不可取消的。
    public boolean isCancelable() {
        return false;
    }
}