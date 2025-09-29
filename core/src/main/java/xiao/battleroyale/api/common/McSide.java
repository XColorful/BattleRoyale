package xiao.battleroyale.api.common;

public enum McSide {
    CLIENT,
    DEDICATED_SERVER;

    public boolean isServerSide() {
        return this == DEDICATED_SERVER;
    }

    public boolean isClientSide() {
        return this == CLIENT;
    }
}
