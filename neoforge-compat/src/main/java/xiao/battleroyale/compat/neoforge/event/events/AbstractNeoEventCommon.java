package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.util.ClassUtils.ArraySet;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractNeoEventCommon {

    private final Object lock = new Object();

    protected final ArraySet<IEventHandler> eventHandlers = new ArraySet<>(); // 先处理的事件
    protected final ArraySet<IEventHandler> statsEventHandlers = new ArraySet<>(); // 接收canceled事件
    protected final EventType eventType;
    protected volatile boolean isDispatching = false; // 标志位，指示当前是否处于事件分发循环中
    protected Queue<PendingOperation> pendingOperations = new LinkedList<>();
    protected record PendingOperation(IEventHandler eventHandler, boolean receivedCanceled, boolean isRegistration) {}

    public AbstractNeoEventCommon(EventType eventType) {
        this.eventType = eventType;
    }

    protected final boolean addEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, receivedCanceled, true));
                return !receivedCanceled ? !eventHandlers.contains(eventHandler) : !statsEventHandlers.contains(eventHandler);
            }
            return addEventHandlerInternal(eventHandler, receivedCanceled);
        }
    }
    protected final boolean addEventHandlerInternal(IEventHandler eventHandler, boolean receivedCanceled) {
        boolean added;
        if (!receivedCanceled) {
            added = eventHandlers.add(eventHandler);
            if (added && eventHandlers.size() == 1) {
                registerToNeo();
            }
        } else {
            added = statsEventHandlers.add(eventHandler);
            if (added && statsEventHandlers.size() == 1) {
                registerToNeo();
            }
        }
        return added;
    }

    public boolean removeEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, receivedCanceled, false));
                return !receivedCanceled ? eventHandlers.contains(eventHandler) : statsEventHandlers.contains(eventHandler);
            }
            return removeEventHandlerInternal(eventHandler, receivedCanceled);
        }
    }
    protected final boolean removeEventHandlerInternal(IEventHandler eventHandler, boolean receivedCanceled) {
        boolean removed;
        if (!receivedCanceled) {
            removed = eventHandlers.remove(eventHandler);
        } else {
            removed = statsEventHandlers.remove(eventHandler);
        }
        if (removed && eventHandlers.isEmpty() && statsEventHandlers.isEmpty()) {
            unregisterToNeo();
        }
        return removed;
    }

    protected abstract void registerToNeo();
    protected abstract void unregisterToNeo();

    protected NeoEvent getNeoEventType(Event event) {
        return new NeoEvent(event);
    }

    protected void onEvent(Event event) {
        NeoEvent neoEvent = getNeoEventType(event);

        boolean isNested;
        synchronized (lock) {
            isNested = isDispatching;
            isDispatching = true;
        }

        try {
            int handlerSize = eventHandlers.size();
            for (int i = 0; i < handlerSize; i++) {
                if (neoEvent.isCanceled()) {
                    break;
                }
                eventHandlers.get(i).handleEvent(this.eventType, neoEvent);
            }

            int statsSize = statsEventHandlers.size();
            for (int i = 0; i < statsSize; i++) {
                statsEventHandlers.get(i).handleEvent(this.eventType, neoEvent);
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