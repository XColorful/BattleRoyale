package xiao.battleroyale.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.ILootObject;
import xiao.battleroyale.api.loot.LootNBT;
import xiao.battleroyale.api.loot.entity.IEntityLootData;
import xiao.battleroyale.api.loot.item.IItemLootData;
import xiao.battleroyale.block.entity.AbstractLootContainerBlockEntity;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;

import java.util.ArrayList;
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
    public static void generateLoot(Level level, ILootEntry entry, BlockEntity target, Supplier<Float> random, UUID gameId) {
        List<ILootData> lootData = entry.generateLootData(random);

        if (lootData == null || lootData.isEmpty()) {
            return;
        }

        if (!(target instanceof ILootObject lootObject)) {
            return;
        }

        if (lootObject instanceof AbstractLootContainerBlockEntity container) { // 物资容器方块
            container.clearContent();
            for (int i = 0; i < lootData.size() && i < container.getContainerSize(); i++) {
                ILootData data = lootData.get(i);
                if (data instanceof IItemLootData itemData) {
                    ItemStack itemStack = itemData.getItemStack();
                    if (itemStack == null) {
                        BattleRoyale.LOGGER.warn("忽略添加到物资容器的空物品");
                        continue;
                    }
                    itemStack.getOrCreateTag().putUUID(LootNBT.GAME_ID_TAG, gameId);
                    container.setItemNoUpdate(i, itemStack); // 省流
                } else {
                    BattleRoyale.LOGGER.warn("忽略添加到物资容器的非物品类型: {}", target.getBlockPos());
                }
            }
            container.sendBlockUpdated(); // 省流
        } else { // 实体刷新方块
            BlockPos spawnOrigin = target.getBlockPos();
            for (int i = 0; i < lootData.size(); i++) {
                ILootData data = lootData.get(i);
                if (data instanceof IEntityLootData entityData) {
                    if (!(level instanceof ServerLevel serverLevel)) {
                        return;
                    }
                    Entity entity = entityData.getEntity(serverLevel);
                    if (entity == null) {
                        BattleRoyale.LOGGER.warn("忽略待刷新的空实体类型");
                        continue;
                    }
                    entity.getPersistentData().putUUID(LootNBT.GAME_ID_TAG, gameId);
                    int count = entityData.getCount();
                    int range = entityData.getRange();
                    for (int j = 0; j < count; j++) {
                        BlockPos spawnPos = findValidSpawnPosition(serverLevel, spawnOrigin, range, random);
                        entity.setPos(spawnPos.getX() + 0.5F, spawnPos.getY(), spawnPos.getZ() + 0.5F);
                        serverLevel.addFreshEntity(entity);
                    }
                } else {
                    BattleRoyale.LOGGER.warn("忽略待刷新的非实体类型: {}", spawnOrigin);
                }
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
     * @param gameId 当前游戏的唯一ID
     * @return true 如果成功刷新，false 否则
     */
    public static boolean refreshLootObject(Level level, BlockPos pos, UUID gameId) {
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
        if (config != null && (blockGameId == null || !blockGameId.equals(gameId))) {
            ILootEntry entry = config.getEntry();
            generateLoot(serverLevel, entry, blockEntity, () -> serverLevel.getRandom().nextFloat(), gameId);
            lootObject.setGameId(gameId);
            blockEntity.setChanged();
            // 记录刷新信息，但这里不再记录总数，而是由 LootTickEvent 累加
            BattleRoyale.LOGGER.debug("Refreshed loot for {} at {} with configId {} for game {}", blockEntity.getClass().getSimpleName(), pos, configId, gameId);
            return true;
        }
        return false;
    }

    /**
     * 刷新指定区块内的所有战利品方块。
     * @param level 服务器世界
     * @param chunkPos 区块位置
     * @param gameId 当前游戏的唯一ID
     * @return 该区块内刷新的战利品方块数量
     */
    public static int refreshLootInChunk(ServerLevel level, ChunkPos chunkPos, UUID gameId) {
        int refreshedCount = 0;
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);

        if (chunk == null) { // 区块未加载
            BattleRoyale.LOGGER.warn("Skipping loot generation for unloaded chunk: {}", chunkPos);
            return refreshedCount;
        }

        clearOldLoot(level, chunkPos, gameId);

        // 刷新区块内所有loot方块
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (refreshLootObject(level, blockEntity.getBlockPos(), gameId)) {
                refreshedCount++;
            }
        }
        return refreshedCount;
    }

    private static void clearOldLoot(ServerLevel level, ChunkPos chunkPos, UUID gameId) {
        BlockPos minPos = new BlockPos(chunkPos.getMinBlockX(), level.getMinBuildHeight(), chunkPos.getMinBlockZ());
        BlockPos maxPos = new BlockPos(chunkPos.getMaxBlockX() + 1, level.getMaxBuildHeight(), chunkPos.getMaxBlockZ() + 1);
        AABB chunkAABB = new AABB(minPos, maxPos);
        List<Entity> allEntitiesInChunk = level.getEntitiesOfClass(Entity.class, chunkAABB, entity -> !(entity instanceof Player));
        List<Entity> oldEntities = new ArrayList<>();
        List<Entity> innocentEntities = new ArrayList<>();

        for (Entity entity : allEntitiesInChunk) {
            UUID entityGameId = null;
            if (entity instanceof ItemEntity itemEntity) { // 物品掉落物，位于{Item:{tag:{GameId:UUID}}}
                ItemStack itemStack = itemEntity.getItem();
                CompoundTag itemTag = itemStack.getOrCreateTag();
                if (itemTag.hasUUID(LootNBT.GAME_ID_TAG)) {
                    entityGameId = itemTag.getUUID(LootNBT.GAME_ID_TAG);
                }
            } else { // 一般实体，位于{ForgeData:{GameId:UUID}}
                CompoundTag persistentData = entity.getPersistentData();
                if (persistentData.hasUUID(LootNBT.GAME_ID_TAG)) {
                    entityGameId = persistentData.getUUID(LootNBT.GAME_ID_TAG);
                }
            }

            if (entityGameId != null) {
                if (!gameId.equals(entityGameId)) {
                    oldEntities.add(entity);
                }
            } else {
                innocentEntities.add(entity);
            }
        }

        // 清除旧游戏实体
        for (Entity entity : oldEntities) {
            UUID debugGameId = null;
            if (entity instanceof ItemEntity itemEntity) {
                if (itemEntity.getItem().getOrCreateTag().hasUUID(LootNBT.GAME_ID_TAG)) {
                    debugGameId = itemEntity.getItem().getOrCreateTag().getUUID(LootNBT.GAME_ID_TAG);
                }
            } else {
                if (entity.getPersistentData().hasUUID(LootNBT.GAME_ID_TAG)) {
                    debugGameId = entity.getPersistentData().getUUID(LootNBT.GAME_ID_TAG);
                }
            }

            BattleRoyale.LOGGER.debug("清除旧游戏实体: {} (UUID: {}) (GameId: {}) at {}",
                    entity.getName().getString(),
                    entity.getUUID(),
                    debugGameId != null ? debugGameId.toString() : "N/A", // 打印转换后的 UUID
                    entity.position());
            entity.remove(Entity.RemovalReason.DISCARDED);
        }

        // 清除无GameId的实体
         if (false) { // TODO 添加配置
             for (Entity entity : innocentEntities) {
                 BattleRoyale.LOGGER.debug("清除无标签实体: {} (UUID: {}) at {}", entity.getName().getString(), entity.getUUID(), entity.position());
                 entity.remove(Entity.RemovalReason.DISCARDED);
             }
         }
    }
}