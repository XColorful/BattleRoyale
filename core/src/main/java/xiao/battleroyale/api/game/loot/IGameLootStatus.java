package xiao.battleroyale.api.game.loot;

public interface IGameLootStatus {

    int getLastBfsTime();
    int getLastBfsProcessedLoot();
    int queuedChunksRefSize();
    int processedChunkCacheSize();
    int cachedPlayerCenterChunksSize();
    int cachedCenterOffsetSize();
}
