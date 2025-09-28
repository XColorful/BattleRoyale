package xiao.battleroyale.compat.forge.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.compat.forge.event.events.LivingDamageEventManager;
import xiao.battleroyale.compat.forge.event.events.LivingDeathEventManager;
import xiao.battleroyale.compat.forge.event.events.ServerTickEventManager;

public class ForgeEventRegister implements IEventRegister {

    @Override
    public boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (eventType) {
            case SERVER_TICK_EVENT -> registered = ServerTickEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> registered = LivingDamageEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> registered = LivingDeathEventManager.register(eventHandler, priority, receiveCanceled);
        }

        if (registered) {
            BattleRoyale.LOGGER.debug("{} registered", eventHandler.getEventHandlerName());
            return true;
        } else {
            BattleRoyale.LOGGER.debug("{} already registered", eventHandler.getEventHandlerName());
            return false;
        }
    }

    @Override
    public boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (eventType) {
            case SERVER_TICK_EVENT -> unregistered = ServerTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> unregistered = LivingDamageEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> unregistered = LivingDeathEventManager.unregister(eventHandler, priority, receiveCanceled);
        }

        if (unregistered) {
            BattleRoyale.LOGGER.debug("{} unregistered", eventHandler.getEventHandlerName());
            return true;
        } else {
            BattleRoyale.LOGGER.debug("{} already unregistered", eventHandler.getEventHandlerName());
            return false;
        }
    }
}
