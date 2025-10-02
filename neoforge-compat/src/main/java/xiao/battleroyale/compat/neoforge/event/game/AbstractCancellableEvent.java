package xiao.battleroyale.compat.neoforge.event.game;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge 版本的游戏事件可取消基类。
 * 实现了 ICancellableEvent 接口，提供了取消状态的存储和管理。
 */
public abstract class AbstractCancellableEvent extends Event implements ICancellableEvent {

    private boolean isCanceled = false;

    public AbstractCancellableEvent() {}

    @Override
    public boolean isCanceled() {
        return this.isCanceled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.isCanceled = cancel;
    }
}