package xiao.battleroyale.config.common.game.zone.zoneshape;

import xiao.battleroyale.api.config.common.game.zone.shape.ZoneShapeTag;

public enum EndCenterType {
    FIXED(ZoneShapeTag.FIXED),
    PREVIOUS(ZoneShapeTag.PREVIOUS),
    RELATIVE(ZoneShapeTag.RELATIVE),
    LOCK_PLAYER(ZoneShapeTag.LOCK_PLAYER);

    private final String value;

    EndCenterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EndCenterType fromValue(String text) {
        for (EndCenterType b : EndCenterType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}