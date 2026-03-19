package xiao.battleroyale.api.server.function;

import xiao.battleroyale.config.common.server.function.type.RegisterEntry;

public interface IFunctionRegister extends IFunctionRegisterApi {

    void applyConfig(RegisterEntry registerEntry);
}
