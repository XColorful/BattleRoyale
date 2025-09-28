package xiao.battleroyale.compat.forge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class LivingDeathEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = LivingDeathHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = LivingDeathHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = LivingDeathNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = LivingDeathLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = LivingDeathLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = LivingDeathHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = LivingDeathHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = LivingDeathNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = LivingDeathLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = LivingDeathLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}