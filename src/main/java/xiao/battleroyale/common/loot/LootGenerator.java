package xiao.battleroyale.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.ILootObject;
import xiao.battleroyale.api.loot.entity.IEntityLootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class LootGenerator {

    public static void generateLoot(Level level, ILootEntry<?> entry, Object target, Supplier<Float> random) {
        List<?> generatedLoot = entry.generateLoot(random);

        if (generatedLoot == null || generatedLoot.isEmpty()) {
            return;
        }

        if (!(target instanceof ILootObject lootObject)) {
            return;
        }

        if (lootObject instanceof Container container) {
            container.clearContent();
            for (int i = 0; i < generatedLoot.size() && i < container.getContainerSize(); i++) {
                Object lootItem = generatedLoot.get(i);
                if (lootItem instanceof ItemStack itemStack) {
                    container.setItem(i, itemStack);
                } else {
                    BattleRoyale.LOGGER.warn("Ignore non-item loot for container at: {}", ((BlockEntity) target).getBlockPos());
                }
            }
        } else if (entry instanceof IEntityLootEntry entityEntry && level instanceof ServerLevel serverLevel) {
            BlockPos spawnOrigin = ((BlockEntity) target).getBlockPos();
            int range = entityEntry.getRange();
            for (Object obj : generatedLoot) {
                if (!(obj instanceof Entity entity)) continue;

                BlockPos spawnPos = findValidSpawnPosition(serverLevel, spawnOrigin, range, random);
                entity.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                serverLevel.addFreshEntity(entity);
            }
        }
    }

    private static BlockPos findValidSpawnPosition(ServerLevel level, BlockPos centerPos, int range, Supplier<Float> random) {
        for (int i = 0; i < 4; i++) {
            int dx = (int) ((random.get() - 0.5) * 2 * range);
            int dz = (int) ((random.get() - 0.5) * 2 * range);
            BlockPos candidate = centerPos.offset(dx, 0, dz);
            BlockPos ground = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
            BlockState below = level.getBlockState(ground.below());

            // 替代 getMaterial().isSolid()
            if (!below.isAir() && below.getCollisionShape(level, ground.below()).isEmpty() == false) {
                return ground;
            }
        }
        return centerPos;
    }

    public static boolean refreshLootObject(Level level, BlockPos pos, UUID currentGameId) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ILootObject lootObject)) {
            return false;
        }

        UUID blockGameId = lootObject.getGameId();
        int configId = lootObject.getConfigId();
        LootConfig config = LootConfigManager.get().getLootConfig(blockEntity, configId);

        if (config != null && (blockGameId == null || !blockGameId.equals(currentGameId))) {
            ILootEntry<?> entry = config.getEntry();
            generateLoot(serverLevel, entry, blockEntity, () -> serverLevel.getRandom().nextFloat());
            lootObject.setGameId(currentGameId);
            blockEntity.setChanged();
            BattleRoyale.LOGGER.info("Refreshed loot for {} at {} with configId {} for game {}", blockEntity.getClass().getSimpleName(), pos, configId, currentGameId);
            return true;
        }
        return false;
    }

    public static int refreshLootInChunk(ServerLevel level, ChunkPos chunkPos, UUID currentGameId) {
        int refreshedCount = 0;
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
            return refreshedCount;
        }

        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (!(blockEntity instanceof ILootObject)) {
                continue;
            }
            if (refreshLootObject(level, blockEntity.getBlockPos(), currentGameId)) {
                refreshedCount++;
            }
        }
        return refreshedCount;
    }

    public static int refreshAllLoadedLoot(ServerLevel level, UUID currentGameId) {
        int totalRefreshedCount = 0;
        MinecraftServer server = level.getServer();
        int simDist = server.getPlayerList().getSimulationDistance();

        Set<ChunkPos> chunksToProcess = new HashSet<>();
        Set<ChunkPos> processedCenterChunks = new HashSet<>();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.level() != level) continue;

            BlockPos playerPos = player.blockPosition();
            ChunkPos centerChunk = new ChunkPos(playerPos);

            // 如果该中心 chunk 已处理过，就跳过
            if (!processedCenterChunks.add(centerChunk)) {
                continue;
            }

            // 计算模拟范围内的 chunk 并加入待处理集合
            for (int dx = -simDist; dx <= simDist; dx++) {
                for (int dz = -simDist; dz <= simDist; dz++) {
                    chunksToProcess.add(new ChunkPos(centerChunk.x + dx, centerChunk.z + dz));
                }
            }
        }

        // 去重后统一刷新
        for (ChunkPos pos : chunksToProcess) {
            LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
            if (chunk != null) {
                totalRefreshedCount += refreshLootInChunk(level, pos, currentGameId);
            }
        }

        BattleRoyale.LOGGER.info("Refreshed {} loot objects in level: {}.", totalRefreshedCount, level.dimension().location());
        return totalRefreshedCount;
    }

}
