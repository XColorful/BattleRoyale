package xiao.battleroyale.event.handler.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.common.effect.boost.BoostManager;
import xiao.battleroyale.event.EventRegister;

public class BoostEventHandler implements IEventHandler {

    private static class BoostEventHandlerHolder {
        private static final BoostEventHandler INSTANCE = new BoostEventHandler();
    }

    public static BoostEventHandler get() {
        return BoostEventHandlerHolder.INSTANCE;
    }

    private BoostEventHandler() {}

    @Override public String getEventHandlerName() {
        return "BoostEventHandler";
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
            BoostManager.get().onTick();
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}
