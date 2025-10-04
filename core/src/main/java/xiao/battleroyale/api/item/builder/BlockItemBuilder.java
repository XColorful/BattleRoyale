package xiao.battleroyale.api.item.builder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class BlockItemBuilder {
    private int count = 1;
    private final CompoundTag nbt = new CompoundTag(); // 内部维护一个 CompoundTag

    private final ItemLike blockItem;

    private BlockItemBuilder(ItemLike blockItem) {
        this.blockItem = blockItem;
    }

    public static BlockItemBuilder create(ItemLike blockItem) {
        return new BlockItemBuilder(blockItem);
    }

    public BlockItemBuilder setCount(int count) {
        this.count = Math.max(count, 1);
        return this;
    }

    public BlockItemBuilder withNBT(Consumer<CompoundTag> consumer) {
        consumer.accept(this.nbt);
        return this;
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(blockItem, this.count);
        if (!this.nbt.isEmpty()) {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(this.nbt.copy()));
        }
        return stack;
    }
}