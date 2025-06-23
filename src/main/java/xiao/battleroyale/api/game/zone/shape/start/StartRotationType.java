package xiao.battleroyale.api.game.zone.shape.start;

import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

public enum StartRotationType {
    FIXED(ZoneShapeTag.FIXED),
    PREVIOUS(ZoneShapeTag.PREVIOUS),
    RELATIVE(ZoneShapeTag.RELATIVE),
    LOCK_PLAYER(ZoneShapeTag.LOCK_PLAYER);

    private final String value;

    StartRotationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StartRotationType fromValue(String text) {
        for (StartRotationType b : StartRotationType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
