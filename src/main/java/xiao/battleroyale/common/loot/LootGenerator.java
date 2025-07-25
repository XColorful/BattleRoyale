package xiao.battleroyale.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.*;
import xiao.battleroyale.api.loot.entity.IEntityLootData;
import xiao.battleroyale.api.loot.item.IItemLootData;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.block.entity.AbstractLootContainerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class LootGenerator {

    private static boolean LOOT_VANILLA_CHEST = true;
    public static void setLootVanillaChest(boolean bool) { LOOT_VANILLA_CHEST = bool; }
    private static boolean REMOVE_LOOT_TABLE = false;
    public static void setRemoveLootTable(boolean bool) { REMOVE_LOOT_TABLE = bool; }
    private static boolean REMOVE_INNOCENT_ENTITY = false;
    public static void setRemoveInnocentEntity(boolean bool) { REMOVE_INNOCENT_ENTITY = bool; }

    /**
     * 根据物资刷新配置生成
     * @param lootContext 物资刷新环境
     * @param target 战利品目标（如方块实体或实体）
     * @param entry 战利品配置
     */
    public static <T extends AbstractLootBlockEntity> void generateLoot(LootContext lootContext, T target, ILootEntry entry) {
        if (REMOVE_LOOT_TABLE) {
            removeLootTable(target);
        }

        List<ILootData> lootData = entry.generateLootData(lootContext, target);
        if (lootData.isEmpty()) {
            return;
        }

        if (target instanceof AbstractLootContainerBlockEntity container) { // 物资容器方块
            container.clearContent();
            for (int i = 0; i < lootData.size() && i < container.getContainerSize(); i++) {
                ILootData data = lootData.get(i);
                if (data.getDataType() == LootDataType.ITEM) {
                    IItemLootData itemData = (IItemLootData) data;
                    ItemStack itemStack = itemData.getItemStack();
                    if (itemStack == null) {
                        continue;
                    }
                    GameUtils.addGameId(itemStack, lootContext.gameId);
                    container.setItemNoUpdate(i, itemStack); // 省流
                } else {
                    BattleRoyale.LOGGER.warn("Ignore adding non-item to loot container at {}", target.getBlockPos());
                }
            }
            container.sendBlockUpdated(); // 省流
        } else { // 实体刷新方块
            BlockPos spawnOrigin = target.getBlockPos();
            for (ILootData data : lootData) {
                if (data.getDataType() == LootDataType.ENTITY) {
                    IEntityLootData entityData = (IEntityLootData) data;
                    Entity entity = entityData.getEntity(lootContext.serverLevel);
                    if (entity == null) {
                        continue;
                    }
                    GameUtils.addGameId(entity, lootContext.gameId);
                    int count = entityData.getCount();
                    int range = entityData.getRange();
                    for (int j = 0; j < count; j++) {
                        BlockPos spawnPos = findValidSpawnPosition(lootContext, spawnOrigin, range);
                        entity.setPos(spawnPos.getX() + 0.5F, spawnPos.getY(), spawnPos.getZ() + 0.5F);
                        lootContext.serverLevel.addFreshEntity(entity);
                    }
                } else {
                    BattleRoyale.LOGGER.warn("Ignore spawn non-entity at {}", spawnOrigin);
                }
            }
        }
    }

    /**
     * 刷新原版箱子
     */
    public static void generateVanillaLoot(LootContext lootContext, BlockEntity targetBlockEntity, ILootEntry entry) {
        if (!(targetBlockEntity instanceof Container container)) {
            return;
        }
        if (REMOVE_LOOT_TABLE) {
            removeLootTable(targetBlockEntity);
        }

        List<ILootData> lootData = entry.generateLootData(lootContext, targetBlockEntity);
        if (lootData.isEmpty()) {
            return;
        }

        container.clearContent();

        for (int i = 0; i < lootData.size() && i < container.getContainerSize(); i++) {
            ILootData data = lootData.get(i);
            if (data.getDataType() == LootDataType.ITEM) {
                IItemLootData itemData = (IItemLootData) data;
                ItemStack itemStack = itemData.getItemStack();
                if (itemStack == null) {
                    continue;
                }
                GameUtils.addGameId(itemStack, lootContext.gameId);
                container.setItem(i, itemStack); // 使用 setItem 触发更新
            } else {
                BattleRoyale.LOGGER.warn("Ignore adding non-item to vanilla container at {}", targetBlockEntity.getBlockPos());
            }
        }
    }

    /**
     * 寻找一个有效的实体生成位置
     * @param lootContext 物资刷新环境
     * @param centerPos 中心位置
     * @param range 刷新范围
     * @return 有效的刷新位置
     */
    private static BlockPos findValidSpawnPosition(LootContext lootContext, BlockPos centerPos, int range) {
        for (int i = 0; i < 4; i++) {
            int dx = (int) ((lootContext.random.get() - 0.5) * 2 * range);
            int dz = (int) ((lootContext.random.get() - 0.5) * 2 * range);
            BlockPos candidate = centerPos.offset(dx, 0, dz);
            BlockPos ground = lootContext.serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
            BlockState below = lootContext.serverLevel.getBlockState(ground.below());

            if (!below.isAir() && !below.getCollisionShape(lootContext.serverLevel, ground.below()).isEmpty()) {
                return ground;
            }
        }
        return centerPos;
    }

    /**
     * 刷新单个战利品方块实体。
     * @return 是否成功刷新
     */
    public static boolean refreshLootObject(LootContext lootContext, BlockEntity blockEntity) {
        if (!(blockEntity instanceof ILootObject lootObject)) {
            if (!LOOT_VANILLA_CHEST) {
                return false;
            }
            if (!(blockEntity instanceof Container)) {
                return false;
            }
            LootConfig config = LootConfigManager.get().getDefaultConfig();
            if (config == null) {
                return false;
            }
            ILootEntry entry = config.entry;
            generateVanillaLoot(lootContext, blockEntity, entry);
            return true;
        }

        UUID blockGameId = lootObject.getGameId();
        int configId = lootObject.getConfigId();
        LootConfig config = LootConfigManager.get().getLootConfig(blockEntity, configId);

        if (config != null && (blockGameId == null || !blockGameId.equals(lootContext.gameId))) {
            ILootEntry entry = config.entry;
            generateLoot(lootContext, (AbstractLootBlockEntity) lootObject, entry);
            lootObject.setGameId(lootContext.gameId);
            blockEntity.setChanged();
            return true;
        }
        return false;
    }

    /**
     * 刷新指定区块内的所有战利品方块。
     * @return 该区块内刷新的战利品方块数量
     */
    public static int refreshLootInChunk(LootContext lootContext) {
        int refreshedCount = 0;
        LevelChunk chunk = lootContext.serverLevel.getChunkSource().getChunkNow(lootContext.chunkPos.x, lootContext.chunkPos.z);

        if (chunk == null) {
            return refreshedCount;
        }

        clearOldLoot(lootContext);

        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (refreshLootObject(lootContext, blockEntity)) {
                refreshedCount++;
            }
        }
        return refreshedCount;
    }
    private static void clearOldLoot(LootContext lootContext) {
        BlockPos minPos = new BlockPos(lootContext.chunkPos.getMinBlockX(), lootContext.serverLevel.getMinBuildHeight(), lootContext.chunkPos.getMinBlockZ());
        BlockPos maxPos = new BlockPos(lootContext.chunkPos.getMaxBlockX() + 1, lootContext.serverLevel.getMaxBuildHeight(), lootContext.chunkPos.getMaxBlockZ() + 1);
        AABB chunkAABB = new AABB(minPos, maxPos);
        List<Entity> allEntitiesInChunk = lootContext.serverLevel.getEntitiesOfClass(Entity.class, chunkAABB, entity -> !(entity instanceof Player));
        List<Entity> oldEntities = new ArrayList<>();
        List<Entity> innocentEntities = new ArrayList<>();

        for (Entity entity : allEntitiesInChunk) {
            UUID entityGameId = GameUtils.getGameId(entity);
            if (entityGameId != null) {
                if (!lootContext.gameId.equals(entityGameId)) {
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
                if (itemEntity.getItem().getOrCreateTag().hasUUID(LootNBTTag.GAME_ID_TAG)) {
                    debugGameId = itemEntity.getItem().getOrCreateTag().getUUID(LootNBTTag.GAME_ID_TAG);
                }
            } else {
                if (entity.getPersistentData().hasUUID(LootNBTTag.GAME_ID_TAG)) {
                    debugGameId = entity.getPersistentData().getUUID(LootNBTTag.GAME_ID_TAG);
                }
            }

            BattleRoyale.LOGGER.info("Clear old game object: {} (UUID: {}) (GameId: {}) at {}",
                    entity.getName().getString(),
                    entity.getUUID(),
                    debugGameId != null ? debugGameId.toString() : "N/A", // 打印转换后的 UUID
                    entity.position());
            entity.remove(Entity.RemovalReason.DISCARDED);
        }

        // 清除无GameId的实体
         if (REMOVE_INNOCENT_ENTITY) {
             for (Entity entity : innocentEntities) {
                 BattleRoyale.LOGGER.debug("Clear entity with no gameId tag: {} (UUID: {}) at {}", entity.getName().getString(), entity.getUUID(), entity.position());
                 entity.remove(Entity.RemovalReason.DISCARDED);
             }
         }
    }

    public static void removeLootTable(BlockEntity blockEntity) {
        CompoundTag nbt = blockEntity.saveWithFullMetadata();
        BattleRoyale.LOGGER.info("nbt before remove loot table:{}", nbt);
        nbt.remove("LootTable");
        nbt.remove("LootTableSeed");
        blockEntity.load(nbt); // 写入内存，没写入硬盘
        blockEntity.setChanged(); // 防止区块被卸载前还没交互

        nbt = blockEntity.saveWithFullMetadata();
        BattleRoyale.LOGGER.info("nbt after remove loot table:{}", nbt);
    }

    public static class LootContext {
        public ServerLevel serverLevel;
        public ChunkPos chunkPos;
        public UUID gameId;
        public Supplier<Float> random;
        public LootContext(ServerLevel serverLevel, ChunkPos chunkPos, UUID gameId) {
            this.serverLevel = serverLevel;
            this.chunkPos = chunkPos;
            this.gameId = gameId;
            this.random = () -> serverLevel.getRandom().nextFloat();
        }
    }
}