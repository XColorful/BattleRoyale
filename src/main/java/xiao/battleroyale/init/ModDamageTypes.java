package xiao.battleroyale.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import xiao.battleroyale.BattleRoyale;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> SAFE_ZONE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(BattleRoyale.MOD_ID, "safezone"));
    public static final ResourceKey<DamageType> UNSAFE_ZONE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(BattleRoyale.MOD_ID, "unsafezone"));

    public static DamageSource safeZone(ServerLevel serverLevel) {
        return new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SAFE_ZONE_DAMAGE));
    }

    public static DamageSource unsafeZone(ServerLevel serverLevel) {
        return new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(UNSAFE_ZONE_DAMAGE));
    }
}
