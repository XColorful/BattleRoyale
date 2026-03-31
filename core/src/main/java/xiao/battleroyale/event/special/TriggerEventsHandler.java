package xiao.battleroyale.event.special;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class TriggerEventsHandler extends AbstractEventHandler {

    private static class TriggerEventsHandlerHolder {
        private static final TriggerEventsHandler INSTANCE = new TriggerEventsHandler();
    }

    public static TriggerEventsHandler get() {
        return TriggerEventsHandlerHolder.INSTANCE;
    }

    private TriggerEventsHandler() {
        super(CustomEventType.TRIGGER_EVENT);
    }
}