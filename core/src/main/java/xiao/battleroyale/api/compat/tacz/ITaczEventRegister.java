package xiao.battleroyale.api.compat.tacz;

public interface ITaczEventRegister {

    boolean registerBleedingHandler();

    boolean unregisterBleedingHandler();

    boolean registerBulletHandler();

    boolean unregisterBulletHandler();
}
