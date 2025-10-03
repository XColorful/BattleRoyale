package xiao.battleroyale.init.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import xiao.battleroyale.BattleRoyale;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> SAFE_ZONE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:safezone", BattleRoyale.MOD_ID)));
    public static final ResourceKey<DamageType> UNSAFE_ZONE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:unsafezone", BattleRoyale.MOD_ID)));

    public static DamageSource safeZone(ServerLevel serverLevel) {
        return new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SAFE_ZONE_DAMAGE));
    }

    public static DamageSource unsafeZone(ServerLevel serverLevel) {
        return new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(UNSAFE_ZONE_DAMAGE));
    }

    public static boolean isZoneDamage(DamageSource damageSource) {
        return damageSource.is(SAFE_ZONE_DAMAGE) || damageSource.is(UNSAFE_ZONE_DAMAGE);
    }
}
