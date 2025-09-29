package xiao.battleroyale.api.compat;

public interface ICompatMod {

    String getModId();

    void checkLoaded();

    boolean isLoaded();
}
