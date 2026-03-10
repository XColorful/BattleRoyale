package xiao.battleroyale.compat.fabric.init;

import xiao.battleroyale.api.init.ICommonSetup;
import xiao.battleroyale.init.CommonSetup;

public class FabricCommonSetup {

    private static final ICommonSetup COMMON_SETUP = CommonSetup.get();

    public static void init() {
        // Fabric 没有 enqueueWork，因为 onInitialize 本身就在主线程按序执行
        COMMON_SETUP.onCommonSetup();
    }
}