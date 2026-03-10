package xiao.battleroyale.compat.fabric.event.events;

import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.util.ClassUtils.ArraySet;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractEventCommon {

    private final Object lock = new Object();

    protected final ArraySet<IEventHandler> eventHandlers = new ArraySet<>(); // 先处理的事件
    protected final ArraySet<IEventHandler> statsEventHandlers = new ArraySet<>(); // 接收canceled事件
    protected final EventType eventType;
    protected volatile boolean isDispatching = false;
    protected Queue<PendingOperation> pendingOperations = new LinkedList<>();

    protected record PendingOperation(IEventHandler eventHandler, boolean receivedCanceled, boolean isRegistration) {}

    public AbstractEventCommon(EventType eventType) {
        this.eventType = eventType;
    }

    protected abstract FabricEvent getFabricEventType(Object... args);

    protected final boolean addEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, receivedCanceled, true));
                return true;
            }
            return addEventHandlerInternal(eventHandler, receivedCanceled);
        }
    }

    protected final boolean removeEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, receivedCanceled, false));
                return true;
            }
            return removeEventHandlerInternal(eventHandler, receivedCanceled);
        }
    }

    private boolean addEventHandlerInternal(IEventHandler eventHandler, boolean receivedCanceled) {
        return receivedCanceled ? statsEventHandlers.add(eventHandler) : eventHandlers.add(eventHandler);
    }

    private boolean removeEventHandlerInternal(IEventHandler eventHandler, boolean receivedCanceled) {
        return receivedCanceled ? statsEventHandlers.remove(eventHandler) : eventHandlers.remove(eventHandler);
    }

    protected void onEvent(Object... args) {
        FabricEvent fabricEvent = getFabricEventType(args);

        boolean isNested;
        synchronized (lock) {
            isNested = isDispatching;
            isDispatching = true;
        }

        try {
            int handlerSize = eventHandlers.size();
            for (int i = 0; i < handlerSize; i++) {
                if (fabricEvent.isCanceled()) {
                    break;
                }
                eventHandlers.get(i).handleEvent(this.eventType, fabricEvent);
            }

            int statsSize = statsEventHandlers.size();
            for (int i = 0; i < statsSize; i++) {
                statsEventHandlers.get(i).handleEvent(this.eventType, fabricEvent);
            }
        } finally {
            if (!isNested) {
                synchronized (lock) {
                    processPendingOperations();
                    isDispatching = false;
                }
            }
        }
    }

    protected final void processPendingOperations() {
        if (pendingOperations.isEmpty()) {
            return;
        }

        PendingOperation op;
        while ((op = pendingOperations.poll()) != null) {
            if (op.isRegistration) {
                addEventHandlerInternal(op.eventHandler, op.receivedCanceled);
            } else {
                removeEventHandlerInternal(op.eventHandler, op.receivedCanceled);
            }
        }
    }
}