package xiao.battleroyale.api.game.loot;

import net.minecraft.world.level.ChunkPos;

public interface IGameLootTester {

    boolean isInQueuedChunksRef(ChunkPos chunkPos);
    boolean isInProcessedChunkCache(ChunkPos chunkPos);
    boolean isInCachedCenterOffset(ChunkPos chunkPos);
}
