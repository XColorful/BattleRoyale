package xiao.battleroyale.common.loot.data;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.item.IItemLootData;
import xiao.battleroyale.util.NBTUtils;

public class ItemData implements IItemLootData {
    private final @Nullable Item item;
    private final @NotNull CompoundTag nbt;
    private final int count;

    public ItemData(String rl, @Nullable String nbt, int count) {
        this.item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(rl));
        if (this.item == null && !rl.isEmpty()) {
            BattleRoyale.LOGGER.warn("Faild to get item type from ResourceLocation {}", rl);
        }
        this.nbt = NBTUtils.stringToNBT(nbt);
        this.count = count; // 原版已经处理小于等于0
    }

    @Nullable
    @Override
    public ItemStack getItemStack() {
        if (this.item == null) {
            return null;
        }
        ItemStack itemStack = new ItemStack(this.item, this.count);
        if (!this.nbt.isEmpty()) {
            itemStack.setTag(this.nbt);
        }
        return itemStack;
    }

    @Override
    public boolean isEmpty() {
        return this.item == null;
    }
}
