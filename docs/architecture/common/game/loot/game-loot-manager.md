[English](#English)

## 游戏物资刷新管理器

[![IGameLootManager](/docs/api/game/loot/IGameLootManager.md)](/docs/api/game/loot/IGameLootManager.md)

#### 物资刷新器配置

- 本模组默认物资刷新器使用异步BFS+单tick限制刷新区块数（均摊计算量）以提高性能
> - 以各玩家位置为BFS初始队列逐层遍历，使刷新尽可能公平
> - 需要在服务器关闭时安全处理额外线程

[![IGameLootConfigGetter](/docs/api/game/loot/IGameLootConfigGetter.md)](/docs/api/game/loot/IGameLootConfigGetter.md)

[![IGameLootOperator](/docs/api/game/loot/IGameLootOperator.md)](/docs/api/game/loot/IGameLootOperator.md)

# English

## Game Loot Manager

[![IGameLootManager](/docs/api/game/loot/IGameLootManager.md)](/docs/api/game/loot/IGameLootManager.md)

#### Loot Generator Configuration

- The mod's default loot generator uses asynchronous BFS + a single-tick chunk generation limit (distributing calculation load) to improve performance.
> - Iterates layer by layer, starting from each player's position as the BFS initial queue, to ensure the refresh is as fair as possible.
> - Requires safe handling of extra threads upon server shutdown

[![IGameLootConfigGetter](/docs/api/game/loot/IGameLootConfigGetter.md)](/docs/api/game/loot/IGameLootConfigGetter.md)

[![IGameLootOperator](/docs/api/game/loot/IGameLootOperator.md)](/docs/api/game/loot/IGameLootOperator.md)