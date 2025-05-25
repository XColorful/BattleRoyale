package xiao.battleroyale.config.common.game.spawn.type.detail;

import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;

import java.util.HashMap;
import java.util.Map;

public enum CommonDetailType {
    FIXED(SpawnDetailTag.FIXED),
    RANDOM(SpawnDetailTag.RANDOM);

    private final String name;

    CommonDetailType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, CommonDetailType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (CommonDetailType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static CommonDetailType fromName(String name) {
        return NAME_TO_TYPE.get(name);
    }
}
