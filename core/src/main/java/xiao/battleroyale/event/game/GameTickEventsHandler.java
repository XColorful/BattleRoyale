package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameTickEventsHandler extends AbstractEventHandler {

    private static class GameTickEventsHandlerHolder {
        private static final GameTickEventsHandler INSTANCE = new GameTickEventsHandler();
    }

    public static GameTickEventsHandler get() {
        return GameTickEventsHandlerHolder.INSTANCE;
    }

    private GameTickEventsHandler() {
        super(CustomEventType.GAME_LOOT_BFS_EVENT,
                CustomEventType.GAME_LOOT_BFS_FINISH_EVENT,
                CustomEventType.GAME_LOOT_EVENT,
                CustomEventType.GAME_LOOT_FINISH_EVENT,
                CustomEventType.GAME_TICK_EVENT,
                CustomEventType.GAME_TICK_FINISH_EVENT,
                CustomEventType.ZONE_TICK_EVENT,
                CustomEventType.ZONE_TICK_FINISH_EVENT);
    }
}