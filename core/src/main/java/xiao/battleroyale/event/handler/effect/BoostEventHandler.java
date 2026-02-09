package xiao.battleroyale.event.handler.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;

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
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.SERVER_TICK_EVENT);
    }

    public static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT){
            BattleRoyale.getEffectManager().getBoostManager().onTick();
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}
