package xiao.battleroyale.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import xiao.battleroyale.BattleRoyale;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> ZONE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(BattleRoyale.MOD_ID, "zone"));

    public static DamageSource zone(ServerLevel serverLevel) {
        return new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ZONE_DAMAGE));
    }
}
