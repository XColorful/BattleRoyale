package xiao.battleroyale.compat.forge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class ClientTickEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = ClientTickHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = ClientTickHigh.get().addEventHander(eventHandler,receiveCanceled);
            case NORMAL -> registered = ClientTickNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = ClientTickLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = ClientTickLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = ClientTickHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = ClientTickHigh.get().removeEventHandler(eventHandler,receiveCanceled);
            case NORMAL -> unregistered = ClientTickNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = ClientTickLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = ClientTickLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}
