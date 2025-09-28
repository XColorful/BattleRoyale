package xiao.battleroyale.compat.forge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class LivingDamageEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = LivingDamageHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = LivingDamageHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = LivingDamageNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = LivingDamageLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = LivingDamageLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = LivingDamageHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = LivingDamageHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = LivingDamageNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = LivingDamageLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = LivingDamageLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}