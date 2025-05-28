package xiao.battleroyale.api.game.zone.shape.start;

public enum StartCenterType {
    FIXED("fixed"),
    PREVIOUS("previous");

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