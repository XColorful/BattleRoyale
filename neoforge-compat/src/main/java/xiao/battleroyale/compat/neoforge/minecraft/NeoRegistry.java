package xiao.battleroyale.compat.neoforge.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import xiao.battleroyale.api.minecraft.IMcRegistry;

import javax.annotation.Nullable;

public class NeoRegistry implements IMcRegistry {

    @Override
    public Identifier createResourceLocation(String rlString) {
        return Identifier.parse(rlString);
    }

    @Override public @Nullable Block getBlock(Identifier rl) {
        return BuiltInRegistries.BLOCK.getOptional(rl).orElse(null);
    }
    @Override public @Nullable Identifier getBlockRl(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
    @Override public @Nullable ParticleType<?> getParticleType(Identifier rl) {
        return BuiltInRegistries.PARTICLE_TYPE.getOptional(rl).orElse(null);
    }
    @Override public @Nullable Identifier getParticleTypeRl(ParticleType<?> particleType) {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);
    }
    @Override public @Nullable MobEffect getMobEffect(Identifier rl) {
        return BuiltInRegistries.MOB_EFFECT.getOptional(rl).orElse(null);
    }
    @Override public @Nullable Identifier getMobEffectRl(MobEffect mobEffect) {
        return BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
    }
    @Override public @Nullable Item getItem(Identifier rl) {
        return BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
    }
    @Override public @Nullable Identifier getItemRl(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
    @Override public @Nullable EntityType<?> getEntityType(Identifier rl) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(rl).orElse(null);
    }
    @Override public @Nullable Identifier getEntityTypeRl(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}