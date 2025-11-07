package xiao.battleroyale.api.game.loot;

import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

public interface IGameLootOperator {

    void applyConfig(GeneratorEntry generatorEntry);

    void forceClearQueuedChunkRef();
    void forceClearProcessedChunkCache();
    void forceClearPlayerCenterChunks();

    void awaitTerminationOnShutdown();
}
