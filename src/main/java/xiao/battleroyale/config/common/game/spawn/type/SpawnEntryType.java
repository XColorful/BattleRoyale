package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum SpawnEntryType {
    GROUND("ground", GroundEntry::fromJson),
    PLANE("plane", PlaneEntry::fromJson);

    private final String name;
    private final Function<JsonObject, ? extends ISpawnEntry> deserializer;

    SpawnEntryType(String name, Function<JsonObject, ? extends ISpawnEntry> deserializer) {
        this.name = name;
        this.deserializer = deserializer;
    }

    public String getName() {
        return name;
    }

    public Function<JsonObject, ? extends ISpawnEntry> getDeserializer() {
        return deserializer;
    }

    private static final Map<String, SpawnEntryType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (SpawnEntryType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static SpawnEntryType fromNames(String name) {
        return NAME_TO_TYPE.get(name);
    }
}
