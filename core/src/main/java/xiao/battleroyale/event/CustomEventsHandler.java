package xiao.battleroyale.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.util.ClassUtils.ArraySet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomEventsHandler {

    private static class CustomEventsHandlerHolder {
        private static final CustomEventsHandler INSTANCE = new CustomEventsHandler();
    }

    public static CustomEventsHandler get() {
        return CustomEventsHandlerHolder.INSTANCE;
    }

    private CustomEventsHandler() {}

    // 用 ConcurrentHashMap 保证获取 Handler 时的线程安全（无须全局锁）
    private final Map<Class<? extends ICustomEvent>, ClassHandler> eventDispatchers = new ConcurrentHashMap<>();

    // 注册事件处理器
    protected <T extends ICustomEvent> boolean registerHandler(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receivedCanceled) {
        // computeIfAbsent 确保每个 Class 对应的 ClassHandler 是单例的
        return eventDispatchers.computeIfAbsent(eventClass, k -> new ClassHandler())
                .registerHandler(eventHandler, priority, receivedCanceled);
    }
    // 取消注册事件处理器
    protected <T extends ICustomEvent> boolean unregisterHandler(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receivedCanceled) {
        ClassHandler handler = eventDispatchers.get(eventClass);
        if (handler != null) {
            return handler.unregisterHandler(eventHandler, priority, receivedCanceled);
        }
        return false;
    }

    // 事件发布入口
    protected void handleEvent(ICustomEvent customEvent) {
        ClassHandler handler = eventDispatchers.get(customEvent.getCustomEventClass());
        if (handler != null) {
            handler.dispatch(customEvent);
        } else {
            BattleRoyale.LOGGER.debug("CustomEvent {} does not have event handler", customEvent.getCustomEventClass());
        }
    }

    private static class ClassHandler {
        private final AbstractEventHandler.EventHandlerContainer container = new AbstractEventHandler.EventHandlerContainer();
        private final Object lock = new Object();
        private final Queue<PendingOperation> pendingOperations = new LinkedList<>();
        private volatile boolean isDispatching = false;

        private record PendingOperation(ICustomEventHandler eventHandler, EventPriority priority, boolean receivedCanceled, boolean isRegistration) {}

        // 注册事件处理器
        public boolean registerHandler(ICustomEventHandler handler, EventPriority priority, boolean receivedCanceled) {
            synchronized (lock) {
                if (isDispatching) {
                    pendingOperations.add(new PendingOperation(handler, priority, receivedCanceled, true));
                    return !receivedCanceled ? !container.eventHandlers.contains(handler) : !container.statsEventHandlers.contains(handler);
                }
                return registerHandlerInternal(handler, priority, receivedCanceled);
            }
        }
        private boolean registerHandlerInternal(ICustomEventHandler handler, EventPriority priority, boolean receivedCanceled) {
            return receivedCanceled ? container.statsEventHandlers.add(handler, priority) : container.eventHandlers.add(handler, priority);
        }
        // 取消注册事件处理器
        public boolean unregisterHandler(ICustomEventHandler handler, EventPriority priority, boolean receivedCanceled) {
            synchronized (lock) {
                if (isDispatching) {
                    pendingOperations.add(new PendingOperation(handler, priority, receivedCanceled, false));
                    return !receivedCanceled ? container.eventHandlers.contains(handler) : container.statsEventHandlers.contains(handler);
                }
                return unregisterHandlerInternal(handler, priority, receivedCanceled);
            }
        }
        private boolean unregisterHandlerInternal(ICustomEventHandler handler, EventPriority priority, boolean receivedCanceled) {
            return receivedCanceled ? container.statsEventHandlers.remove(handler, priority) : container.eventHandlers.remove(handler, priority);
        }

        public void dispatch(ICustomEvent event) {
            boolean isNested;
            synchronized (lock) {
                isNested = isDispatching;
                isDispatching = true;
            }

            try {
                for (int i = 0; i < EventPriority.values().length; i++) {
                    // 普通事件
                    ArraySet<ICustomEventHandler> regularHandlers = container.eventHandlers.getHandlersInOrder()[i];
                    for (ICustomEventHandler handler : regularHandlers) {
                        if (event.isCanceled()) { // 不接收取消的handler无法恢复isCanceled
                            break;
                        }
                        handler.handleEvent(CustomEventType.CUSTOM_EVENT, event);
                    }
                    // Stats 监听器 (接收取消)
                    ArraySet<ICustomEventHandler> statsHandlers = container.statsEventHandlers.getHandlersInOrder()[i];
                    for (ICustomEventHandler handler : statsHandlers) {
                        handler.handleEvent(CustomEventType.CUSTOM_EVENT, event);
                    }
                }
            } finally {
                if (!isNested) {
                    synchronized (lock) {
                        processQueue();
                        isDispatching = false;
                    }
                }
            }
        }

        private void processQueue() {
            if (pendingOperations.isEmpty()) return;

            PendingOperation op;
            while ((op = pendingOperations.poll()) != null) {
                if (op.isRegistration) {
                    registerHandlerInternal(op.eventHandler, op.priority, op.receivedCanceled);
                } else {
                    unregisterHandlerInternal(op.eventHandler, op.priority, op.receivedCanceled);
                }
            }
        }
    }
}