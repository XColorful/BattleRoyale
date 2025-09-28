package xiao.battleroyale.api.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public interface IMcRegistry {

    @Nullable Block getBlock(ResourceLocation rl);
    @Nullable ResourceLocation getBlockRl(Block block);
    @Nullable ParticleType<?> getParticleType(ResourceLocation rl);
    @Nullable ResourceLocation getParticleTypeRl(ParticleType<?> particleType);
    @Nullable MobEffect getMobEffect(ResourceLocation rl);
    @Nullable ResourceLocation getMobEffectRl(MobEffect mobEffect);
    @Nullable Item getItem(ResourceLocation rl);
    @Nullable ResourceLocation getItemRl(Item item);
    @Nullable EntityType<?> getEntityType(ResourceLocation rl);
    @Nullable ResourceLocation getEntityTypeRl(EntityType<?> entityType);

    boolean isModLoaded(String modId);
}
