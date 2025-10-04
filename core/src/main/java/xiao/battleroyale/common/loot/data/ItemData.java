package xiao.battleroyale.common.loot.data;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.item.IItemLootData;

public class ItemData implements IItemLootData {
    private final @Nullable Item item;
    private final @NotNull CompoundTag nbt;
    private final int count;
    private static final String EMPTY_RL = "minecraft:air";
    private static final String EMPTY_TYPE = "air";
    private final boolean isEmpty;

    public ItemData(String rl, @NotNull CompoundTag nbt, int count) {
        this.item = BattleRoyale.getMcRegistry().getItem(BattleRoyale.getMcRegistry().createResourceLocation(rl));
        this.isEmpty = this.item == null
                || (this.item.toString().equals(EMPTY_TYPE) && !rl.equals(EMPTY_RL));
        if (this.item == null) {
            BattleRoyale.LOGGER.warn("Faild to get item type from ResourceLocation {}", rl);
        }
        this.nbt = nbt;
        this.count = count; // 原版已经处理小于等于0
    }

    @Nullable
    @Override
    public ItemStack getItemStack() {
        if (this.isEmpty()) {
            return null;
        }
        ItemStack itemStack = new ItemStack(this.item, this.count);
        if (!this.nbt.isEmpty()) {
            CustomData customData = CustomData.of(this.nbt);
            itemStack.set(DataComponents.CUSTOM_DATA, customData);
        }
        return itemStack;
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty;
    }
}
