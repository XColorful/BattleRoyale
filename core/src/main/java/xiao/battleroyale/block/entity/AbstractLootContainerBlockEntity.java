package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractLootContainerBlockEntity extends AbstractLootBlockEntity implements Container, Clearable {
    private static final String ITEMS_TAG = "Items";
    private static final String SLOT_TAG = "Slot";
    protected NonNullList<ItemStack> items; // 参考NeoForge的ItemStackHandler

    protected AbstractLootContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int size) {
        super(type, pos, blockState);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public static int getLootContainerSize() {
        return 100;
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
        if (this.level != null && !this.level.isClientSide()) {
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
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.items.replaceAll(ignored -> ItemStack.EMPTY);
        input.listOrEmpty(ITEMS_TAG, ItemStackWithSlot.CODEC).forEach(itemWithSlot -> {
            if (itemWithSlot.isValidInContainer(this.items.size())) {
                this.items.set(itemWithSlot.slot(), itemWithSlot.stack());
            }
        });
    }


    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        ValueOutput.TypedOutputList<ItemStackWithSlot> itemList = output.list(ITEMS_TAG, ItemStackWithSlot.CODEC);

        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                itemList.add(new ItemStackWithSlot(i, stack));
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider p_329179_) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, p_329179_);
        saveAdditional(output);
        return output.buildResult();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}