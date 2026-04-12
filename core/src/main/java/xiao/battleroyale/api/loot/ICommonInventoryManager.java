package xiao.battleroyale.api.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.List;

public interface ICommonInventoryManager extends IInventoryManager, ILootStatus, ICommonLootOperator {

    boolean resetInventoryWithLoot(@Nullable CommandSourceStack source, ServerLevel serverLevel, ServerPlayer player, int lootId);
    boolean resetInventoryWithLoot(Inventory inventory, LootGenerator.LootContext lootContext, ILootEntry entry);

    int resetInventoryWithLoot(@Nullable CommandSourceStack source, ServerLevel serverLevel, List<ServerPlayer> players, int lootId);
    int resetInventoryWithLoot(List<Inventory> inventories, LootGenerator.LootContext lootContext, ILootEntry entry);


    boolean inventoryLootGeneration(@Nullable CommandSourceStack source, ServerLevel serverLevel,
                                    ServerPlayer player, int lootId, int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace);
    boolean inventoryLootGeneration(ServerPlayer player, LootGenerator.LootContext lootContext, ILootEntry entry,
                                    int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace);

    int inventoryLootGeneration(@Nullable CommandSourceStack source, ServerLevel serverLevel,
                                List<ServerPlayer> players, int lootId, int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace);
    int inventoryLootGeneration(List<ServerPlayer> players, LootGenerator.LootContext lootContext, ILootEntry entry,
                                int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace);
}
