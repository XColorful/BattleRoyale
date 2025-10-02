package xiao.battleroyale.compat.neoforge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class NeoPlayerLoggedInEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = NeoPlayerLoggedInHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = NeoPlayerLoggedInHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = NeoPlayerLoggedInNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = NeoPlayerLoggedInLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = NeoPlayerLoggedInLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = NeoPlayerLoggedInHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = NeoPlayerLoggedInHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = NeoPlayerLoggedInNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = NeoPlayerLoggedInLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = NeoPlayerLoggedInLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}