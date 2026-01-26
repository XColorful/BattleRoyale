package xiao.battleroyale.config.common.game.zone.zoneshape;

import xiao.battleroyale.api.config.common.game.zone.shape.ZoneShapeTag;

public enum EndDimensionType {
    FIXED(ZoneShapeTag.FIXED),
    PREVIOUS(ZoneShapeTag.PREVIOUS),
    RELATIVE(ZoneShapeTag.RELATIVE);

    private final String value;

    EndDimensionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EndDimensionType fromValue(String text) {
        for (EndDimensionType b : EndDimensionType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}