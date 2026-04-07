package xiao.battleroyale.event.server;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class ServerUtilityEventsHandler extends AbstractEventHandler {

    private static class ServerUtilityEventsHandlerHolder {
        private static final ServerUtilityEventsHandler INSTANCE = new ServerUtilityEventsHandler();
    }

    public static ServerUtilityEventsHandler get() {
        return ServerUtilityEventsHandlerHolder.INSTANCE;
    }

    private ServerUtilityEventsHandler() {
        super(CustomEventType.SURVIVAL_LOBBY_TELEPORT_EVENT,
                CustomEventType.SURVIVAL_LOBBY_TELEPORT_FINISH_EVENT);
    }
}
