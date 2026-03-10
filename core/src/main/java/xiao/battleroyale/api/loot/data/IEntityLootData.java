package xiao.battleroyale.api.loot.data;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IEntityLootData extends ILootData {
    @Override
    default LootDataType getDataType() {
        return LootDataType.ENTITY;
    }

    int getCount();

    int getRange();

    int getAttempts();

    @Nullable
    Entity getEntity(ServerLevel serverLevel);
}
