package xiao.battleroyale.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.event.client.ClientRenderEventHandler;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.event.loot.LootGenerateEventsHandler;
import xiao.battleroyale.event.special.RegisterManagerEventsHandler;
import xiao.battleroyale.event.special.TriggerEventsHandler;

public class EventRegister implements ICustomEventRegister {

    private static class EventRegisterHolder {
        private static final EventRegister INSTANCE = new EventRegister();
    }

    public static ICustomEventRegister get() {
        return EventRegisterHolder.INSTANCE;
    }

    protected EventRegister() {}

    private static IEventRegister eventRegister;

    public static void initialize(IEventRegister eventRegister) {
        if (EventRegister.eventRegister == null) {
            EventRegister.eventRegister = eventRegister;
        }
    }

    // -------- Forge & NeoForge事件 --------
    public boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        if (eventRegister == null) {
            throw new IllegalStateException("Event register has not been initialized. Call init() first.");
        }
        return eventRegister.register(eventHandler, eventType, priority, receiveCanceled);
    }
    public boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        if (eventRegister == null) {
            throw new IllegalStateException("Event register has not been initialized. Call init() first.");
        }
        return eventRegister.unregister(eventHandler, eventType, priority, receiveCanceled);
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
            case GAME_PLAYER_DAMAGE_EVENT,
                 GAME_PLAYER_DAMAGE_FINISH_EVENT,
                 GAME_PLAYER_DEATH_EVENT,
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
            case ZONE_COMPLETE_EVENT,
                 ZONE_CREATED_EVENT,
                 CUSTOM_ZONE_EVENT,
                 AIRDROP_EVENT,
                 ENTITY_EVENT -> GameZoneEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // generate
            case CUSTOM_GENERATE_EVENT -> LootGenerateEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // client
            case SPECIAL_ZONE_RENDER_EVENT -> ClientRenderEventHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            // special
            case REGISTER_MANAGER_EVENT -> RegisterManagerEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            case TRIGGER_EVENT -> TriggerEventsHandler.get().registerHandler(eventHandler, customEventType, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.warn("Attempted to register handler for unassigned CustomEventType: {}. Registration aborted.", customEventType);
                yield false;
            }
        };
    }
    @Override
    public <T extends ICustomEvent> boolean register(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receiveCanceled) {
        return CustomEventsHandler.get().registerHandler(eventHandler, eventClass, priority, receiveCanceled);
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
            case GAME_PLAYER_DAMAGE_EVENT,
                 GAME_PLAYER_DAMAGE_FINISH_EVENT,
                 GAME_PLAYER_DEATH_EVENT,
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
            case ZONE_COMPLETE_EVENT,
                 ZONE_CREATED_EVENT,
                 CUSTOM_ZONE_EVENT,
                 AIRDROP_EVENT,
                 ENTITY_EVENT -> GameZoneEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // generate
            case CUSTOM_GENERATE_EVENT -> LootGenerateEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // client
            case SPECIAL_ZONE_RENDER_EVENT -> ClientRenderEventHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            // special
            case REGISTER_MANAGER_EVENT -> RegisterManagerEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            case TRIGGER_EVENT -> TriggerEventsHandler.get().unregisterHandler(eventHandler, customEventType, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.warn("Attempted to unregister handler for unassigned CustomEventType: {}. Unregistration aborted.", customEventType);
                yield false;
            }
        };
    }
    @Override
    public <T extends ICustomEvent> boolean unregister(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receiveCanceled) {
        return CustomEventsHandler.get().unregisterHandler(eventHandler, eventClass, priority, receiveCanceled);
    }
}