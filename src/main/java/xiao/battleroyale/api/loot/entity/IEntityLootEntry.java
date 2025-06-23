package xiao.battleroyale.api.loot.entity;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import java.util.List;
import java.util.function.Supplier;

public interface IEntityLootEntry extends ILootEntry {
    @Override
    @NotNull
    List<ILootData> generateLootData(Supplier<Float> random);
}