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

    /**
     * 根据字符串值获取对应的枚举实例
     * @param text 字符串值
     * @return 对应的EndCenterType枚举实例，如果未找到则返回null
     */
    public static EndCenterType fromValue(String text) {
        for (EndCenterType b : EndCenterType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}