package xiao.battleroyale.init;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.ICommonSetup;
import xiao.battleroyale.common.game.process.deathmatch.DMGameProcessManager;
import xiao.battleroyale.data.io.TempDataManager;
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
        BattleRoyale.LOGGER.debug("onCommonSetup, reloadAllConfigs:");
        BattleRoyale.getModConfigManager().reloadAllConfigs();
        CommandSelector.get().setupSelectors(BattleRoyale.getSelectorRegistry());
        TempDataManager.get().saveTempData();

        // 所有扩展放在 onCommonSetup 执行
        DMGameProcessManager.init(BattleRoyale.getMcSide());
    }
}
