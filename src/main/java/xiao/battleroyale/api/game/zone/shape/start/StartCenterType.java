package xiao.battleroyale.api.game.zone.shape.start;

import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

public enum StartCenterType {
    FIXED(ZoneShapeTag.FIXED),
    PREVIOUS(ZoneShapeTag.PREVIOUS),
    RELATIVE(ZoneShapeTag.RELATIVE);

    private final String value;

    StartCenterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StartCenterType fromValue(String text) {
        for (StartCenterType b : StartCenterType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}