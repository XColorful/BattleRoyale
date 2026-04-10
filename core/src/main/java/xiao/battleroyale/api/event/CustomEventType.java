package xiao.battleroyale.api.event;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.client.render.SpecialZoneRenderEvent;
import xiao.battleroyale.api.event.game.finish.*;
import xiao.battleroyale.api.event.game.game.*;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportEvent;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportFinishEvent;
import xiao.battleroyale.api.event.game.starter.*;
import xiao.battleroyale.api.event.game.team.InvitePlayerCompleteEvent;
import xiao.battleroyale.api.event.game.team.InvitePlayerEvent;
import xiao.battleroyale.api.event.game.team.RequestPlayerCompleteEvent;
import xiao.battleroyale.api.event.game.team.RequestPlayerEvent;
import xiao.battleroyale.api.event.game.tick.*;
import xiao.battleroyale.api.event.game.zone.*;
import xiao.battleroyale.api.event.loot.generate.CustomGenerateEvent;
import xiao.battleroyale.api.event.server.utility.SurvivalLobbyTeleportEvent;
import xiao.battleroyale.api.event.server.utility.SurvivalLobbyTeleportFinishEvent;
import xiao.battleroyale.api.event.special.RegisterManagerEvent;
import xiao.battleroyale.api.event.special.TriggerEvent;

import java.util.HashMap;
import java.util.Map;

public enum CustomEventType {
    // finish
    GAME_COMPLETE_EVENT(GameCompleteEvent.class),
    GAME_COMPLETE_FINISH_EVENT(GameCompleteFinishEvent.class),
    GAME_STOP_EVENT(GameStopEvent.class),
    GAME_STOP_FINISH_EVENT(GameStopFinishEvent.class),
    SERVER_STOP_EVENT(ServerStopEvent.class),
    SERVER_STOP_FINISH_EVENT(ServerStopFinishEvent.class),
    // game
    GAME_PLAYER_DAMAGE_EVENT(GamePlayerDamageEvent.class),
    GAME_PLAYER_DAMAGE_FINISH_EVENT(GamePlayerDamageFinishEvent.class),
    GAME_PLAYER_DEATH_EVENT(GamePlayerDeathEvent.class),
    GAME_PLAYER_DEATH_FINISH_EVENT(GamePlayerDeathFinishEvent.class),
    GAME_PLAYER_DOWN_EVENT(GamePlayerDownEvent.class),
    GAME_PLAYER_DOWN_FINISH_EVENT(GamePlayerDownFinishEvent.class),
    GAME_PLAYER_REVIVE_EVENT(GamePlayerReviveEvent.class),
    GAME_PLAYER_REVIVE_FINISH_EVENT(GamePlayerReviveFinishEvent.class),
    GAME_SPECTATE_EVENT(GameSpectateEvent.class),
    // spawn
    GAME_LOBBY_TELEPORT_EVENT(GameLobbyTeleportEvent.class),
    GAME_LOBBY_TELEPORT_FINISH_EVENT(GameLobbyTeleportFinishEvent.class),
    // starter
    GAME_INIT_EVENT(GameInitEvent.class),
    GAME_INIT_FINISH_EVENT(GameInitFinishEvent.class),
    GAME_LOAD_EVENT(GameLoadEvent.class),
    GAME_LOAD_FINISH_EVENT(GameLoadFinishEvent.class),
    GAME_START_EVENT(GameStartEvent.class),
    GAME_START_FINISH_EVENT(GameStartFinishEvent.class),
    // team
    INVITE_PLAYER_EVENT(InvitePlayerEvent.class),
    INVITE_PLAYER_COMPLETE_EVENT(InvitePlayerCompleteEvent.class),
    REQUEST_PLAYER_EVENT(RequestPlayerEvent.class),
    REQUEST_PLAYER_COMPLETE_EVENT(RequestPlayerCompleteEvent.class),
    // tick
    GAME_LOOT_BFS_EVENT(GameLootBfsEvent.class),
    GAME_LOOT_BFS_FINISH_EVENT(GameLootBfsFinishEvent.class),
    GAME_LOOT_EVENT(GameLootEvent.class),
    GAME_LOOT_FINISH_EVENT(GameLootFinishEvent.class),
    GAME_TICK_EVENT(GameTickEvent.class),
    GAME_TICK_FINISH_EVENT(GameTickFinishEvent.class),
    ZONE_TICK_EVENT(ZoneTickEvent.class),
    ZONE_TICK_FINISH_EVENT(ZoneTickFinishEvent.class),
    // zone
    ZONE_CREATED_EVENT(ZoneCreatedEvent.class),
    ZONE_COMPLETE_EVENT(ZoneCompleteEvent.class),
    CUSTOM_ZONE_EVENT(CustomZoneEvent.class),
    AIRDROP_EVENT(AirdropEvent.class),
    ENTITY_EVENT(EntityEvent.class),
    // generate
    CUSTOM_GENERATE_EVENT(CustomGenerateEvent.class),
    // client
    SPECIAL_ZONE_RENDER_EVENT(SpecialZoneRenderEvent.class),
    // utility
    SURVIVAL_LOBBY_TELEPORT_EVENT(SurvivalLobbyTeleportEvent.class),
    SURVIVAL_LOBBY_TELEPORT_FINISH_EVENT(SurvivalLobbyTeleportFinishEvent.class),
    // special
    REGISTER_MANAGER_EVENT(RegisterManagerEvent.class),
    TRIGGER_EVENT(TriggerEvent.class),
    // custom
    CUSTOM_EVENT(null);

    public final Class<? extends ICustomEvent> eventClass;
    CustomEventType(Class<? extends ICustomEvent> eventClass) {
        this.eventClass = eventClass;
    }
    public @Nullable Class<? extends ICustomEvent> getEventClass() {
        return eventClass;
    }

    private static final Map<String, CustomEventType> CUSTOM_EVENT_TYPES = new HashMap<>();

    static {
        for (CustomEventType type : values()) {
            CUSTOM_EVENT_TYPES.put(type.name(), type);
        }
    }

    public static @Nullable CustomEventType fromString(String name) {
        if (name == null) return null;
        return CUSTOM_EVENT_TYPES.get(name);
    }

    public String getName() {
        return this.name();
    }

    public static final SuggestionProvider<CommandSourceStack> CUSTOM_EVENT_TYPE_SUGGESTS = (context, builder) ->
            SharedSuggestionProvider.suggest(new String[]{
                    // finish
                    GAME_COMPLETE_EVENT.getName(),
                    GAME_COMPLETE_FINISH_EVENT.getName(),
                    GAME_STOP_EVENT.getName(),
                    GAME_STOP_FINISH_EVENT.getName(),
                    // game
//                    SERVER_STOP_EVENT.getName(),
//                    SERVER_STOP_FINISH_EVENT.getName(),
                    GAME_PLAYER_DAMAGE_EVENT.getName(),
                    GAME_PLAYER_DAMAGE_FINISH_EVENT.getName(),
                    GAME_PLAYER_DEATH_EVENT.getName(),
                    GAME_PLAYER_DEATH_FINISH_EVENT.getName(),
                    GAME_PLAYER_DOWN_EVENT.getName(),
                    GAME_PLAYER_DOWN_FINISH_EVENT.getName(),
                    GAME_PLAYER_REVIVE_EVENT.getName(),
                    GAME_PLAYER_REVIVE_FINISH_EVENT.getName(),
                    GAME_SPECTATE_EVENT.getName(),
                    // spawn
                    GAME_LOBBY_TELEPORT_EVENT.getName(),
                    GAME_LOBBY_TELEPORT_FINISH_EVENT.getName(),
                    // starter
                    GAME_INIT_EVENT.getName(),
                    GAME_INIT_FINISH_EVENT.getName(),
                    GAME_LOAD_EVENT.getName(),
                    GAME_LOAD_FINISH_EVENT.getName(),
                    GAME_START_EVENT.getName(),
                    GAME_START_FINISH_EVENT.getName(),
                    // team
                    INVITE_PLAYER_EVENT.getName(),
                    INVITE_PLAYER_COMPLETE_EVENT.getName(),
                    REQUEST_PLAYER_EVENT.getName(),
                    REQUEST_PLAYER_COMPLETE_EVENT.getName(),
                    // tick
                    GAME_LOOT_BFS_EVENT.getName(),
                    GAME_LOOT_BFS_FINISH_EVENT.getName(),
                    GAME_LOOT_EVENT.getName(),
                    GAME_LOOT_FINISH_EVENT.getName(),
                    GAME_TICK_EVENT.getName(),
                    GAME_TICK_FINISH_EVENT.getName(),
                    ZONE_TICK_EVENT.getName(),
                    ZONE_TICK_FINISH_EVENT.getName(),
                    // zone
                    ZONE_CREATED_EVENT.getName(),
                    ZONE_COMPLETE_EVENT.getName(),
                    CUSTOM_ZONE_EVENT.getName(),
                    AIRDROP_EVENT.getName(),
                    ENTITY_EVENT.getName(),
                    // generate
                    CUSTOM_GENERATE_EVENT.getName(),
                    // client
//                    SPECIAL_ZONE_RENDER_EVENT.getName(),
                    // utility
                    SURVIVAL_LOBBY_TELEPORT_EVENT.getName(),
                    SURVIVAL_LOBBY_TELEPORT_FINISH_EVENT.getName(),
                    // register
                    REGISTER_MANAGER_EVENT.getName(),
                    TRIGGER_EVENT.getName()
                    // custom
//                    CUSTOM_EVENT.getName()
            }, builder);
}
