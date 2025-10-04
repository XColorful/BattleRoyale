package xiao.battleroyale.compat.forge.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import xiao.battleroyale.api.minecraft.IMcRegistry;

import javax.annotation.Nullable;

public class ForgeRegistry implements IMcRegistry {

    @Override public ResourceLocation createResourceLocation(String rlString) {
        return ResourceLocation.parse(rlString);
    }

    @Override public @Nullable Block getBlock(ResourceLocation rl) {
        return ForgeRegistries.BLOCKS.getValue(rl);
    }
    @Override public @Nullable ResourceLocation getBlockRl(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
    @Override public @Nullable ParticleType<?> getParticleType(ResourceLocation rl) {
        return ForgeRegistries.PARTICLE_TYPES.getValue(rl);
    }
    @Override public @Nullable ResourceLocation getParticleTypeRl(ParticleType<?> particleType) {
        return ForgeRegistries.PARTICLE_TYPES.getKey(particleType);
    }
    @Override public @Nullable MobEffect getMobEffect(ResourceLocation rl) {
        return ForgeRegistries.MOB_EFFECTS.getValue(rl);
    }
    @Override public @Nullable ResourceLocation getMobEffectRl(MobEffect mobEffect) {
        return ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
    }
    @Override public @Nullable Item getItem(ResourceLocation rl) {
        return ForgeRegistries.ITEMS.getValue(rl);
    }
    @Override public @Nullable ResourceLocation getItemRl(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }
    @Override public @Nullable EntityType<?> getEntityType(ResourceLocation rl) {
        return ForgeRegistries.ENTITY_TYPES.getValue(rl); // 无论输入不存在的RL还是null都不会返回null，小Forge露出猪脚了吧[doge]
    }
    @Override public @Nullable ResourceLocation getEntityTypeRl(EntityType entityType) {
        return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
