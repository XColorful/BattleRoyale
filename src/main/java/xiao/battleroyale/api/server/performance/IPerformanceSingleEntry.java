package xiao.battleroyale.api.server.performance;

import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

public interface IPerformanceSingleEntry extends IConfigSingleEntry {

    GeneratorEntry getGeneratorEntry();
}
