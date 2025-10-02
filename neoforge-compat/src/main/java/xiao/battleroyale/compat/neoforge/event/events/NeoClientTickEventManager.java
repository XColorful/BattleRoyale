package xiao.battleroyale.compat.neoforge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class NeoClientTickEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = NeoClientTickHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = NeoClientTickHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = NeoClientTickNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = NeoClientTickLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = NeoClientTickLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = NeoClientTickHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = NeoClientTickHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = NeoClientTickNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = NeoClientTickLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = NeoClientTickLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}