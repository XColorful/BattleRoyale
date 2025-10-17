package xiao.battleroyale.event.handler.message;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.event.EventRegister;

public class MessageEventHandler implements IEventHandler {

    private MessageEventHandler() {}

    private static class MessageEventHandlerHolder {
        private static final MessageEventHandler INSTANCE = new MessageEventHandler();
    }

    public static MessageEventHandler get() {
        return MessageEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "MessageEventHandler";
    }

    public static void register() {
        EventRegister.register(get(), EventType.SERVER_TICK_EVENT);
    }

    public static void unregister() {
        EventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            MessageManager.get().tick();
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}
