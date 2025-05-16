package xiao.battleroyale.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IBlock {
    ResourceLocation getBlockId(ItemStack block);

    void setBlockId(ItemStack block, @Nullable ResourceLocation blockId);
}
