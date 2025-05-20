package xiao.battleroyale.api.item.builder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import java.util.UUID;

public class BlockItemBuilder {
    private int count = 1;
    private UUID gameId;
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

    public BlockItemBuilder setGameId(UUID gameId) {
        this.gameId = gameId;
        return this;
    }

    public BlockItemBuilder withNBT(java.util.function.Consumer<net.minecraft.nbt.CompoundTag> consumer) {
        ItemStack stack = new ItemStack(this.blockItem, this.count);
        consumer.accept(stack.getOrCreateTag());
        return this;
    }

    public ItemStack build() {
        ItemStack block = new ItemStack(blockItem ,this.count);
        if (this.gameId != null) {
            block.getOrCreateTag().putUUID("BlockGameId", this.gameId); // 保存 GameId 到物品 NBT
        }
        return block;
    }
}