package xiao.battleroyale.common.loot;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.api.loot.ILootEntry;

import java.util.List;

public class InventoryGenerator {

    // 清空背包并填充
    public static void generateInventoryLoot(Inventory inventory, LootGenerator.LootContext lootContext, ILootEntry entry) {
        inventory.clearContent();
        List<ItemStack> lootItems = LootGenerator.generateLootItem(lootContext, entry);
        int lastIndex = Math.min(inventory.getContainerSize(), lootItems.size()); // 默认遍历的时候背包大小不变
        for (int i = 0; i < lastIndex; i++) {
            inventory.setItem(i, lootItems.get(i));
        }
    }

    public static void lootItemsToPlayerInventory(ServerPlayer player, List<ItemStack> lootItems, int firstIndex, int lastIndex, boolean skipNonEmptySlot, boolean dropBeforeReplace) {
        if (lootItems.isEmpty()) {
            return;
        }
        int lootItemIndex = 0;
        Inventory inventory = player.getInventory();

        firstIndex = Math.max(0, firstIndex);
        lastIndex = Math.min(lastIndex, inventory.getContainerSize() - 1);
        for (int i = firstIndex; i <= lastIndex; i++) {
            ItemStack slot = inventory.getItem(i);
            boolean isSlotEmpty = slot.isEmpty();
            // 跳过非空槽
            if (skipNonEmptySlot
                    && !isSlotEmpty) {
                continue;
            }
            if (lootItemIndex >= lootItems.size()) {
                break;
            }
            ItemStack lootItem = lootItems.get(lootItemIndex++);
            // 覆盖前先吐出来
            if (dropBeforeReplace
                    && !isSlotEmpty) {
                player.drop(slot, true, false);
            }
            inventory.setItem(i, lootItem);
        }
    }
}
