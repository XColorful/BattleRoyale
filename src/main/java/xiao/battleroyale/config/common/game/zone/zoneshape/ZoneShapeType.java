package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum ZoneShapeType {
    CIRCLE(ZoneShapeTag.CIRCLE, CircleEntry::fromJson),
    SQUARE(ZoneShapeTag.SQUARE, SquareEntry::fromJson),
    RECTANGLE(ZoneShapeTag.RECTANGLE, RectangleEntry::fromJson),
    HEXAGON(ZoneShapeTag.HEXAGON, HexagonEntry::fromJson);

    private final String name;
    private final Function<JsonObject, ? extends IZoneShapeEntry> deseralizer;

    ZoneShapeType(String name, Function<JsonObject, ? extends IZoneShapeEntry> deseralizer) {
        this.name = name;
        this.deseralizer = deseralizer;
    }

    public String getName() {
        return name;
    }

    public Function<JsonObject, ? extends IZoneShapeEntry> getDeserializer() {
        return deseralizer;
    }

    private static final Map<String, ZoneShapeType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (ZoneShapeType type: values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static ZoneShapeType fromName(String name) {
        return NAME_TO_TYPE.get(name);
    }
}
