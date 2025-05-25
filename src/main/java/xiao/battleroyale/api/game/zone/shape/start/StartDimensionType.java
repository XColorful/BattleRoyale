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

    /**
     * 根据字符串值获取对应的枚举实例
     * @param text 字符串值
     * @return 对应的StartDimensionType枚举实例，如果未找到则返回null
     */
    public static StartDimensionType fromValue(String text) {
        for (StartDimensionType b : StartDimensionType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}