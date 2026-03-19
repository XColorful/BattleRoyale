package xiao.battleroyale.common.server.performance;

import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.server.performance.IPerformanceManager;
import xiao.battleroyale.common.server.AbstractServerManager;

public class PerformanceManager extends AbstractServerManager implements IPerformanceManager {

    private static class PerformanceManagerHolder {
        private static final PerformanceManager INSTANCE = new PerformanceManager();
    }

    public static PerformanceManager get() {
        return PerformanceManagerHolder.INSTANCE;
    }

    protected PerformanceManager() {
        ;
    }

    public static void init(McSide mcSide) {
        ;
    }
}
