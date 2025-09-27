package xiao.battleroyale.api.game.zone.shape.end;

import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

public enum EndRotationType {
    FIXED(ZoneShapeTag.FIXED),
    PREVIOUS(ZoneShapeTag.PREVIOUS),
    RELATIVE(ZoneShapeTag.RELATIVE),
    LOCK_PLAYER(ZoneShapeTag.LOCK_PLAYER);

    private final String value;

    EndRotationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EndRotationType fromValue(String text) {
        for (EndRotationType b : EndRotationType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}