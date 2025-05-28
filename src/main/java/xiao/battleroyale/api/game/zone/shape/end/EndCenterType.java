package xiao.battleroyale.api.game.zone.shape.end;

public enum EndCenterType {
    FIXED("fixed"),
    PREVIOUS("previous");

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