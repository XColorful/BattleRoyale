package xiao.battleroyale.api.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public interface IMcRegistry {

    Identifier createResourceLocation(String rlString);

    @Nullable Block getBlock(Identifier rl);
    @Nullable Identifier getBlockRl(Block block);
    @Nullable ParticleType<?> getParticleType(Identifier rl);
    @Nullable Identifier getParticleTypeRl(ParticleType<?> particleType);
    @Nullable MobEffect getMobEffect(Identifier rl);
    @Nullable Identifier getMobEffectRl(MobEffect mobEffect);
    @Nullable Item getItem(Identifier rl);
    @Nullable Identifier getItemRl(Item item);
    @Nullable EntityType<?> getEntityType(Identifier rl);
    @Nullable Identifier getEntityTypeRl(EntityType<?> entityType);

    boolean isModLoaded(String modId);
}
