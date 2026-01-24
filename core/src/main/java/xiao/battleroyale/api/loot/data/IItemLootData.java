package xiao.battleroyale.api.loot.data;

import net.minecraft.world.item.ItemStack;

public interface IItemLootData extends ILootData {
    @Override
    default LootDataType getDataType() {
        return LootDataType.ITEM;
    }

    ItemStack getItemStack();
}