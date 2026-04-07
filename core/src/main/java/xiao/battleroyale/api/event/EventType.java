package xiao.battleroyale.api.event;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    SERVER_TICK_EVENT,
    CLIENT_TICK_EVENT,
    LIVING_ATTACK_EVENT,
    LIVING_HURT_EVENT,
    LIVING_DAMAGE_EVENT,
    LIVING_DEATH_EVENT,
    PLAYER_LOGGED_IN_EVENT,
    PLAYER_LOGGED_OUT_EVENT,
    PLAYER_RESPAWN_EVENT,
    ENTITY_INTERACT_EVENT,
    ENTITY_INTERACT_SPECIFIC_EVENT,
    LEFT_CLICK_BLOCK_EVENT,
    RIGHT_CLICK_BLOCK_EVENT,
    RIGHT_CLICK_ITEM_EVENT,
    BLOCK_BREAK_EVENT,
    BLOCK_TOOL_MODIFICATION_EVENT,
    ENTITY_PLACE_BLOCK_EVENT,
    PORTAL_SPAWN_EVENT,
    FARMLAND_TRAMPLE_EVENT,
    RENDER_LEVEL_STAGE_EVENT,
    RENDER_TRANSLUCENT_EVENT, // 单独拆一个事件，避免多次上锁，减少一点不必要的开销
    RENDER_GUI_EVENT;

    private static final Map<String, EventType> EVENT_TYPES = new HashMap<>();

    static {
        for (EventType type : values()) {
            EVENT_TYPES.put(type.name(), type);
        }
    }

    public static @Nullable EventType fromString(String name) {
        if (name == null) return null;
        return EVENT_TYPES.get(name);
    }

    public String getName() {
        return this.name();
    }

    public static final SuggestionProvider<CommandSourceStack> EVENT_TYPE_SUGGESTS = (context, builder) ->
            SharedSuggestionProvider.suggest(new String[]{
                    SERVER_TICK_EVENT.getName(),
//                    CLIENT_TICK_EVENT.getName(),
                    LIVING_ATTACK_EVENT.getName(),
                    LIVING_HURT_EVENT.getName(),
                    LIVING_DAMAGE_EVENT.getName(),
                    LIVING_DEATH_EVENT.getName(),
                    PLAYER_LOGGED_IN_EVENT.getName(),
                    PLAYER_LOGGED_OUT_EVENT.getName(),
                    PLAYER_RESPAWN_EVENT.getName(),
                    ENTITY_INTERACT_EVENT.getName(),
                    ENTITY_INTERACT_SPECIFIC_EVENT.getName(),
                    LEFT_CLICK_BLOCK_EVENT.getName(),
                    RIGHT_CLICK_BLOCK_EVENT.getName(),
                    RIGHT_CLICK_ITEM_EVENT.getName(),
                    BLOCK_BREAK_EVENT.getName(),
                    BLOCK_TOOL_MODIFICATION_EVENT.getName(),
                    ENTITY_PLACE_BLOCK_EVENT.getName(),
                    PORTAL_SPAWN_EVENT.getName(),
                    FARMLAND_TRAMPLE_EVENT.getName(),
//                    RENDER_LEVEL_STAGE_EVENT.getName(),
//                    RENDER_TRANSLUCENT_EVENT.getName(),
//                    RENDER_GUI_EVENT.getName(),
            }, builder);
}
