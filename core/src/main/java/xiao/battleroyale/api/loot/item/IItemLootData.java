package xiao.battleroyale.api.loot.item;

import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootDataType;
import xiao.battleroyale.common.loot.LootGenerator;

public interface IItemLootData extends ILootData {
    @Override
    default LootDataType getDataType() {
        return LootDataType.ITEM;
    }

    ItemStack getItemStack(LootGenerator.LootContext lootContext);
}