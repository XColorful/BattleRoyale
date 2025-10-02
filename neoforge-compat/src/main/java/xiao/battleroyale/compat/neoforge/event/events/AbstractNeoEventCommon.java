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

    public boolean addEventHander(IEventHandler eventHandler, boolean receivedCanceled) {
        synchronized (lock) {
            if (isDispatching) {
                pendingOperations.add(new PendingOperation(eventHandler, receivedCanceled, true));
                return !receivedCanceled ? !eventHandlers.contains(eventHandler) : !statsEventHandlers.contains(eventHandler);
            }
            return addEventHandlerInternal(eventHandler, receivedCanceled);
        }
    }
    protected boolean addEventHandlerInternal(IEventHandler eventHandler, boolean receivedCanceled) {
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
    protected boolean removeEventHandlerInternal(IEventHandler eventHandler, boolean receivedCanceled) {
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

        synchronized (lock) {
            isDispatching = true;
            for (IEventHandler handler : eventHandlers) {
                if (neoEvent.isCanceled()) {
                    break;
                }
                handler.handleEvent(this.eventType, neoEvent);
            }

            for (IEventHandler handler : statsEventHandlers) {
                handler.handleEvent(this.eventType, neoEvent);
            }
            isDispatching = false;

            processPendingOperations();
        }
    }

    protected void processPendingOperations() {
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