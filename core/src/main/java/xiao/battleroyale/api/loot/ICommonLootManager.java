package xiao.battleroyale.api.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.IServerTickEvent;
import xiao.battleroyale.common.loot.LootStatus;

public interface ICommonLootManager extends ILootManager, ILootStatus {

    int chunksToProcessSize();
    int processedChunkTrackerSize();
    ServerLevel getCurrentGenerationLevel();
    int totalLootRefreshedInBatch();

    LootStatus lootPos(@Nullable CommandSourceStack source, ServerLevel serverLevel, Vec3 pos);
    int lootChunk(@Nullable CommandSourceStack source, ServerLevel serverLevel, Vec3 pos);
    int lootGeneration(CommandSourceStack source, ServerLevel serverLevel);
    boolean stopLootGeneration(@Nullable CommandSourceStack source);

    boolean onLootTick(IServerTickEvent event);
}
