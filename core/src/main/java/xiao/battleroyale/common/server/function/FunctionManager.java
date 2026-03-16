package xiao.battleroyale.common.server.function;

import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.server.function.IFunctionManager;

public class FunctionManager implements IFunctionManager {

    private static class FunctionManagerHolder {
        private static final FunctionManager INSTANCE = new FunctionManager();
    }

    public static FunctionManager get() {
        return FunctionManagerHolder.INSTANCE;
    }

    protected FunctionManager() {
        ;
    }

    public static void init(McSide mcSide) {
        ;
    }
}