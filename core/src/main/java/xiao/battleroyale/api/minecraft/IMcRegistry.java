package xiao.battleroyale.api.minecraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface IMcRegistry {

    Block getBlock(ResourceLocation rl);
}
