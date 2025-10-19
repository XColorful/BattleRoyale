package xiao.battleroyale.event;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.util.ClassUtils.ArraySet;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/*
 * 所有EventsHandler的抽象父类 (通用部分)
 * 转发注册该类的事件, 含优先级处理
 * 接收该类事件并按监听的优先级逐个执行
 * 子类只需在构造函数里注册对应的CustomEventType即可
 */
public class AbstractEventHandler {

    private final Map<CustomEventType, EventHandlerContainer> eventDispatchers = new EnumMap<>(CustomEventType.class);

    private final Object lock = new Object();
    private volatile boolean isDispatching = false;
    private final Queue<PendingOperation> pendingOperations = new LinkedList<>();

    private record PendingOperation(ICustomEventHandler eventHandler, CustomEventType type, EventPriority priority, boolean receiveCanceled, boolean isRegistration) {}

    public AbstractEventHandler(CustomEventType... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("AbstractEventHandler must be initialized with at least one CustomEventType.");
        }

        for (CustomEventType type : types) {
            eventDispatchers.put(type, new EventHandlerContainer());
        }
    }

    // 注册事件处理器
    protected boolean registerHandler(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, customEventType, priority, receiveCanceled, true));
                EventHandlerContainer container = eventDispatchers.get(customEventType);
                if (container == null) return false;
                return !receiveCanceled ? !container.eventHandlers.contains(eventHandler) : !container.statsEventHandlers.contains(eventHandler);
            }
            return registerHandlerInternal(eventHandler, customEventType, priority, receiveCanceled);
        }
    }
    private boolean registerHandlerInternal(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receivedCanceled) {
        EventHandlerContainer container = eventDispatchers.get(customEventType);
        if (container == null) return false;

        if (!receivedCanceled) {
            return container.eventHandlers.add(eventHandler, priority);
        } else {
            return container.statsEventHandlers.add(eventHandler, priority);
        }
    }
    // 取消注册事件处理器
    protected boolean unregisterHandler(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, customEventType, priority, receiveCanceled, false));
                EventHandlerContainer container = eventDispatchers.get(customEventType);
                if (container == null) return false;
                return !receiveCanceled ? container.eventHandlers.contains(eventHandler) : container.statsEventHandlers.contains(eventHandler);
            }
            return unregisterHandlerInternal(eventHandler, customEventType, priority, receiveCanceled);
        }
    }
    private boolean unregisterHandlerInternal(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receivedCanceled) {
        EventHandlerContainer container = eventDispatchers.get(customEventType);
        if (container == null) return false;

        if (!receivedCanceled) {
            return container.eventHandlers.remove(eventHandler, priority);
        } else {
            return container.statsEventHandlers.remove(eventHandler, priority);
        }
    }

    // 事件发布入口
    protected void handleEvent(ICustomEvent customEvent) {
        CustomEventType customEventType = customEvent.getEventType();
        EventHandlerContainer container = eventDispatchers.get(customEventType);
        if (container == null) return;

        synchronized (lock) {
            isDispatching = true;

            // 循环遍历所有优先级
            for (int i = 0; i < EventHandlerContainer.PRIORITY_ORDER.length; i++) {
                // 普通事件
                ArraySet<ICustomEventHandler> regularHandlers = container.eventHandlers.getHandlersInOrder()[i];
                for (ICustomEventHandler handler : regularHandlers) {
                    if (customEvent.isCanceled()) { // 不接收取消的handler无法恢复isCanceled
                        break;
                    }
                    handler.handleEvent(customEventType, customEvent);
                }
                // Stats事件 (接收取消)
                ArraySet<ICustomEventHandler> statsHandlers = container.statsEventHandlers.getHandlersInOrder()[i];
                for (ICustomEventHandler handler : statsHandlers) {
                    handler.handleEvent(customEventType, customEvent);
                }
            }

            isDispatching = false;
            processPendingOperations();
        }
    }

    private void processPendingOperations() {
        if (pendingOperations.isEmpty()) return;

        PendingOperation op;
        while ((op = pendingOperations.poll()) != null) {
            if (op.isRegistration) {
                registerHandlerInternal(op.eventHandler, op.type(), op.priority(), op.receiveCanceled());
            } else {
                unregisterHandlerInternal(op.eventHandler, op.type(), op.priority(), op.receiveCanceled());
            }
        }
    }

    protected static class EventHandlerContainer {

        private static final EventPriority[] PRIORITY_ORDER = EventPriority.values();

        @SuppressWarnings("unchecked")
        private final ArraySet<ICustomEventHandler>[] handlers = new ArraySet[PRIORITY_ORDER.length];
        private final ArraySet<ICustomEventHandler>[] statsHandlers = new ArraySet[PRIORITY_ORDER.length];

        public final PrioritizedHandlerSet eventHandlers = new PrioritizedHandlerSet(handlers);
        public final PrioritizedHandlerSet statsEventHandlers = new PrioritizedHandlerSet(statsHandlers);

        public EventHandlerContainer() {
            for (int i = 0; i < PRIORITY_ORDER.length; i++) {
                handlers[i] = new ArraySet<>();
                statsHandlers[i] = new ArraySet<>();
            }
        }

        public boolean isEmpty() {
            return eventHandlers.size() + statsEventHandlers.size() == 0;
        }

        public static class PrioritizedHandlerSet {
            private final ArraySet<ICustomEventHandler>[] sets;
            public PrioritizedHandlerSet(ArraySet<ICustomEventHandler>[] sets) {
                this.sets = sets;
            }

            public ArraySet<ICustomEventHandler>[] getHandlersInOrder() {
                return sets;
            }
            private int getIndex(EventPriority priority) {
                return priority.ordinal();
            }
            public boolean contains(ICustomEventHandler eventHandler) {
                for (ArraySet<ICustomEventHandler> set : sets) {
                    if (set.contains(eventHandler)) {
                        return true;
                    }
                }
                return false;
            }
            public boolean add(ICustomEventHandler eventHandler, EventPriority priority) {
                if (contains(eventHandler)) {
                    return false;
                }
                int index = getIndex(priority);
                return sets[index].add(eventHandler);
            }
            public boolean remove(ICustomEventHandler eventHandler, EventPriority priority) {
                int index = getIndex(priority);
                return sets[index].remove(eventHandler);
            }
            public int size() {
                int size = 0;
                for (ArraySet<ICustomEventHandler> set : sets) {
                    size += set.size();
                }
                return size;
            }
        }
    }
}
