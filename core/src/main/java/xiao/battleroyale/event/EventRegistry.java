package xiao.battleroyale.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;

public class EventRegistry {

    public static boolean register(IEventHandler eventHandler, EventType eventType) {
        register(eventHandler, eventType, EventPriority.NORMAL, false);
        return true;
    }
    public static boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        BattleRoyale.getEventRegister().register(eventHandler, eventType, priority, receiveCanceled);
        return true;
    }
    public static boolean unregister(IEventHandler eventHandler, EventType eventType) {
        unregister(eventHandler, eventType, EventPriority.NORMAL, false);
        return true;
    }
    public static boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        BattleRoyale.getEventRegister().unregister(eventHandler, eventType, priority, receiveCanceled);
        return true;
    }
}