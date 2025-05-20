package xiao.battleroyale.api.loot;

import java.util.UUID;

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
     * 获取此战利品方块所属的大逃杀游戏 UUID。
     * 当区块加载时，会根据这个 UUID 与当前游戏 UUID 比较，不一致则刷新。
     *
     * @return 大逃杀游戏 UUID。
     */
    UUID getGameId();

    /**
     * 设置此战利品方块所属的大逃杀游戏 UUID。
     *
     * @param gameId 要设置的大逃杀游戏 UUID。
     */
    void setGameId(UUID gameId);

    // 可以添加其他通用的方法，例如获取颜色、名称等
}