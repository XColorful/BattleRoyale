package xiao.battleroyale.config.common.game.zone.zonespecial;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.api.game.zone.special.IZoneSpecialEntry;
import xiao.battleroyale.api.game.zone.special.ZoneSpecialTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum ZoneSpecialType {
    // default
    @ApiStatus.Internal NULL(ZoneSpecialTag.NULL, null),
    // client
    RENDER(ZoneSpecialTag.RENDER, SpecialRenderEntry::fromJson);

    private final String name;
    private final Function<JsonObject, ? extends IZoneSpecialEntry> deserializer;

    ZoneSpecialType(String name, Function<JsonObject, ? extends IZoneSpecialEntry> deserializer) {
        this.name = name;
        this.deserializer = deserializer;
    }

    public String getName() {
        return name;
    }

    public Function<JsonObject, ? extends IZoneSpecialEntry> getDeserializer() {
        return deserializer;
    }

    private static final Map<String, ZoneSpecialType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (ZoneSpecialType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static ZoneSpecialType fromName(String name) {
        return NAME_TO_TYPE.get(name);
    }
}
