package xiao.battleroyale.common.loot;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.api.loot.ICommonInventoryManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

import java.util.List;
import java.util.UUID;

public class CommonInventoryManager implements ICommonInventoryManager {

    private static class CommonInventoryManagerHolder {
        private static final CommonInventoryManager INSTANCE = new CommonInventoryManager();
    }

    public static CommonInventoryManager get() {
        return CommonInventoryManagerHolder.INSTANCE;
    }

    protected CommonInventoryManager() {}

    public static void init(McSide mcSide) {
    }

    private static boolean ALLOW_LOOT_IN_GAME = false;
    public static void setAllowLootInGame(boolean allow) { ALLOW_LOOT_IN_GAME = allow; }
    @Override public void applyConfig(GeneratorEntry generatorEntry) {
        setAllowLootInGame(generatorEntry.allowNormalLootInGame);
    }

    @Override
    public LootStatus lootStatusCheck() {
        return !ALLOW_LOOT_IN_GAME && BattleRoyale.getGameManager().isInGame() ? LootStatus.REJECT : LootStatus.AVAILABLE;
    }

    @Override
    public LootStatus lootStatusCheck(CommandSourceStack source) {
        LootStatus status = this.lootStatusCheck();
        if (source != null) {
            switch(status) {
                case REJECT, UNAVAILABLE -> source.sendFailure(Component.translatable("battleroyale.message.inventory_loot_unavailable"));
            }
        }
        return status;
    }

    @Override
    public boolean resetInventoryWithLoot(@Nullable CommandSourceStack source, ServerLevel serverLevel, ServerPlayer player, int lootId) {
        if (this.lootStatusCheck(source) != LootStatus.AVAILABLE) return false;
        ILootEntry lootEntry = getLootEntryByLootId(lootId);
        if (lootEntry == null) return false;
        return this.resetInventory(player.getInventory(), new LootGenerator.LootContext(serverLevel, player.chunkPosition(), UUID.randomUUID()), lootEntry);
    }

    @Override
    public boolean resetInventoryWithLoot(Inventory inventory, LootGenerator.LootContext lootContext, ILootEntry entry) {
        if (this.lootStatusCheck() != LootStatus.AVAILABLE) return false;
        return resetInventory(inventory, lootContext, entry);
    }

    @Override
    public int resetInventoryWithLoot(@Nullable CommandSourceStack source, ServerLevel serverLevel, List<ServerPlayer> players, int lootId) {
        if (this.lootStatusCheck(source) != LootStatus.AVAILABLE) return -1;
        ILootEntry lootEntry = getLootEntryByLootId(lootId);
        if (lootEntry == null) {
            return -2;
        }

        UUID newGameId = UUID.randomUUID();
        int successCount = 0;
        for (ServerPlayer player : players) {
            LootGenerator.LootContext context = new LootGenerator.LootContext(serverLevel, player.chunkPosition(), newGameId);
            if (this.resetInventory(player.getInventory(), context, lootEntry)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public int resetInventoryWithLoot(List<Inventory> inventories, LootGenerator.LootContext lootContext, ILootEntry entry) {
        if (this.lootStatusCheck() != LootStatus.AVAILABLE) return -1;
        int successCount = 0;
        for (Inventory inventory : inventories) {
            if (this.resetInventory(inventory, lootContext, entry)) {
                successCount++;
            }
        }
        return successCount;
    }

    private boolean resetInventory(Inventory inventory, LootGenerator.LootContext lootContext, ILootEntry entry) {
        InventoryGenerator.generateInventoryLoot(inventory, lootContext, entry);
        return true;
    }

    @Override
    public boolean inventoryLootGeneration(@Nullable CommandSourceStack source, ServerLevel serverLevel, ServerPlayer player, int lootId,
                                           int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace) {
        if (this.lootStatusCheck(source) != LootStatus.AVAILABLE) return false;
        ILootEntry lootEntry = getLootEntryByLootId(lootId);
        if (lootEntry == null) return false;

        LootGenerator.LootContext context = new LootGenerator.LootContext(serverLevel, player.chunkPosition(), UUID.randomUUID());
        return this.inventoryLootGeneration(player, context, lootEntry, firstIndex, lastIndex, skipNonEmptySlot, dropBeforeReplace);
    }

    @Override
    public boolean inventoryLootGeneration(ServerPlayer player, LootGenerator.LootContext lootContext, ILootEntry entry,
                                           int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace) {
        if (this.lootStatusCheck() != LootStatus.AVAILABLE) return false;

        List<ItemStack> items = LootGenerator.generateLootItem(lootContext, entry);
        return this.inventoryGeneration(player, items, firstIndex, lastIndex, skipNonEmptySlot, dropBeforeReplace);
    }

    @Override
    public int inventoryLootGeneration(@Nullable CommandSourceStack source, ServerLevel serverLevel, List<ServerPlayer> players, int lootId,
                                       int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace) {
        if (this.lootStatusCheck(source) != LootStatus.AVAILABLE) return -1;
        ILootEntry lootEntry = getLootEntryByLootId(lootId);
        if (lootEntry == null) return -2;

        UUID newGameId = UUID.randomUUID();
        int successCount = 0;
        for (ServerPlayer player : players) {
            LootGenerator.LootContext context = new LootGenerator.LootContext(serverLevel, player.chunkPosition(), newGameId);
            if (this.inventoryLootGeneration(player, context, lootEntry, firstIndex, lastIndex, skipNonEmptySlot, dropBeforeReplace)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public int inventoryLootGeneration(List<ServerPlayer> players, LootGenerator.LootContext lootContext, ILootEntry entry,
                                       int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace) {
        if (this.lootStatusCheck() != LootStatus.AVAILABLE) return -1;

        int successCount = 0;
        for (ServerPlayer player : players) {
            List<ItemStack> items = LootGenerator.generateLootItem(lootContext, entry);
            if (this.inventoryGeneration(player, items, firstIndex, lastIndex, skipNonEmptySlot, dropBeforeReplace)) {
                successCount++;
            }
        }
        return successCount;
    }

    private boolean inventoryGeneration(ServerPlayer player, List<ItemStack> lootItems,
                                        int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace) {
        InventoryGenerator.lootItemsToPlayerInventory(player, lootItems, firstIndex, lastIndex, skipNonEmptySlot, dropBeforeReplace);
        return true;
    }

    private @Nullable ILootEntry getLootEntryByLootId(int lootId) {
        IConfigSubManager<?> lootConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(LootConfigManager.get().getNameKey());
        if (lootConfigManager == null) {
            BattleRoyale.LOGGER.warn("LootConfigManager is null");
            return null;
        }
        IConfigSingleEntry entry = lootConfigManager.getConfigEntry(LootConfigTypeEnum.LOOT_SPAWNER, lootId);
        if (!(entry instanceof LootConfigManager.LootConfig lootConfig)) {
            return null;
        }
        return lootConfig.entry;
    }
}