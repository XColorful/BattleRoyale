package xiao.battleroyale.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventPoster;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.event.loot.LootGenerateEventsHandler;

public class EventPoster implements ICustomEventPoster {

    private static class EventPosterHolder {
        private static final EventPoster INSTANCE = new EventPoster();
    }

    public static EventPoster get() {
        return EventPosterHolder.INSTANCE;
    }

    private EventPoster() {}

    public static boolean postEvent(ICustomEvent customEvent) {
        return get().postCustomEvent(customEvent);
    }
    public boolean postCustomEvent(ICustomEvent customEvent) {
        CustomEventType customEventType = customEvent.getEventType();
        switch (customEventType) {
            // finish
            case GAME_COMPLETE_EVENT,
            GAME_COMPLETE_FINISH_EVENT,
            GAME_STOP_EVENT,
            GAME_STOP_FINISH_EVENT,
            SERVER_STOP_EVENT,
            SERVER_STOP_FINISH_EVENT -> GameFinishEventsHandler.get().handleEvent(customEvent);
            // game
            case GAME_PLAYER_DEATH_EVENT,
            GAME_PLAYER_DEATH_FINISH_EVENT,
            GAME_PLAYER_DOWN_EVENT,
            GAME_PLAYER_DOWN_FINISH_EVENT,
            GAME_PLAYER_REVIVE_EVENT,
            GAME_PLAYER_REVIVE_FINISH_EVENT,
            GAME_SPECTATE_EVENT -> GameGameEventsHandler.get().handleEvent(customEvent);
            // spawn
            case GAME_LOBBY_TELEPORT_EVENT,
            GAME_LOBBY_TELEPORT_FINISH_EVENT -> GameSpawnEventsHandler.get().handleEvent(customEvent);
            // starter
            case GAME_INIT_EVENT,
            GAME_INIT_FINISH_EVENT,
            GAME_LOAD_EVENT,
            GAME_LOAD_FINISH_EVENT,
            GAME_START_EVENT,
            GAME_START_FINISH_EVENT -> GameStarterEventsHandler.get().handleEvent(customEvent);
            // team
            case INVITE_PLAYER_EVENT,
            INVITE_PLAYER_COMPLETE_EVENT,
            REQUEST_PLAYER_EVENT,
            REQUEST_PLAYER_COMPLETE_EVENT -> GameTeamEventsHandler.get().handleEvent(customEvent);
            // tick
            case GAME_LOOT_BFS_EVENT,
            GAME_LOOT_BFS_FINISH_EVENT,
            GAME_LOOT_EVENT,
            GAME_LOOT_FINISH_EVENT,
            GAME_TICK_EVENT,
            GAME_TICK_FINISH_EVENT,
            ZONE_TICK_EVENT,
            ZONE_TICK_FINISH_EVENT -> GameTickEventsHandler.get().handleEvent(customEvent);
            // zone
            case AIRDROP_EVENT,
            CUSTOM_ZONE_EVENT,
            ZONE_COMPLETE_EVENT,
            ZONE_CREATED_EVENT -> GameZoneEventsHandler.get().handleEvent(customEvent);
            // generate
            case CUSTOM_GENERATE_EVENT -> LootGenerateEventsHandler.get().handleEvent(customEvent);
            default -> {
                BattleRoyale.LOGGER.error("Attempted to post event {} which has no assigned Event Handler! This is a serious configuration error.", customEventType);
            }
        }
        return customEvent.isCanceled();
    }
}
