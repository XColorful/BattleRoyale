package xiao.battleroyale.api.loot.item;

import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import java.util.List;
import java.util.function.Supplier;

public interface IItemLootEntry extends ILootEntry {
    @Override
    List<ILootData> generateLootData(Supplier<Float> random);
}