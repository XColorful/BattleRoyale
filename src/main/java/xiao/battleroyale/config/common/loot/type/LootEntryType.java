package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum LootEntryType {
    MULTI(LootEntryTag.TYPE_MULTI, MultiEntry::fromJson),
    RANDOM(LootEntryTag.TYPE_RANDOM, RandomEntry::fromJson),
    WEIGHT(LootEntryTag.TYPE_WEIGHT, WeightEntry::fromJson),
    ITEM(LootEntryTag.TYPE_ITEM, ItemEntry::fromJson),
    ENTITY(LootEntryTag.TYPE_ENTITY, EntityEntry::fromJson),
    EMPTY(LootEntryTag.TYPE_EMPTY, EmptyEntry::fromJson),
    NONE(LootEntryTag.TYPE_NONE, NoneEntry::fromJson),
    REPEAT(LootEntryTag.TYPE_REPEAT, RepeatEntry::fromJson),
    TIME(LootEntryTag.TYPE_TIME, TimeEntry::fromJson),
    BOUND(LootEntryTag.TYPE_BOUND, BoundEntry::fromJson),
    EXTRA(LootEntryTag.TYPE_EXTRA, ExtraEntry::fromJson),
    SHUFFLE(LootEntryTag.TYPE_SHUFFLE, ShuffleEntry::fromJson),
    CLEAN(LootEntryTag.TYPE_CLEAN, CleanEntry::fromJson);

    private final String name;
    private final Function<JsonObject, ? extends ILootEntry> deserializer;

    LootEntryType(String name, Function<JsonObject, ? extends ILootEntry> deserializer) {
        this.name = name;
        this.deserializer = deserializer;
    }

    public String getName() {
        return name;
    }

    public Function<JsonObject, ? extends ILootEntry> getDeserializer() {
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