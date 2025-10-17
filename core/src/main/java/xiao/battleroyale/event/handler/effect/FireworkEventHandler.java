package xiao.battleroyale.event.handler.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.common.effect.firework.FireworkManager;
import xiao.battleroyale.event.EventRegister;

public class FireworkEventHandler implements IEventHandler {

    private static class FireworkEventHandlerHolder {
        private static final FireworkEventHandler INSTANCE = new FireworkEventHandler();
    }

    public static FireworkEventHandler get() {
        return FireworkEventHandlerHolder.INSTANCE;
    }

    private FireworkEventHandler() {}

    @Override public String getEventHandlerName() {
        return "FireworkEventHandler";
    }

    public static void register() {
        EventRegister.register(get(), EventType.SERVER_TICK_EVENT);
    }

    public static void unregister() {
        EventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT){
            FireworkManager.get().onTick();
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}