package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameTeamEventsHandler extends AbstractEventHandler {

    private static class GameTeamEventsHandlerHolder {
        private static final GameTeamEventsHandler INSTANCE = new GameTeamEventsHandler();
    }

    public static GameTeamEventsHandler get() {
        return GameTeamEventsHandlerHolder.INSTANCE;
    }

    private GameTeamEventsHandler() {
        super(CustomEventType.INVITE_PLAYER_EVENT,
                CustomEventType.INVITE_PLAYER_COMPLETE_EVENT,
                CustomEventType.REQUEST_PLAYER_EVENT,
                CustomEventType.REQUEST_PLAYER_COMPLETE_EVENT);
    }
}