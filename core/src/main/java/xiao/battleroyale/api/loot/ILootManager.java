package xiao.battleroyale.api.loot;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ILootManager {

    int getMaxLootChunkPerTick();

    @Nullable UUID getCurrentGenerationGameId();
}
