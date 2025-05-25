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

    /**
     * 根据字符串值获取对应的枚举实例
     * @param text 字符串值
     * @return 对应的StartCenterType枚举实例，如果未找到则返回null
     */
    public static StartCenterType fromValue(String text) {
        for (StartCenterType b : StartCenterType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}