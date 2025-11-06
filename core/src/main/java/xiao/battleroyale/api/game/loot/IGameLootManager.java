package xiao.battleroyale.api.game.loot;

import net.minecraft.world.level.ChunkPos;
import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

public interface IGameLootManager extends IGameSubManager {

    void applyConfig(GeneratorEntry generatorEntry);

    void awaitTerminationOnShutdown();

    int getMaxLootChunkPerTick();

    int getMaxLootDistance();

    int getTolerantCenterDistance();

    int getMaxCachedCenter();

    int getMaxQueuedChunk();

    int getBfsFrequency();

    boolean isInstantNextBfs();

    int getMaxCachedLootChunk();

    int getCleanCachedChunk();

    int getLastBfsTime();

    int getLastBfsProcessedLoot();

    int queuedChunksRefSize();

    int processedChunkCacheSize();

    int cachedPlayerCenterChunksSize();

    int cachedCenterOffsetSize();

    boolean isInQueuedChunksRef(ChunkPos chunkPos);

    boolean isInProcessedChunkCache(ChunkPos chunkPos);

    boolean isInCachedCenterOffset(ChunkPos chunkPos);
}
