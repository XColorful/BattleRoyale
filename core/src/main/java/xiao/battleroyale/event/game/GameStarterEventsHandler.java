package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameStarterEventsHandler extends AbstractEventHandler {

    private static class GameStarterEventsHandlerHolder {
        private static final GameStarterEventsHandler INSTANCE = new GameStarterEventsHandler();
    }

    public static GameStarterEventsHandler get() {
        return GameStarterEventsHandlerHolder.INSTANCE;
    }

    private GameStarterEventsHandler() {
        super(CustomEventType.GAME_INIT_EVENT,
                CustomEventType.GAME_INIT_FINISH_EVENT,
                CustomEventType.GAME_LOAD_EVENT,
                CustomEventType.GAME_LOAD_FINISH_EVENT,
                CustomEventType.GAME_START_EVENT,
                CustomEventType.GAME_START_FINISH_EVENT);
    }
}