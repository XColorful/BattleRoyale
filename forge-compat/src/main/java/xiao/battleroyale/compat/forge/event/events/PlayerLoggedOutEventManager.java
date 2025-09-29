package xiao.battleroyale.compat.forge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class PlayerLoggedOutEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = PlayerLoggedOutHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = PlayerLoggedOutHigh.get().addEventHander(eventHandler,receiveCanceled);
            case NORMAL -> registered = PlayerLoggedOutNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = PlayerLoggedOutLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = PlayerLoggedOutLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = PlayerLoggedOutHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = PlayerLoggedOutHigh.get().removeEventHandler(eventHandler,receiveCanceled);
            case NORMAL -> unregistered = PlayerLoggedOutNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = PlayerLoggedOutLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = PlayerLoggedOutLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}
