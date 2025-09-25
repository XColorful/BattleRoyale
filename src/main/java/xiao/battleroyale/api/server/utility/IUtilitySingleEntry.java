package xiao.battleroyale.api.server.utility;

import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.config.common.server.utility.type.SurvivalEntry;

public interface IUtilitySingleEntry extends IConfigSingleEntry {

    SurvivalEntry getSurvivalEntry();
}
