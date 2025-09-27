package xiao.battleroyale.init;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.ICommonSetup;
import xiao.battleroyale.network.GameInfoHandler;

public class CommonSetup implements ICommonSetup {

    private static final CommonSetup INSTANCE = new CommonSetup();

    public static CommonSetup get() {
        return INSTANCE;
    }

    private CommonSetup() {}

    @Override
    public void onCommonSetup() {
        GameInfoHandler.init();
        BattleRoyale.getModConfigManager().reloadAllConfigs();
    }
}
