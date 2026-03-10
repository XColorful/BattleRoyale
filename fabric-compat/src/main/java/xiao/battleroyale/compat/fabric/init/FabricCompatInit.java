package xiao.battleroyale.compat.fabric.init;

import xiao.battleroyale.init.CompatInit;

public class FabricCompatInit {
    private static final CompatInit COMPAT_INIT = CompatInit.get();

    public static void init() {
        // 直接执行，因为在 Fabric 顺序加载中，
        // 执行到这里的时机通常已经足够晚
        COMPAT_INIT.onLoadComplete();
    }
}