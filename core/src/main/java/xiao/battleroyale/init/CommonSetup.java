package xiao.battleroyale.init;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.ICommonSetup;
import xiao.battleroyale.network.NetworkHandler;

public class CommonSetup implements ICommonSetup {

    private static final CommonSetup INSTANCE = new CommonSetup();

    public static CommonSetup get() {
        return INSTANCE;
    }

    private CommonSetup() {}

    @Override
    public void onCommonSetup() {
        NetworkHandler.get().registerMessages();
        BattleRoyale.getModConfigManager().reloadAllConfigs();
    }
}
