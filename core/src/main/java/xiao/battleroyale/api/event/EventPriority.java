package xiao.battleroyale.api.event;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum EventPriority {
    HIGHEST,
    HIGH,
    NORMAL,
    LOW,
    LOWEST;

    private static final Map<String, EventPriority> EVENT_PRIORITIES = new HashMap<>();

    static {
        for (EventPriority priority : values()) {
            EVENT_PRIORITIES.put(priority.name(), priority);
        }
    }

    public static @Nullable EventPriority fromString(String name) {
        if (name == null) return null;
        return EVENT_PRIORITIES.get(name);
    }

    public String getName() {
        return this.name();
    }
}
