package xiao.battleroyale.api.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IBlock {
    Identifier getBlockId(ItemStack block);

    void setBlockId(ItemStack block, @Nullable Identifier blockId);
}
