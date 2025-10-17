package xiao.battleroyale.event.handler.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.event.EventRegistry;

public class LoopEventHandler implements IEventHandler {

    private LoopEventHandler() {}

    private static class LoopEventHandlerHolder {
        private static final LoopEventHandler INSTANCE = new LoopEventHandler();
    }

    public static LoopEventHandler get() {
        return LoopEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "LoopEventHandler";
    }

    public static void register() {
        EventRegistry.register(get(), EventType.SERVER_TICK_EVENT);
    }

    public static void unregister() {
        EventRegistry.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            if (!GameManager.get().isInGame()) {
                unregister();
            }
            GameManager.get().onGameTick();
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}