package xiao.battleroyale.api.loot.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootDataType;

import javax.annotation.Nullable;

public interface IEntityLootData extends ILootData {
    @Override
    default LootDataType getDataType() {
        return LootDataType.ENTITY;
    }

    int getCount();

    int getRange();

    @Nullable
    Entity getEntity(ServerLevel level);
}
