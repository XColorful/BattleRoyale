package xiao.battleroyale.api.game.zone.shape.start;

public enum StartDimensionType {
    FIXED("fixed"),
    PREVIOUS("previous");

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