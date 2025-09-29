package xiao.battleroyale.compat.forge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class PlayerLoggedInEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = PlayerLoggedInHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = PlayerLoggedInHigh.get().addEventHander(eventHandler,receiveCanceled);
            case NORMAL -> registered = PlayerLoggedInNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = PlayerLoggedInLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = PlayerLoggedInLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = PlayerLoggedInHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = PlayerLoggedInHigh.get().removeEventHandler(eventHandler,receiveCanceled);
            case NORMAL -> unregistered = PlayerLoggedInNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = PlayerLoggedInLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = PlayerLoggedInLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}
