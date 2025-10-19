package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameSpawnEventsHandler extends AbstractEventHandler {

    private static class GameSpawnEventsHandlerHolder {
        private static final GameSpawnEventsHandler INSTANCE = new GameSpawnEventsHandler();
    }

    public static GameSpawnEventsHandler get() {
        return GameSpawnEventsHandlerHolder.INSTANCE;
    }

    private GameSpawnEventsHandler() {
        super(CustomEventType.GAME_LOBBY_TELEPORT_EVENT,
                CustomEventType.GAME_LOBBY_TELEPORT_FINISH_EVENT);
    }
}