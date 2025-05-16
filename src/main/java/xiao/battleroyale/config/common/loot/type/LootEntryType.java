package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum LootEntryType {
    MULTI("multi", MultiEntry::fromJson),
    RANDOM("random", RandomEntry::fromJson),
    WEIGHT("weight", WeightEntry::fromJson),
    ITEM("item", ItemEntry::fromJson),
    ENTITY("entity", EntityEntry::fromJson),
    EMPTY("empty", EmptyEntry::fromJson),
    NONE("none", NoneEntry::fromJson),
    REPEAT("repeat", RepeatEntry::fromJson),
    TIME("time", TimeEntry::fromJson);

    private final String name;
    private final Function<JsonObject, ? extends ILootEntry<?>> deserializer;

    LootEntryType(String name, Function<JsonObject, ? extends ILootEntry<?>> deserializer) {
        this.name = name;
        this.deserializer = deserializer;
    }

    public String getName() {
        return name;
    }

    public Function<JsonObject, ? extends ILootEntry<?>> getDeserializer() {
        return deserializer;
    }

    private static final Map<String, LootEntryType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (LootEntryType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static LootEntryType fromName(String name) {
        return NAME_TO_TYPE.get(name);
    }
}