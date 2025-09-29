package xiao.battleroyale.api.game.zone.shape.start;

import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

public enum StartDimensionType {
    FIXED(ZoneShapeTag.FIXED),
    PREVIOUS(ZoneShapeTag.PREVIOUS),
    RELATIVE(ZoneShapeTag.RELATIVE);

    private final String value;

    StartDimensionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StartDimensionType fromValue(String text) {
        for (StartDimensionType b : StartDimensionType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}