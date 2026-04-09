package xiao.battleroyale.event;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.util.ClassUtils;

public class EventDispatcher {
    private final _EventHandlerContainer container = new _EventHandlerContainer();
    private final Object lock = new Object();

    private volatile _HandlerEntry[] fastPath = new _HandlerEntry[0];

    private record _HandlerEntry(ICustomEventHandler handler, boolean receivesCanceled) {
    }

    private void buildFastPath() {
        // 预分配数组
        int totalSize = container.eventHandlers.size() + container.statsEventHandlers.size();
        if (totalSize == 0) {
            this.fastPath = new _HandlerEntry[0];
            return;
        }

        // 按优先级顺序填入数组
        _HandlerEntry[] newPath = new _HandlerEntry[totalSize];
        int currentIndex = 0;
        for (int i = 0; i < _EventHandlerContainer.PRIORITY_ORDER.length; i++) {
            // 普通事件
            ClassUtils.ArraySet<ICustomEventHandler> regular = container.eventHandlers.getHandlersInOrder()[i];
            for (int j = 0; j < regular.size(); j++) {
                newPath[currentIndex++] = new _HandlerEntry(regular.get(j), false);
            }
            // Stats 监听器 (接收取消)
            ClassUtils.ArraySet<ICustomEventHandler> stats = container.statsEventHandlers.getHandlersInOrder()[i];
            for (int j = 0; j < stats.size(); j++) {
                newPath[currentIndex++] = new _HandlerEntry(stats.get(j), true);
            }
        }

        // 原子性替换引用（volatile 保证可见性）
        this.fastPath = newPath;
    }

    // 注册事件处理器
    public boolean registerHandler(ICustomEventHandler handler, EventPriority priority, boolean receivedCanceled) {
        synchronized (lock) {
            boolean success = receivedCanceled ? container.statsEventHandlers.add(handler, priority) : container.eventHandlers.add(handler, priority);
            if (success) buildFastPath();
            return success;
        }
    }

    // 取消注册事件处理器
    public boolean unregisterHandler(ICustomEventHandler handler, EventPriority priority, boolean receivedCanceled) {
        synchronized (lock) {
            boolean success = receivedCanceled ? container.statsEventHandlers.remove(handler, priority) : container.eventHandlers.remove(handler, priority);
            if (success) buildFastPath();
            return success;
        }
    }

    public void dispatch(ICustomEvent event) {
        _HandlerEntry[] handlerEntries = this.fastPath;
        CustomEventType eventType = event.getEventType();
        for (int i = 0; i < handlerEntries.length; i++) {
            _HandlerEntry entry = handlerEntries[i];
            if (event.isCanceled() && !entry.receivesCanceled) continue;
            entry.handler.handleEvent(eventType, event);
        }
    }
}
