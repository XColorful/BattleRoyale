package xiao.battleroyale.common.server.utility.lootconfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.config.common.loot.item.IItemLootEntry;
import xiao.battleroyale.api.game.IGameIdWriteApi;
import xiao.battleroyale.algorithm.BfsCalculator;
import xiao.battleroyale.algorithm.BfsCalculator.Offset2D;
import xiao.battleroyale.common.loot.data.ItemData;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.RepeatEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry.WeightedEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static xiao.battleroyale.command.CommandArg.SLOT;
import static xiao.battleroyale.command.CommandArg.BLOCK;
import static xiao.battleroyale.command.CommandArg.CHUNK;

public class LootConfigGenerator {

    public static boolean SKIP_EMPTY_SLOT = true;
    public static boolean SKIP_EMPTY_BLOCK = true;
    public static boolean SKIP_EMPTY_CHUNK = true;

    public static @Nullable LootConfig autoLootConfig(ServerLevel serverLevel,
                                         int lootId, String name,
                                         String type, Vec3 centerPos,
                                         int repeat, int weight, int radius) {
        ChunkPos centerChunk = new ChunkPos(BlockPos.containing(centerPos.x, centerPos.y, centerPos.z));
        List<ChunkPos> chunks = new ArrayList<>();
        List<List<Offset2D>> layers = BfsCalculator.calculateCenterOffset(radius);
        for (List<Offset2D> layer : layers) {
            for (Offset2D offset : layer) {
                chunks.add(new ChunkPos(centerChunk.x + offset.x(), centerChunk.z + offset.z()));
            }
        }

        List<WeightedEntry> weightedEntries = new ArrayList<>();
        for (ChunkPos chunkPos : chunks) {
            LevelChunk chunk = serverLevel.getChunk(chunkPos.x, chunkPos.z);
            // 区块列表<容器物品列表<ItemStack>>
            List<List<ItemStack>> containerGroup = getItemsFromChunk(chunk);
            switch (type) {
                // 以单个物品槽为单位构建 WeightedEntry
                case SLOT -> containerGroup.forEach(items ->
                        items.forEach(stack -> weightedEntries.add(new WeightedEntry(weight, slotBuilder(stack)))));
                // 以单个容器内物品为单位构建 WeightedEntry
                case BLOCK -> containerGroup.forEach(items -> {
                    if (!SKIP_EMPTY_BLOCK || !items.isEmpty())
                        weightedEntries.add(new WeightedEntry(weight, listBuilder(items)));
                });
                // 以单个区块所有容器内物品为单位构建 WeightedEntry
                case CHUNK -> {
                    List<ItemStack> allInChunk = containerGroup.stream().flatMap(List::stream).collect(Collectors.toList());
                    if (!SKIP_EMPTY_CHUNK || !allInChunk.isEmpty())
                        weightedEntries.add(new WeightedEntry(weight, listBuilder(allInChunk)));
                }
            }
        }

        // 没有东西就不创建配置
        if (weightedEntries.isEmpty()) return null;

        WeightEntry weightEntry = new WeightEntry(weightedEntries);
        RepeatEntry repeatEntry = new RepeatEntry(repeat, repeat, weightEntry);

        return new LootConfig(lootId, name, "#FFFFFFAA",
                repeatEntry);
    }

    private static List<List<ItemStack>> getItemsFromChunk(LevelChunk chunk) {
        List<List<ItemStack>> result = new ArrayList<>();
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof Container container) {
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack stack = container.getItem(i);
                    if (SKIP_EMPTY_SLOT && stack.isEmpty()) continue;
                    items.add(stack);
                }
                // 如果跳过空槽，则只有 items 不为空时才加入
                if (!SKIP_EMPTY_SLOT || !items.isEmpty()) {
                    result.add(items);
                }
            }
        }
        return result;
    }

    public static IGameIdWriteApi gameIdWriteApi = BattleRoyale.getGameManager().getGameIdWriteApi();
    public static IItemLootEntry slotBuilder(ItemStack itemStack) {
        // 防御一下ItemStack.EMPTY的NBT
        if (itemStack.isEmpty()) {
            return new ItemEntry(ItemData.EMPTY_RL, "{}", 0);
        }

        ResourceLocation rl = BattleRoyale.getMcRegistry().getItemRl(itemStack.getItem());
        String rlString;
        if (rl != null) {
            rlString = rl.toString();
        } else {
            rlString = ItemData.EMPTY_RL;
            BattleRoyale.LOGGER.warn("Failed to get ResourceLocation from ItemStack {}", itemStack.toString());
        }
        gameIdWriteApi.removeGameId(itemStack); // 移除gameId避免配置文件臃肿
        CompoundTag nbt = itemStack.getTag();
        String nbtString = nbt != null ? nbt.toString() : "{}";
        return new ItemEntry(rlString, nbtString, itemStack.getCount());
    }

    public static ILootEntry listBuilder(List<ItemStack> itemStacks) {
        List<ILootEntry> entries = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            entries.add(slotBuilder(itemStack));
        }
        return new MultiEntry(entries);
    }
}
