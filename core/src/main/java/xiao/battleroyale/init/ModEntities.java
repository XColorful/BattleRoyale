package xiao.battleroyale.init;

import net.minecraft.world.entity.EntityType;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.IRegistrar;

public class ModEntities {
    public static final IRegistrar<EntityType<?>> ENTITY_TYPES =
            BattleRoyale.getRegistrarFactory().createEntityTypes(BattleRoyale.MOD_ID);
}