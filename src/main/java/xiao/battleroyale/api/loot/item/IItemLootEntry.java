package xiao.battleroyale.api.loot.item;

import xiao.battleroyale.api.loot.ILootEntry;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.function.Supplier;

public interface IItemLootEntry extends ILootEntry<ItemStack> {
    @Override
    List<ItemStack> generateLoot(Supplier<Float> random);
}