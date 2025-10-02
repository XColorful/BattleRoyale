package xiao.battleroyale.compat.neoforge.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.compat.neoforge.event.events.*;

public class NeoEventRegister implements IEventRegister {

    @Override
    public boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (eventType) {
            case SERVER_TICK_EVENT -> registered = NeoServerTickEventManager.register(eventHandler, priority, receiveCanceled);
            case CLIENT_TICK_EVENT -> registered = NeoClientTickEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> registered = NeoLivingDamageEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> registered = NeoLivingDeathEventManager.register(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_IN_EVENT -> registered = NeoPlayerLoggedInEventManager.register(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_OUT_EVENT -> registered = NeoPlayerLoggedOutEventManager.register(eventHandler, priority, receiveCanceled);
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
            case SERVER_TICK_EVENT -> unregistered = NeoServerTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case CLIENT_TICK_EVENT -> unregistered = NeoClientTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> unregistered = NeoLivingDamageEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> unregistered = NeoLivingDeathEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_IN_EVENT -> unregistered = NeoPlayerLoggedInEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_OUT_EVENT -> unregistered = NeoPlayerLoggedOutEventManager.unregister(eventHandler, priority, receiveCanceled);
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