package xiao.battleroyale.compat.fabric.minecraft;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.minecraft.IMcRegistry;

public class FabricRegistry implements IMcRegistry {

    @Override public ResourceLocation createResourceLocation(String rlString) {
        return new ResourceLocation(rlString);
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
        return BuiltInRegistries.ENTITY_TYPE.getOptional(rl).orElse(null);
    }
    @Override public @Nullable ResourceLocation getEntityTypeRl(EntityType entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    @Override public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}