package xiao.battleroyale.api.loot.data;

import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.common.loot.LootGenerator;

public interface IItemLootData extends ILootData {
    @Override
    default LootDataType getDataType() {
        return LootDataType.ITEM;
    }

    ItemStack getItemStack(LootGenerator.LootContext lootContext);
}