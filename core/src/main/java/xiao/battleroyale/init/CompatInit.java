package xiao.battleroyale.init;

import xiao.battleroyale.api.init.ICompatInit;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.compat.tacz.Tacz;

public class CompatInit implements ICompatInit {

    private static final CompatInit INSTANCE = new CompatInit();

    public static CompatInit get() {
        return INSTANCE;
    }

    private CompatInit() {};

    @Override
    public void onLoadComplete() {
        PlayerRevive.get().checkLoaded();
        Tacz.get().checkLoaded();
    }
}