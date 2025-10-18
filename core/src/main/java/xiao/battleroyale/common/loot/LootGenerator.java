package xiao.battleroyale.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameIdReadApi;
import xiao.battleroyale.api.game.IGameIdWriteApi;
import xiao.battleroyale.api.loot.*;
import xiao.battleroyale.api.loot.entity.IEntityLootData;
import xiao.battleroyale.api.loot.item.IItemLootData;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.block.entity.AbstractLootContainerBlockEntity;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LootGenerator {

    public static final int CHUNK_NOT_LOADED = -1;

    private static boolean LOOT_ANY_BLOCK_ENTITY = true;
    public static void setLootAnyBlockEntity(boolean bool) { LOOT_ANY_BLOCK_ENTITY = bool; }
    private static boolean LOOT_VANILLA_CHEST = true;
    public static void setLootVanillaChest(boolean bool) { LOOT_VANILLA_CHEST = bool; }
    private static boolean REMOVE_LOOT_TABLE = false;
    public static void setRemoveLootTable(boolean bool) { REMOVE_LOOT_TABLE = bool; }
    private static boolean CLEAR_PREVIOUS_CONTENT = true;
    public static void setClearPreviousContent(boolean bool) { CLEAR_PREVIOUS_CONTENT = bool; }
    private static boolean REMOVE_INNOCENT_ENTITY = false;
    public static void setRemoveInnocentEntity(boolean bool) { REMOVE_INNOCENT_ENTITY = bool; }
    private static boolean HAS_BLOCK_FILTER = false;
    private static final LootBlockFilter blockFilter = new LootBlockFilter();
    public static void setLootBlockFilter(@NotNull List<String> whiteListRegex, @NotNull List<String> blackListRegex) {
        blockFilter.updateFilter(whiteListRegex, blackListRegex);
        HAS_BLOCK_FILTER = blockFilter.hasFilter();
    }

    private static final IGameIdReadApi gameIdReadApi = GameManager.get().getGameIdReadApi();
    private static final IGameIdWriteApi gameIdWriteApi = GameManager.get().getGameIdWriteApi();

    /**
     * 根据物资刷新配置生成
     * @param lootContext 物资刷新环境
     * @param entry 战利品配置
     */
    public static @NotNull List<ItemStack> generateLootItem(LootContext lootContext, ILootEntry entry) {
        List<ItemStack> lootItems = new ArrayList<>();

        List<ILootData> lootData = entry.generateLootData(lootContext);
        if (lootData.isEmpty()) {
            return lootItems;
        }

        for (ILootData data : lootData) {
            if (data.getDataType() == LootDataType.ITEM) {
                IItemLootData itemData = (IItemLootData) data;
                ItemStack itemStack = itemData.getItemStack();
                if (itemStack == null) {
                    continue;
                }
                gameIdWriteApi.addGameId(itemStack, lootContext.gameId);
                lootItems.add(itemStack);
            }
        }

        return lootItems;
    }
    public static @NotNull List<Entity> generateLootEntities(LootContext lootContext, ILootEntry entry) {
        List<Entity> lootEntities = new ArrayList<>();

        List<ILootData> lootData = entry.generateLootData(lootContext);
        if (lootData.isEmpty()) {
            return lootEntities;
        }

        for (ILootData data : lootData) {
            if (data.getDataType() == LootDataType.ENTITY) {
                IEntityLootData entityLootData = (IEntityLootData) data;
                int count = entityLootData.getCount();
                for (int i = 0; i < count; i++) {
                    Entity entity = entityLootData.getEntity(lootContext.serverLevel);
                    if (entity == null) break;

                    gameIdWriteApi.addGameId(entity, lootContext.gameId);
                    lootEntities.add(entity);
                }
            }
        }

        return lootEntities;
    }

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
            if (CLEAR_PREVIOUS_CONTENT) {
                container.clearContent();
            }
            for (int i = 0; i < lootData.size() && i < container.getContainerSize(); i++) {
                ILootData data = lootData.get(i);
                if (data.getDataType() == LootDataType.ITEM) {
                    IItemLootData itemData = (IItemLootData) data;
                    ItemStack itemStack = itemData.getItemStack();
                    if (itemStack == null) {
                        continue;
                    }
                    gameIdWriteApi.addGameId(itemStack, lootContext.gameId);
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
                    generateLootEntities(lootContext, (IEntityLootData) data, spawnOrigin);
                } else {
                    BattleRoyale.LOGGER.warn("Ignore spawn non-entity at {}", spawnOrigin);
                }
            }
        }
    }

    /**
     * 返回成功生成的实体数量
     */
    public static int generateLootEntities(LootContext lootContext, IEntityLootData entityData, BlockPos spawnOrigin) {
        int count = entityData.getCount();
        int range = entityData.getRange();
        int attempts = entityData.getAttempts();
        int generatedCount = 0;
        for (int j = 0; j < count; j++) {
            Entity entity = entityData.getEntity(lootContext.serverLevel); // 每次getEntity会自动绑定新UUID
            if (entity == null) {
                BattleRoyale.LOGGER.debug("Failed to generate entity at BlockPos:{}, count:{}/{}", spawnOrigin, j+1, count);
                continue;
            }
            gameIdWriteApi.addGameId(entity, lootContext.gameId);
            BlockPos spawnPos = findValidSpawnPosition(lootContext, spawnOrigin, range, attempts);
            entity.setPos(spawnPos.getX() + 0.5F, spawnPos.getY(), spawnPos.getZ() + 0.5F);
            if (lootContext.serverLevel.addFreshEntity(entity)) {
                generatedCount++;
            }
        }
        return generatedCount;
    }

    /**
     * 刷新原版箱子
     */
    public static void generateVanillaLoot(LootContext lootContext, BlockEntity targetBlockEntity, ILootEntry entry) {
        if (REMOVE_LOOT_TABLE) {
            removeLootTable(targetBlockEntity);
        }

        // 执行刷新
        List<ILootData> lootData = entry.generateLootData(lootContext, targetBlockEntity);
        if (lootData.isEmpty() || !(targetBlockEntity instanceof Container container)) {
            return;
        }
        // 非容器方块实体至此结束

        // 容器方块实体
        if (CLEAR_PREVIOUS_CONTENT) {
            container.clearContent();
        }
        for (int i = 0; i < lootData.size() && i < container.getContainerSize(); i++) {
            ILootData data = lootData.get(i);
            if (data.getDataType() == LootDataType.ITEM) {
                IItemLootData itemData = (IItemLootData) data;
                ItemStack itemStack = itemData.getItemStack();
                if (itemStack == null) {
                    continue;
                }
                gameIdWriteApi.addGameId(itemStack, lootContext.gameId);
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
    private static BlockPos findValidSpawnPosition(LootContext lootContext, BlockPos centerPos, int range, int attempts) {
        for (int i = 0; i < attempts; i++) {
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
        if (blockEntity instanceof ILootObject lootObject) { // 本模组方块
            UUID blockGameId = lootObject.getGameId();
            LootConfig config = LootConfigManager.get().getLootConfig(blockEntity, lootObject.getConfigId());

            if (config != null && (blockGameId == null || !blockGameId.equals(lootContext.gameId))) {
                ILootEntry entry = config.entry;
                generateLoot(lootContext, (AbstractLootBlockEntity) lootObject, entry);
                lootObject.setGameId(lootContext.gameId);
                // blockEntity.setChanged(); // setGameId内部已经标记
                return true;
            }
        } else if (LOOT_VANILLA_CHEST) { // 原版方块
            if (!LOOT_ANY_BLOCK_ENTITY && !(blockEntity instanceof Container)) {
                return false;
            }
            LootConfig config = LootConfigManager.get().getDefaultConfig();
            if (config == null ||
                    (HAS_BLOCK_FILTER && !blockFilter.shouldLoot(blockEntity))) {
                return false;
            }
            UUID blockGameId = gameIdReadApi.getGameId(blockEntity);
            if (blockGameId == null || !blockGameId.equals(lootContext.gameId)) {
                ILootEntry entry = config.entry;
                generateVanillaLoot(lootContext, blockEntity, entry);
                gameIdWriteApi.addGameId(blockEntity, lootContext.gameId);
                // blockEntity.setChanged(); // addGameId内部已经标记
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新指定区块内的所有战利品方块。
     * @return 该区块内刷新的战利品方块数量，返回 CHUNK_NOT_LOADED 则为区块未加载
     */
    public static int refreshLootInChunk(LootContext lootContext) {
        int refreshedCount = 0;
        LevelChunk chunk = lootContext.serverLevel.getChunkSource().getChunkNow(lootContext.chunkPos.x, lootContext.chunkPos.z);

        if (chunk == null) { // 区块未加载不算作已刷新，返回特殊标记
            return CHUNK_NOT_LOADED;
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
            UUID entityGameId = gameIdReadApi.getGameId(entity);
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
        // nbt.remove("LootTable"); // 无效
        // nbt.remove("LootTableSeed");
        if (nbt.contains("LootTable")) {
            nbt.putString("LootTable", ""); // 必须为字符串类型，空字符串会解析为"minecraft:"，否则写不进去
            // nbt.putLong("LootTableSeed", 0L); // LootTable解析不出来之后就已经没有LootTableSeed了
        }
        blockEntity.load(nbt); // 写入内存，没写入硬盘
        blockEntity.setChanged(); // 防止区块被卸载前还没交互
        // 之后LootEntry能读取到的已经是"LootTable":"minecraft:"
    }

    public static class LootContext {
        public @NotNull final ServerLevel serverLevel;
        public final ChunkPos chunkPos;
        public final UUID gameId;
        public final Supplier<Float> random;
        public LootContext(@NotNull ServerLevel serverLevel, ChunkPos chunkPos, UUID gameId) {
            this.serverLevel = serverLevel;
            this.chunkPos = chunkPos;
            this.gameId = gameId;
            this.random = () -> serverLevel.getRandom().nextFloat();
        }
    }

    public static class LootBlockFilter {
        private List<Pattern> whiteListPatterns = new ArrayList<>();
        private List<Pattern> blackListPatterns = new ArrayList<>();
        public boolean shouldLoot(BlockEntity blockEntity) {
            ResourceLocation rl = BattleRoyale.getMcRegistry().getBlockRl(blockEntity.getBlockState().getBlock());
            String blockId = rl != null ? rl.toString() : "";

            // 如果白名单不为空，则必须匹配至少一个白名单项
            if (!whiteListPatterns.isEmpty()) {
                boolean matchesWhiteList = whiteListPatterns.stream()
                        .anyMatch(pattern -> pattern.matcher(blockId).matches());
                if (!matchesWhiteList) {
                    return false;
                }
            }

            // 如果匹配到任何一个黑名单项，则不刷新
            boolean matchesBlackList = blackListPatterns.stream()
                    .anyMatch(pattern -> pattern.matcher(blockId).matches());
            return !matchesBlackList;
        }
        public boolean hasFilter() {
            return !whiteListPatterns.isEmpty() || !blackListPatterns.isEmpty();
        }
        public void updateFilter(@NotNull List<String> whiteListRegex, @NotNull List<String> blackListRegex) {
            this.whiteListPatterns = whiteListRegex.stream()
                    .map(Pattern::compile)
                    .collect(Collectors.toList());
            this.blackListPatterns = blackListRegex.stream()
                    .map(Pattern::compile)
                    .collect(Collectors.toList());
        }
    }
}