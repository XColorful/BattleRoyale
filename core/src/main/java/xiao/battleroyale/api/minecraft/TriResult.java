package xiao.battleroyale.api.minecraft;

public enum TriResult {
    ALLOW,
    DENY,
    DEFAULT;

    public TriResult from(boolean bool) {
        return bool ? ALLOW : DENY;
    }

    public boolean toBool(boolean defaultValue) {
        return switch (this) {
            case ALLOW -> true;
            case DENY -> false;
            default -> defaultValue;
        };
    }

    public boolean isDenied() {
        return this == DENY;
    }
    public boolean isDefault() {
        return this == DEFAULT;
    }
    public boolean isAllowed() {
        return this == ALLOW;
    }
}
