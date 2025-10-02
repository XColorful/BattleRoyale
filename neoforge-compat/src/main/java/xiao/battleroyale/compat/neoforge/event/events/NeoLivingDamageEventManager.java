package xiao.battleroyale.compat.neoforge.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.IEventHandler;

public class NeoLivingDamageEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (priority) {
            case HIGHEST -> registered = NeoLivingDamageHighest.get().addEventHander(eventHandler, receiveCanceled);
            case HIGH -> registered = NeoLivingDamageHigh.get().addEventHander(eventHandler, receiveCanceled);
            case NORMAL -> registered = NeoLivingDamageNormal.get().addEventHander(eventHandler, receiveCanceled);
            case LOW -> registered = NeoLivingDamageLow.get().addEventHander(eventHandler, receiveCanceled);
            case LOWEST -> registered = NeoLivingDamageLowest.get().addEventHander(eventHandler, receiveCanceled);
        }
        return registered;
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (priority) {
            case HIGHEST -> unregistered = NeoLivingDamageHighest.get().removeEventHandler(eventHandler, receiveCanceled);
            case HIGH -> unregistered = NeoLivingDamageHigh.get().removeEventHandler(eventHandler, receiveCanceled);
            case NORMAL -> unregistered = NeoLivingDamageNormal.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOW -> unregistered = NeoLivingDamageLow.get().removeEventHandler(eventHandler, receiveCanceled);
            case LOWEST -> unregistered = NeoLivingDamageLowest.get().removeEventHandler(eventHandler, receiveCanceled);
        }
        return unregistered;
    }
}