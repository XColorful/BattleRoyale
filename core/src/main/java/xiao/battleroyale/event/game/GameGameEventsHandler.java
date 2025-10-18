package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameGameEventsHandler extends AbstractEventHandler {

    private static class GameGameEventsHandlerHolder {
        private static final GameGameEventsHandler INSTANCE = new GameGameEventsHandler();
    }

    public static GameGameEventsHandler get() {
        return GameGameEventsHandlerHolder.INSTANCE;
    }

    private GameGameEventsHandler() {
        super(CustomEventType.GAME_PLAYER_DEATH_EVENT,
                CustomEventType.GAME_PLAYER_DEATH_FINISH_EVENT,
                CustomEventType.GAME_PLAYER_DOWN_EVENT,
                CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT,
                CustomEventType.GAME_PLAYER_REVIVE_EVENT,
                CustomEventType.GAME_PLAYER_REVIVE_FINISH_EVENT,
                CustomEventType.GAME_SPECTATE_EVENT);
    }
}