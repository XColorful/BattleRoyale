package xiao.battleroyale.api.loot;

import java.util.List;
import java.util.function.Supplier;
import xiao.battleroyale.api.IConfigEntry;

public interface ILootEntry extends IConfigEntry {
    /**
     * 根据配置生成战利品列表
     * @param random 提供随机数的 Supplier
     * @return 生成的战利品列表
     */
    List<ILootData> generateLootData(Supplier<Float> random);
}