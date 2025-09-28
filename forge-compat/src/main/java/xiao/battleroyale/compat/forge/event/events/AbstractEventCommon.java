package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.util.ClassUtils.ArraySet;

public abstract class AbstractEventCommon {

    protected final ArraySet<IEventHandler> eventHandlers = new ArraySet<>(); // 先处理的事件
    protected final ArraySet<IEventHandler> statsEventHandlers = new ArraySet<>(); // 接收canceled事件
    protected final EventType eventType;

    public AbstractEventCommon(EventType eventType) {
        this.eventType = eventType;
    }

    public boolean addEventHander(IEventHandler eventHandler, boolean receivedCanceled) {
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
        for (IEventHandler handler : eventHandlers) {
            if (forgeEvent.isCanceled()) {
                break;
            }
            handler.handleEvent(this.eventType, forgeEvent);
        }

        for (IEventHandler handler : statsEventHandlers) {
            handler.handleEvent(this.eventType, forgeEvent);
        }
    }
}
