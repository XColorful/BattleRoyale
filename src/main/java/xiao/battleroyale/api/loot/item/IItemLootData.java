package xiao.battleroyale.api.loot.item;

import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootDataType;

public interface IItemLootData extends ILootData {
    @Override
    default LootDataType getType() {
        return LootDataType.ITEM;
    }

    ItemStack getItemStack();
}