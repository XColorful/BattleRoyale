package xiao.battleroyale.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractLootMenu extends AbstractContainerMenu {

    protected final Container lootContainer;
    protected final BlockEntity blockEntity;
    protected final Level level;
    protected final int numRows;

    protected AbstractLootMenu(MenuType<?> type, int id, Inventory playerInventory, Container lootContainer, BlockEntity blockEntity, int numRows) {
        super(type, id);
        this.lootContainer = lootContainer;
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.numRows = numRows;

        int containerRows = numRows;
        int containerCols = lootContainer.getContainerSize() / numRows;

        // 添加容器的物品槽位
        for (int row = 0; row < containerRows; ++row) {
            for (int col = 0; col < containerCols; ++col) {
                this.addSlot(new Slot(lootContainer, col + row * containerCols, 8 + col * 18, 18 + row * 18));
            }
        }

        // 添加玩家的物品槽位
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, 9 + col + row * 9, 8 + col * 18, 18 + containerRows * 18 + 10 + row * 18));
            }
        }

        // 添加玩家的快捷栏槽位
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 18 + containerRows * 18 + 10 + 3 * 18 + 4));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int containerSlots = this.lootContainer.getContainerSize();
            if (index < containerSlots) {
                if (!this.moveItemStackTo(itemstack1, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null && player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5D, this.blockEntity.getBlockPos().getY() + 0.5D, this.blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
    }
}