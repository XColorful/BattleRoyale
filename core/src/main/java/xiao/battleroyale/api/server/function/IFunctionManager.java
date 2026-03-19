package xiao.battleroyale.api.server.function;

import xiao.battleroyale.api.server.IServerSubManager;

public interface IFunctionManager extends IServerSubManager, IFunctionRegister {

    void clearConfigFunction();
    void clearApiFunction();
}
