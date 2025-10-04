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
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLootMenu extends AbstractContainerMenu {

    protected final Container lootContainer;
    protected final BlockEntity blockEntity;
    protected final Level level;

    protected AbstractLootMenu(MenuType<?> type, int id, Inventory playerInventory, Container lootContainer, BlockEntity blockEntity) {
        super(type, id);
        this.lootContainer = lootContainer;
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();

        addLootContainer(lootContainer);
        addPlayerInventory(playerInventory);
        // ↓不调用就会导致拿起物品后界面清空（物品还在容器里，界面里也能取出来）
        if (!playerInventory.player.level().isClientSide) {
            // this.broadcastChanges();
            // ↑每次只更新一个（有问题），用下面这个
            this.sendAllDataToRemote();
        }
    }

    protected abstract void addLootContainer(Container lootContainer);

    protected abstract void addPlayerInventory(Inventory playerInventory);

    protected void addLootContainer(Container lootContainer, int offset) {
        int index = 0;
        int size = lootContainer.getContainerSize();
        int containerRows = size / 9;
        int containerCols = Math.min(size, 9);
        for (int row = 0; row < containerRows; row++) {
            for (int col = 0; col < containerCols; col++, index++) {
                if (index >= size) {
                    break;
                }
                this.addSlot(new Slot(lootContainer, index, 8 + col * 18, offset + row * 18));
            }
        }
    }

    protected void addPlayerInventory(Inventory playerInventory, int offset) {
        // 玩家物品槽位
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = 9 + col + row * 9;
                this.addSlot(new Slot(playerInventory, index, 8 + col * 18, offset + row * 18));
            }
        }
        // 玩家快捷栏槽位
        for (int row = 3; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col;
                this.addSlot(new Slot(playerInventory, index, 8 + col * 18, offset + row * 18 + 4));
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
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
    public boolean stillValid(@NotNull Player player) {
        return this.blockEntity != null && player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5D, this.blockEntity.getBlockPos().getY() + 0.5D, this.blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
    }
}