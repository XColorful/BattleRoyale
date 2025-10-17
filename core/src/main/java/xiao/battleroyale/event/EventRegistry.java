package xiao.battleroyale.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.common.game.stats.GamePlayerStats;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.event.loot.LootGenerateEventsHandler;

public class EventRegistry implements ICustomEventRegister {

    private static IEventRegister eventRegister;

    public static void initialize(IEventRegister eventRegister) {
        if (EventRegistry.eventRegister == null) {
            EventRegistry.eventRegister = eventRegister;
        }
    }

    // -------- Forge & NeoForge事件 --------
    public static boolean register(IEventHandler eventHandler, EventType eventType) {
        register(eventHandler, eventType, EventPriority.NORMAL, false);
        return true;
    }
    public static boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        if (eventRegister == null) {
            throw new IllegalStateException("Event register has not been initialized. Call init() first.");
        }
        eventRegister.register(eventHandler, eventType, priority, receiveCanceled);
        return true;
    }
    public static boolean unregister(IEventHandler eventHandler, EventType eventType) {
        unregister(eventHandler, eventType, EventPriority.NORMAL, false);
        return true;
    }
    public static boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        if (eventRegister == null) {
            throw new IllegalStateException("Event register has not been initialized. Call init() first.");
        }
        eventRegister.unregister(eventHandler, eventType, priority, receiveCanceled);
        return true;
    }

    // -------- 自定义事件 --------

    @Override
    public boolean register(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return switch (customEventType) {
            // finish
            case GAME_COMPLETE_EVENT,
            GAME_COMPLETE_FINISH_EVENT,
            GAME_STOP_EVENT,
            GAME_STOP_FINISH_EVENT,
            SERVER_STOP_EVENT,
            SERVER_STOP_FINISH_EVENT -> GameFinishEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // game
            case GAME_PLAYER_DEATH_EVENT,
            GAME_PLAYER_DEATH_FINISH_EVENT,
            GAME_PLAYER_DOWN_EVENT,
            GAME_PLAYER_DOWN_FINISH_EVENT,
            GAME_PLAYER_REVIVE_EVENT,
            GAME_PLAYER_REVIVE_FINISH_EVENT,
            GAME_SPECTATE_EVENT -> GameGameEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // spawn
            case GAME_LOBBY_TELEPORT_EVENT,
            GAME_LOBBY_TELEPORT_FINISH_EVENT -> GameSpawnEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // starter
            case GAME_INIT_EVENT,
            GAME_INIT_FINISH_EVENT,
            GAME_LOAD_EVENT,
            GAME_LOAD_FINISH_EVENT,
            GAME_START_EVENT,
            GAME_START_FINISH_EVENT -> GameStarterEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // team
            case INVITE_PLAYER_EVENT,
            INVITE_PLAYER_COMPLETE_EVENT,
            REQUEST_PLAYER_EVENT,
            REQUEST_PLAYER_COMPLETE_EVENT -> GameTeamEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // tick
            case GAME_LOOT_BFS_EVENT,
            GAME_LOOT_BFS_FINISH_EVENT,
            GAME_LOOT_EVENT,
            GAME_LOOT_FINISH_EVENT,
            GAME_TICK_EVENT,
            GAME_TICK_FINISH_EVENT,
            ZONE_TICK_EVENT,
            ZONE_TICK_FINISH_EVENT -> GameTickEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // zone
            case AIRDROP_EVENT,
            CUSTOM_ZONE_EVENT,
            ZONE_COMPLETE_EVENT,
            ZONE_CREATED_EVENT -> GameZoneEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // generate
            case CUSTOM_GENERATE_EVENT -> LootGenerateEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.debug("Attempted to register handler for unassigned CustomEventType: {}. Registration aborted.", customEventType);
                yield false;
            }
        };
    }

    @Override
    public boolean unregister(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return switch (customEventType) {
            // finish
            case GAME_COMPLETE_EVENT,
                 GAME_COMPLETE_FINISH_EVENT,
                 GAME_STOP_EVENT,
                 GAME_STOP_FINISH_EVENT,
                 SERVER_STOP_EVENT,
                 SERVER_STOP_FINISH_EVENT -> GameFinishEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // game
            case GAME_PLAYER_DEATH_EVENT,
                 GAME_PLAYER_DEATH_FINISH_EVENT,
                 GAME_PLAYER_DOWN_EVENT,
                 GAME_PLAYER_DOWN_FINISH_EVENT,
                 GAME_PLAYER_REVIVE_EVENT,
                 GAME_PLAYER_REVIVE_FINISH_EVENT,
                 GAME_SPECTATE_EVENT -> GameGameEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // spawn
            case GAME_LOBBY_TELEPORT_EVENT,
                 GAME_LOBBY_TELEPORT_FINISH_EVENT -> GameSpawnEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // starter
            case GAME_INIT_EVENT,
                 GAME_INIT_FINISH_EVENT,
                 GAME_LOAD_EVENT,
                 GAME_LOAD_FINISH_EVENT,
                 GAME_START_EVENT,
                 GAME_START_FINISH_EVENT -> GameStarterEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // team
            case INVITE_PLAYER_EVENT,
                 INVITE_PLAYER_COMPLETE_EVENT,
                 REQUEST_PLAYER_EVENT,
                 REQUEST_PLAYER_COMPLETE_EVENT -> GameTeamEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // tick
            case GAME_LOOT_BFS_EVENT,
                 GAME_LOOT_BFS_FINISH_EVENT,
                 GAME_LOOT_EVENT,
                 GAME_LOOT_FINISH_EVENT,
                 GAME_TICK_EVENT,
                 GAME_TICK_FINISH_EVENT,
                 ZONE_TICK_EVENT,
                 ZONE_TICK_FINISH_EVENT -> GameTickEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // zone
            case AIRDROP_EVENT,
                 CUSTOM_ZONE_EVENT,
                 ZONE_COMPLETE_EVENT,
                 ZONE_CREATED_EVENT -> GameZoneEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // generate
            case CUSTOM_GENERATE_EVENT -> LootGenerateEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.debug("Attempted to unregister handler for unassigned CustomEventType: {}. Unregistration aborted.", customEventType);
                yield false;
            }
        };
    }
}