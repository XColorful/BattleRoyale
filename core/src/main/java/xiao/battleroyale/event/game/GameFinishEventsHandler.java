package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameFinishEventsHandler extends AbstractEventHandler {

    private static class GameFinishEventsHandlerHolder {
        private static final GameFinishEventsHandler INSTANCE = new GameFinishEventsHandler();
    }

    public static GameFinishEventsHandler get() {
        return GameFinishEventsHandlerHolder.INSTANCE;
    }

    private GameFinishEventsHandler() {
        super(CustomEventType.GAME_COMPLETE_EVENT,
                CustomEventType.GAME_COMPLETE_FINISH_EVENT,
                CustomEventType.GAME_STOP_EVENT,
                CustomEventType.GAME_STOP_FINISH_EVENT,
                CustomEventType.SERVER_STOP_EVENT,
                CustomEventType.SERVER_STOP_FINISH_EVENT);
    }
}
