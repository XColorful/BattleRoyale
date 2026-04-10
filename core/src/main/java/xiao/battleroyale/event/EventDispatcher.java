package xiao.battleroyale.event;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IHandler;
import xiao.battleroyale.util.ClassUtils;

import java.util.function.BiConsumer;

/**
 * @param <T> 处理器的类型 (IEventHandler 或 ICustomEventHandler)
 * @param <E> 事件的类型 (IEvent 或 ICustomEvent)
 * @param <Y> 事件类型枚举 (EventType 或 CustomEventType)
 */
public class EventDispatcher<T extends IHandler<Y, E>, E extends IEvent, Y> {
    private final _EventHandlerContainer<T> container = new _EventHandlerContainer<>();
    private final Object lock = new Object();

    private volatile _HandlerEntry<T>[] fastPath;

    @SuppressWarnings("unchecked")
    public EventDispatcher() {
        this.fastPath = new _HandlerEntry[0];
    }

    private record _HandlerEntry<T>(T handler, boolean receivesCanceled) {}

    private void buildFastPath() {
        // 预分配数组
        int totalSize = container.eventHandlers.size() + container.statsEventHandlers.size();
        if (totalSize == 0) {
            this.fastPath = new _HandlerEntry[0];
            return;
        }

        // 按优先级顺序填入数组
        @SuppressWarnings("unchecked")
        _HandlerEntry<T>[] newPath = new _HandlerEntry[totalSize];
        int currentIndex = 0;
        for (int i = 0; i < _EventHandlerContainer.PRIORITY_ORDER.length; i++) {
            // 普通事件
            ClassUtils.ArraySet<T> regular = container.eventHandlers.getHandlersInOrder()[i];
            for (int j = 0; j < regular.size(); j++) {
                newPath[currentIndex++] = new _HandlerEntry<>(regular.get(j), false);
            }
            // Stats 监听器 (接收取消)
            ClassUtils.ArraySet<T> stats = container.statsEventHandlers.getHandlersInOrder()[i];
            for (int j = 0; j < stats.size(); j++) {
                newPath[currentIndex++] = new _HandlerEntry<>(stats.get(j), true);
            }
        }

        // 原子性替换引用（volatile 保证可见性）
        this.fastPath = newPath;
    }

    // 注册事件处理器
    public boolean registerHandler(T handler, EventPriority priority, boolean receivedCanceled) {
        synchronized (lock) {
            boolean success = receivedCanceled ? container.statsEventHandlers.add(handler, priority) : container.eventHandlers.add(handler, priority);
            if (success) buildFastPath();
            return success;
        }
    }

    // 取消注册事件处理器
    public boolean unregisterHandler(T handler, EventPriority priority, boolean receivedCanceled) {
        synchronized (lock) {
            boolean success = receivedCanceled ? container.statsEventHandlers.remove(handler, priority) : container.eventHandlers.remove(handler, priority);
            if (success) buildFastPath();
            return success;
        }
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    /**
     * 高性能分发入口
     * @param invoker 显式传递 invoker 逻辑，解决泛型擦除后无法直接调用 handleEvent 的问题
     * 外部传入固定的 Lambda (Monomorphic Call Site) 能极大辅助 JIT 进行深度内联
     */
    public void dispatch(E event, BiConsumer<T, E> invoker) {
        _HandlerEntry<T>[] handlerEntries = this.fastPath;
        for (int i = 0; i < handlerEntries.length; i++) {
            _HandlerEntry<T> entry = handlerEntries[i];
            if (event.isCanceled() && !entry.receivesCanceled) continue;
            invoker.accept(entry.handler, event);
        }
    }
}
