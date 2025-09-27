package xiao.battleroyale.compat.forge.event.event;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.util.ClassUtils;

public abstract class AbstractEventCommon {

    protected final ClassUtils.ArraySet<IEventHandler> eventHandlers = new ClassUtils.ArraySet<>(); // 先处理的事件
    protected final ClassUtils.ArraySet<IEventHandler> statsEventHandlers = new ClassUtils.ArraySet<>(); // 接收canceled事件
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

    protected void onEvent(Event event) {
        ForgeEvent forgeEvent = new ForgeEvent(event);
        for (IEventHandler handler : eventHandlers) {
            if (forgeEvent.isCanceled()) {
                break;
            }
            handler.handleEvent(EventType.SERVER_TICK_EVENT, forgeEvent);
        }

        for (IEventHandler handler : statsEventHandlers) {
            handler.handleEvent(EventType.SERVER_TICK_EVENT, forgeEvent);
        }
    }
}
