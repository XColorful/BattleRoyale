package xiao.battleroyale.event.game;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class GameZoneEventsHandler extends AbstractEventHandler {

    private static class GameZoneEventsHandlerHolder {
        private static final GameZoneEventsHandler INSTANCE = new GameZoneEventsHandler();
    }

    public static GameZoneEventsHandler get() {
        return GameZoneEventsHandlerHolder.INSTANCE;
    }

    private GameZoneEventsHandler() {
        super(CustomEventType.AIRDROP_EVENT,
                CustomEventType.CUSTOM_ZONE_EVENT,
                CustomEventType.ZONE_COMPLETE_EVENT,
                CustomEventType.ZONE_CREATED_EVENT);
    }
}