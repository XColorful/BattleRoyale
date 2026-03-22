package xiao.battleroyale.api.event;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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

    public static final SuggestionProvider<CommandSourceStack> EVENT_PRIORITY_SUGGESTS = (context, builder) ->
            SharedSuggestionProvider.suggest(Arrays.stream(EventPriority.values())
                    .map(EventPriority::name)
                    .toArray(String[]::new), builder);
}
