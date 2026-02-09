package xiao.battleroyale.common.message;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;

public class MessageEventHandler implements IEventHandler {

    private MessageEventHandler() {}

    private static class MessageEventHandlerHolder {
        private static final MessageEventHandler INSTANCE = new MessageEventHandler();
    }

    public static MessageEventHandler get() {
        return MessageEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return String.format("%s:MessageEventHandler", BattleRoyale.MOD_ID);
    }

    protected static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.SERVER_TICK_EVENT);
    }

    protected static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            MessageManager.get().tick();
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}
