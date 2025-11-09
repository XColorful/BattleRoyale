package xiao.battleroyale.api.game.loot;

public interface IGameLootConfigGetter {

    int getMaxLootChunkPerTick();
    int getMaxLootDistance();
    int getTolerantCenterDistance();
    int getMaxCachedCenter();
    int getMaxQueuedChunk();
    int getBfsFrequency();
    boolean isInstantNextBfs();
    int getMaxCachedLootChunk();
    int getCleanCachedChunk();

    int getSimulationDistance();
}
