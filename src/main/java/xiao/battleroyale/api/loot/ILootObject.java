package xiao.battleroyale.api.loot;

import net.minecraft.resources.ResourceLocation;

public interface ILootObject {

    /**
     * 获取与此战利品对象关联的配置 ID。
     * 这个 ID 用于从 LootConfigManager 中查找具体的战利品配置。
     *
     * @return 配置 ID (通常是 LootConfig 的 id 字段)。
     */
    int getConfigId();

    /**
     * 设置与此战利品对象关联的配置 ID。
     *
     * @param configId 要设置的配置 ID。
     */
    void setConfigId(int configId);

    /**
     * 获取此战利品对象的唯一标识符。
     * 这可能对应于 LootConfig 中的 id 或其他唯一标识。
     *
     * @return 唯一标识符。
     */
    ResourceLocation getLootObjectId();

    /**
     * 设置此战利品对象的唯一标识符。
     *
     * @param lootObjectId 要设置的唯一标识符。
     */
    void setLootObjectId(ResourceLocation lootObjectId);

    // 可以添加其他通用的方法，例如获取颜色、名称等
}