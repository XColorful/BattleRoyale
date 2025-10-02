package xiao.battleroyale.compat.neoforge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class NeoLivingDeathEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = NeoLivingDeathHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = NeoLivingDeathHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = NeoLivingDeathNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = NeoLivingDeathLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = NeoLivingDeathLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = NeoLivingDeathHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = NeoLivingDeathHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = NeoLivingDeathNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = NeoLivingDeathLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = NeoLivingDeathLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}