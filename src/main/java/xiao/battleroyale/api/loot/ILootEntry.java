package xiao.battleroyale.api.loot;

import java.util.List;
import java.util.function.Supplier;
import com.google.gson.JsonObject;

public interface ILootEntry {
    /**
     * 根据配置生成战利品列表.
     * @param random 提供随机数的 Supplier
     * @return 生成的战利品列表
     */
    List<ILootData> generateLootData(Supplier<Float> random);

    /**
     * 获取当前 LootEntry 的类型，用于 JSON 反序列化.
     * @return LootEntry 的类型名称
     */
    String getType();

    /**
     * 将当前 LootEntry 序列化为 JSON 对象，用于配置存储和编辑.
     * @return 包含 LootEntry 配置的 JSON 对象
     */
    JsonObject toJson();
}