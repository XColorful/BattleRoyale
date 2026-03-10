package xiao.battleroyale.compat.fabric.event;

import xiao.battleroyale.api.event.IEvent;

public class FabricEvent implements IEvent {

    private boolean canceled = false;
    private final boolean cancelable;

    public FabricEvent(boolean cancelable) {
        this.cancelable = cancelable;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        if (this.cancelable) {
            this.canceled = cancel;
        }
    }

    @Override
    public Object getEvent() {
        // Fabric 没有统一的 Event 对象，通常返回自身或核心参数
        return this;
    }
}