package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractLootContainerBlockEntity extends AbstractLootBlockEntity implements Container, Clearable {
    private static final String ITEMS_TAG = "Items";
    protected NonNullList<ItemStack> items;

    protected AbstractLootContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int size) {
        super(type, pos, blockState);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index >= 0 && index < this.items.size()) {
            return this.items.get(index);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemStack = this.items.get(index);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = itemStack.split(count);
        if (itemStack.isEmpty()) {
            this.items.set(index, ItemStack.EMPTY);
        }
        this.setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemStack = this.items.get(index);
        this.items.set(index, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        this.setChanged();
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ITEMS_TAG, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(ITEMS_TAG, Tag.TAG_COMPOUND);
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getInt("Slot");
                if (slot >= 0 && slot < this.items.size()) {
                    this.items.set(slot, ItemStack.of(itemTag));
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                this.items.get(i).save(itemTag);
                listTag.add(itemTag);
            }
        }
        tag.put(ITEMS_TAG, listTag);
    }
}