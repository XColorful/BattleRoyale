package xiao.battleroyale.event;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;

public class EventRegistry {

    private static IEventRegister eventRegister;

    public static void initialize(IEventRegister eventRegister) {
        EventRegistry.eventRegister = eventRegister;
    }

    public static boolean register(IEventHandler eventHandler, EventType eventType) {
        register(eventHandler, eventType, EventPriority.NORMAL, false);
        return true;
    }
    public static boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        if (eventRegister == null) {
            throw new IllegalStateException("Event registry has not been initialized. Call init() first.");
        }
        eventRegister.register(eventHandler, eventType, priority, receiveCanceled);
        return true;
    }
    public static boolean unregister(IEventHandler eventHandler, EventType eventType) {
        unregister(eventHandler, eventType, EventPriority.NORMAL, false);
        return true;
    }
    public static boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        if (eventRegister == null) {
            throw new IllegalStateException("Event registry has not been initialized. Call init() first.");
        }
        eventRegister.unregister(eventHandler, eventType, priority, receiveCanceled);
        return true;
    }
}