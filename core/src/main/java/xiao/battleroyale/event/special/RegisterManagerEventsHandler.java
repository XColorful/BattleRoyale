package xiao.battleroyale.event.special;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class RegisterManagerEventsHandler extends AbstractEventHandler {

    private static class RegisterManagerEventsHandlerHolder {
        private static final RegisterManagerEventsHandler INSTANCE = new RegisterManagerEventsHandler();
    }

    public static RegisterManagerEventsHandler get() {
        return RegisterManagerEventsHandlerHolder.INSTANCE;
    }

    private RegisterManagerEventsHandler() {
        super(CustomEventType.REGISTER_MANAGER_EVENT);
    }
}
