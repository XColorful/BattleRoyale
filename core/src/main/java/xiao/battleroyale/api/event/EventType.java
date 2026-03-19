package xiao.battleroyale.api.event;

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
}
