package xiao.battleroyale.compat.forge.minecraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import xiao.battleroyale.api.minecraft.IMcRegistry;

public class ForgeRegistry implements IMcRegistry {

    @Override
    public Block getBlock(ResourceLocation rl) {
        return ForgeRegistries.BLOCKS.getValue(rl);
    }
}
