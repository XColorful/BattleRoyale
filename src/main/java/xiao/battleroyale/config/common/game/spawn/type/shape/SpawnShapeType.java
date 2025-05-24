package xiao.battleroyale.config.common.game.spawn.type.shape;

import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;

import java.util.HashMap;
import java.util.Map;

public enum SpawnShapeType {
    CIRCLE(SpawnShapeTag.CIRCLE),
    SQUARE(SpawnShapeTag.SQUARE),
    RECTANGLE(SpawnShapeTag.RECTANGLE);

    private final String name;

    SpawnShapeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, SpawnShapeType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (SpawnShapeType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static SpawnShapeType fromName(String name) {
        return NAME_TO_TYPE.get(name);
    }
}