package xiao.battleroyale.compat.forge.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.compat.forge.event.event.ServerTickEventManager;

public class ForgeEventRegister implements IEventRegister {

    @Override
    public boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        boolean registered = false;
        switch (eventType) {
            case SERVER_TICK_EVENT -> {
                registered = ServerTickEventManager.register(eventHandler, priority, receiveCanceled);
            }
        }

        if (registered) {
            BattleRoyale.LOGGER.debug("{} registered", eventHandler.getEventName());
            return true;
        } else {
            BattleRoyale.LOGGER.debug("{} already registered", eventHandler.getEventName());
            return false;
        }
    }

    @Override
    public boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        boolean unregistered = false;
        switch (eventType) {
            case SERVER_TICK_EVENT -> {
                unregistered = ServerTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            }
        }

        if (unregistered) {
            BattleRoyale.LOGGER.debug("{} unregistered", eventHandler.getEventName());
            return true;
        } else {
            BattleRoyale.LOGGER.debug("{} already unregistered", eventHandler.getEventName());
            return false;
        }
    }
}
