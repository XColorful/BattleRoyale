package xiao.battleroyale.compat.neoforge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class NeoPlayerLoggedOutEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = NeoPlayerLoggedOutHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = NeoPlayerLoggedOutHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = NeoPlayerLoggedOutNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = NeoPlayerLoggedOutLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = NeoPlayerLoggedOutLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = NeoPlayerLoggedOutHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = NeoPlayerLoggedOutHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = NeoPlayerLoggedOutNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = NeoPlayerLoggedOutLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = NeoPlayerLoggedOutLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}