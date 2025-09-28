package xiao.battleroyale.api.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public interface IMcRegistry {

    @Nullable Block getBlock(ResourceLocation rl);
    @Nullable ResourceLocation getBlockRl(Block block);
    @Nullable ParticleType<?> getParticleType(ResourceLocation rl);
    @Nullable ResourceLocation getParticleTypeRl(ParticleType<?> particleType);
    @Nullable MobEffect getMobEffect(ResourceLocation rl);
    @Nullable ResourceLocation getMobEffectRl(MobEffect mobEffect);

    boolean isModLoaded(String modId);
}
