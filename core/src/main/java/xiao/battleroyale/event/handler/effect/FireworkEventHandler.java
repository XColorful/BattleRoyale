package xiao.battleroyale.event.handler.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;

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
            BattleRoyale.getEffectManager().getFireworkManager().onTick();
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}