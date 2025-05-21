package xiao.battleroyale.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class LootGenerator {

    /**
     * 根据战利品配置生成战利品到目标对象。
     * @param level 当前世界
     * @param entry 战利品入口配置
     * @param target 战利品目标（如方块实体或实体）
     * @param random 随机数生成器
     */
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

    /**
     * 寻找一个有效的实体生成位置。
     * @param level 服务器世界
     * @param centerPos 中心位置
     * @param range 刷新范围
     * @param random 随机数生成器
     * @return 有效的刷新位置
     */
    private static BlockPos findValidSpawnPosition(ServerLevel level, BlockPos centerPos, int range, Supplier<Float> random) {
        for (int i = 0; i < 4; i++) {
            int dx = (int) ((random.get() - 0.5) * 2 * range);
            int dz = (int) ((random.get() - 0.5) * 2 * range);
            BlockPos candidate = centerPos.offset(dx, 0, dz);
            BlockPos ground = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
            BlockState below = level.getBlockState(ground.below());

            if (!below.isAir() && !below.getCollisionShape(level, ground.below()).isEmpty()) {
                return ground;
            }
        }
        return centerPos;
    }

    /**
     * 刷新单个战利品方块实体。
     * @param level 当前世界
     * @param pos 方块位置
     * @param currentGameId 当前游戏的唯一ID
     * @return true 如果成功刷新，false 否则
     */
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

        // 仅在配置存在且方块的 gameId 与当前 gameId 不同时刷新
        if (config != null && (blockGameId == null || !blockGameId.equals(currentGameId))) {
            ILootEntry<?> entry = config.getEntry();
            generateLoot(serverLevel, entry, blockEntity, () -> serverLevel.getRandom().nextFloat());
            lootObject.setGameId(currentGameId);
            blockEntity.setChanged();
            // 记录刷新信息，但这里不再记录总数，而是由 LootTickEvent 累加
            BattleRoyale.LOGGER.debug("Refreshed loot for {} at {} with configId {} for game {}", blockEntity.getClass().getSimpleName(), pos, configId, currentGameId);
            return true;
        }
        return false;
    }

    /**
     * 刷新指定区块内的所有战利品方块。
     * @param level 服务器世界
     * @param chunkPos 区块位置
     * @param currentGameId 当前游戏的唯一ID
     * @return 该区块内刷新的战利品方块数量
     */
    public static int refreshLootInChunk(ServerLevel level, ChunkPos chunkPos, UUID currentGameId) {
        int refreshedCount = 0;
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);

        if (chunk == null) { // 区块未加载
            BattleRoyale.LOGGER.warn("Skipping loot generation for unloaded chunk: {}", chunkPos);
            return refreshedCount;
        }

        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (refreshLootObject(level, blockEntity.getBlockPos(), currentGameId)) {
                refreshedCount++;
            }
        }
        return refreshedCount;
    }
}