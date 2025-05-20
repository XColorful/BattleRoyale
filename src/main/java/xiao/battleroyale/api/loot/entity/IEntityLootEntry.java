package xiao.battleroyale.api.loot.entity;

import xiao.battleroyale.api.loot.ILootEntry;
import net.minecraft.world.entity.Entity;
import java.util.List;
import java.util.function.Supplier;

public interface IEntityLootEntry extends ILootEntry<Entity> {
    @Override
    List<Entity> generateLoot(Supplier<Float> random);

    int getRange();
}