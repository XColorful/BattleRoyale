package xiao.battleroyale.compat.forge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class ServerTickEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = ServerTickHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = ServerTickHigh.get().addEventHander(eventHandler,receiveCanceled);
            case NORMAL -> registered = ServerTickNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = ServerTickLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = ServerTickLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = ServerTickHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = ServerTickHigh.get().removeEventHandler(eventHandler,receiveCanceled);
            case NORMAL -> unregistered = ServerTickNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = ServerTickLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = ServerTickLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}
