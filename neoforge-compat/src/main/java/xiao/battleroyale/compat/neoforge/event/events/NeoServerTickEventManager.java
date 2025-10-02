package xiao.battleroyale.compat.neoforge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class NeoServerTickEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = NeoServerTickHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = NeoServerTickHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = NeoServerTickNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = NeoServerTickLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = NeoServerTickLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = NeoServerTickHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = NeoServerTickHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = NeoServerTickNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = NeoServerTickLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = NeoServerTickLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}