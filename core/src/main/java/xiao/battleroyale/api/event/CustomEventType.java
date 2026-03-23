package xiao.battleroyale.api.event;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum CustomEventType {
    // finish
    GAME_COMPLETE_EVENT,
    GAME_COMPLETE_FINISH_EVENT,
    GAME_STOP_EVENT,
    GAME_STOP_FINISH_EVENT,
    SERVER_STOP_EVENT,
    SERVER_STOP_FINISH_EVENT,
    // game
    GAME_PLAYER_DAMAGE_EVENT,
    GAME_PLAYER_DAMAGE_FINISH_EVENT,
    GAME_PLAYER_DEATH_EVENT,
    GAME_PLAYER_DEATH_FINISH_EVENT,
    GAME_PLAYER_DOWN_EVENT,
    GAME_PLAYER_DOWN_FINISH_EVENT,
    GAME_PLAYER_REVIVE_EVENT,
    GAME_PLAYER_REVIVE_FINISH_EVENT,
    GAME_SPECTATE_EVENT,
    // spawn
    GAME_LOBBY_TELEPORT_EVENT,
    GAME_LOBBY_TELEPORT_FINISH_EVENT,
    // starter
    GAME_INIT_EVENT,
    GAME_INIT_FINISH_EVENT,
    GAME_LOAD_EVENT,
    GAME_LOAD_FINISH_EVENT,
    GAME_START_EVENT,
    GAME_START_FINISH_EVENT,
    // team
    INVITE_PLAYER_EVENT,
    INVITE_PLAYER_COMPLETE_EVENT,
    REQUEST_PLAYER_EVENT,
    REQUEST_PLAYER_COMPLETE_EVENT,
    // tick
    GAME_LOOT_BFS_EVENT,
    GAME_LOOT_BFS_FINISH_EVENT,
    GAME_LOOT_EVENT,
    GAME_LOOT_FINISH_EVENT,
    GAME_TICK_EVENT,
    GAME_TICK_FINISH_EVENT,
    ZONE_TICK_EVENT,
    ZONE_TICK_FINISH_EVENT,
    // zone
    ZONE_CREATED_EVENT,
    ZONE_COMPLETE_EVENT,
    CUSTOM_ZONE_EVENT,
    AIRDROP_EVENT,
    ENTITY_EVENT,
    // generate
    CUSTOM_GENERATE_EVENT,
    // client
    SPECIAL_ZONE_RENDER_EVENT,
    // register
    REGISTER_MANAGER_EVENT,
    // custom
    CUSTOM_EVENT;

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
                    // register
                    REGISTER_MANAGER_EVENT.getName(),
                    // custom
//                    CUSTOM_EVENT.getName()
            }, builder);
}
