package xiao.battleroyale.api.game.zone.shape.end;

public enum EndDimensionType {
    FIXED("fixed"),
    PREVIOUS("previous");

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