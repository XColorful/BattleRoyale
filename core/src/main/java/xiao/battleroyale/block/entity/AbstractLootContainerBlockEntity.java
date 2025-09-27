package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractLootContainerBlockEntity extends AbstractLootBlockEntity implements Container, Clearable {
    private static final String ITEMS_TAG = "Items";
    private static final String SLOT_TAG = "Slot";
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
    public @NotNull ItemStack getItem(int index) {
        if (index >= 0 && index < this.items.size()) {
            return this.items.get(index);
        }
        return ItemStack.EMPTY;
    }

    public void sendBlockUpdated() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack itemStack = this.items.get(index);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = itemStack.split(count);
        if (itemStack.isEmpty()) {
            this.items.set(index, ItemStack.EMPTY);
        }
        this.setChanged();
        sendBlockUpdated();
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        ItemStack itemStack = this.items.get(index);
        this.items.set(index, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        this.items.set(index, stack);
        this.setChanged();
        sendBlockUpdated();
    }

    public void setItemNoUpdate(int index, ItemStack stack) {
        this.items.set(index, stack);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
        sendBlockUpdated();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ITEMS_TAG, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(ITEMS_TAG, Tag.TAG_COMPOUND);
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getInt(SLOT_TAG);
                if (slot >= 0 && slot < this.items.size()) {
                    this.items.set(slot, ItemStack.of(itemTag));
                }
            }
        }
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt(SLOT_TAG, i);
                this.items.get(i).save(itemTag);
                listTag.add(itemTag);
            }
        }
        pTag.put(ITEMS_TAG, listTag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt(SLOT_TAG, i);
                this.items.get(i).save(itemTag);
                listTag.add(itemTag);
            }
        }
        tag.put(ITEMS_TAG, listTag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.load(tag);
        }
        // 不需要额外触发重绘，Minecraft 会自动处理 BlockEntity 数据更新后的渲染刷新
    }
}