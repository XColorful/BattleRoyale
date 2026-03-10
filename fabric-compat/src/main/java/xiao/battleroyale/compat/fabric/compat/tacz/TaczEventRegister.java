package xiao.battleroyale.compat.fabric.compat.tacz;

import xiao.battleroyale.api.compat.tacz.ITaczEventRegister;

public class TaczEventRegister implements ITaczEventRegister {

    private static class TaczEventRegisterHolder {
        private static final TaczEventRegister INSTANCE = new TaczEventRegister();
    }

    public static TaczEventRegister get() {
        return TaczEventRegisterHolder.INSTANCE;
    }

    private TaczEventRegister() {}

    @Override
    public boolean registerBleedingHandler() {
        // 空实现，不注册任何监听器
        return false;
    }

    @Override
    public boolean unregisterBleedingHandler() {
        // 空实现
        return false;
    }
}