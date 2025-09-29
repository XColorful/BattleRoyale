package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.util.ClassUtils.ArraySet;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractEventCommon {

    private final Object lock = new Object();

    protected final ArraySet<IEventHandler> eventHandlers = new ArraySet<>(); // 先处理的事件
    protected final ArraySet<IEventHandler> statsEventHandlers = new ArraySet<>(); // 接收canceled事件
    protected final EventType eventType;
    protected volatile boolean isDispatching = false; // 标志位，指示当前是否处于事件分发循环中
    protected Queue<PendingOperation> pendingOperations = new LinkedList<>();
    protected record PendingOperation(IEventHandler eventHandler, boolean receivedCanceled, boolean isRegistration) {}

    public AbstractEventCommon(EventType eventType) {
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
                registerToForge();
            }
        } else {
            added = statsEventHandlers.add(eventHandler);
            if (added && statsEventHandlers.size() == 1) {
                registerToForge();
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
            unregisterToForge();;
        }
        return removed;
    }

    protected abstract void registerToForge();
    protected abstract void unregisterToForge();

    protected ForgeEvent getForgeEventType(Event event) {
        return new ForgeEvent(event);
    }

    protected void onEvent(Event event) {
        ForgeEvent forgeEvent = getForgeEventType(event);

        synchronized (lock) {
            isDispatching = true;
            for (IEventHandler handler : eventHandlers) {
                if (forgeEvent.isCanceled()) {
                    break;
                }
                handler.handleEvent(this.eventType, forgeEvent);
            }

            for (IEventHandler handler : statsEventHandlers) {
                handler.handleEvent(this.eventType, forgeEvent);
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
