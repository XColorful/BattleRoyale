package xiao.battleroyale.compat.neoforge.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import xiao.battleroyale.api.minecraft.IMcRegistry;

import javax.annotation.Nullable;

public class NeoRegistry implements IMcRegistry {

    @Override
    public ResourceLocation createResourceLocation(String rlString) {
        return ResourceLocation.parse(rlString);
    }

    @Override public @Nullable Block getBlock(ResourceLocation rl) {
        return BuiltInRegistries.BLOCK.get(rl);
    }
    @Override public @Nullable ResourceLocation getBlockRl(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
    @Override public @Nullable ParticleType<?> getParticleType(ResourceLocation rl) {
        return BuiltInRegistries.PARTICLE_TYPE.get(rl);
    }
    @Override public @Nullable ResourceLocation getParticleTypeRl(ParticleType<?> particleType) {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);
    }
    @Override public @Nullable MobEffect getMobEffect(ResourceLocation rl) {
        return BuiltInRegistries.MOB_EFFECT.get(rl);
    }
    @Override public @Nullable ResourceLocation getMobEffectRl(MobEffect mobEffect) {
        return BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
    }
    @Override public @Nullable Item getItem(ResourceLocation rl) {
        return BuiltInRegistries.ITEM.get(rl);
    }
    @Override public @Nullable ResourceLocation getItemRl(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
    @Override public @Nullable EntityType<?> getEntityType(ResourceLocation rl) {
        return BuiltInRegistries.ENTITY_TYPE.get(rl);
    }
    @Override public @Nullable ResourceLocation getEntityTypeRl(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}