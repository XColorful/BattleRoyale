[English](#English)

### 游戏物资刷新管理器
> _IGameLootManager_

> _/battleroyale api IGameLootManager [...]_

#### 游戏物资配置获取
> _IGameLootConfigGetter_

##### 获取最大刷新区块数
> _/battleroyale api IGameLootManager getMaxLootChunkPerTick_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)每tick最多刷新的区块数

##### 获取最远刷新区块距离
> _/battleroyale api IGameLootManager getMaxLootDistance_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)最远刷新区块距离

##### 获取中心容忍距离
> _/battleroyale api IGameLootManager getTolerantCenterDistance_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)中心容忍距离

##### 获取缓存记录的中心数量
> _/battleroyale api IGameLootManager getMaxCachedCenter_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)缓存记录的中心数量

##### 获取最大待处理区块数
> _/battleroyale api IGameLootManager getMaxQueuedChunk_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)最大待处理区块数

##### 获取完整刷新频率
> _/battleroyale api IGameLootManager getBfsFrequency_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)完整刷新的频率

##### 获取是否立即开始下次刷新
> _/battleroyale api IGameLootManager isInstantNextBfs_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)是否立即开始下一次刷新

##### 获取缓存记录的刷新区块数量
> _/battleroyale api IGameLootManager getMaxCachedLootChunk_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)缓存记录的刷新区块数量

##### 获取清理记录的刷新区块的数量
> _/battleroyale api IGameLootManager getCleanCachedChunk_

- `返回值`：[游戏刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#游戏刷新设置)清理记录的刷新区块的数量

##### 获取刷新区块距离
> _/battleroyale api IGameLootManager getSimulationDistance_

- `返回值`：最远刷新区块距离

#### 游戏物资刷新状态
> _IGameLootStatus_

##### 获取最近完整刷新时间
> _/battleroyale api IGameLootManager getLastBfsTime_

- `返回值`：上一次开始完整刷新的游戏时间

##### 获取最近完整刷新物资数
> _/battleroyale api IGameLootManager getLastBfsProcessedLoot_

- `返回值`：上一次完整刷新的物资数

##### 获取当前待处理区块队列
> _/battleroyale api IGameLootManager queuedChunksRefSize_

- `返回值`：当前待处理区块数量

##### 获取已处理区块缓存数量
> _/battleroyale api IGameLootManager processedChunkCacheSize_

- `返回值`：当前已处理的区块缓存数量

##### 获取玩家中心区块缓存数量
> _/battleroyale api IGameLootManager cachedPlayerCenterChunksSize_

- `返回值`：当前缓存的玩家中心区块数量

##### 获取已计算的中心距离偏移数量
> _/battleroyale api IGameLootManager cachedCenterOffsetSize_

- `返回值`：当前已计算的中心距离偏移数量

#### 游戏物资刷新测试
> _IGameLootTester_

##### 测试是否在待处理区块队列
> _/battleroyale api IGameLootManager isInQueuedChunksRef [xyz]_

- xyz：坐标
- `返回值`：坐标所在区块是否在待处理区块队列

##### 测试是否在已处理区块缓存
> _/battleroyale api IGameLootManager isInProcessedChunkCache [xyz]_

- xyz：坐标
- `返回值`：坐标所在区块是否在已处理区块缓存

##### 测试是否在玩家中心区块缓存
> _/battleroyale api IGameLootManager isInCachedCenterOffset [xyz]_

- xyz：坐标
- `返回值`：坐标所在区块是否在玩家中心区块缓存

#### 游戏物资刷新操作
> _IGameLootOperator_

##### 强制清空待处理区块队列
> _/battleroyale api IGameLootManager forceClearQueuedChunkRef_

- `返回值`：1

##### 强制清空已处理区块缓存
> _/battleroyale api IGameLootManager forceClearProcessedChunkCache_

- `返回值`：1

##### 强制清空玩家中心区块缓存
> _/battleroyale api IGameLootManager forceClearPlayerCenterChunks_

- `返回值`：1

# English

### Game loot generation manager
> _IGameLootManager_

> _/battleroyale api IGameLootManager [...]_

#### Game loot config getter
> _IGameLootConfigGetter_

##### Get max loot chunk per tick
> _/battleroyale api IGameLootManager getMaxLootChunkPerTick_

- `return`: the maximum number of chunks to loot per tick in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get max loot distance
> _/battleroyale api IGameLootManager getMaxLootDistance_

- `return`: the maximum chunk distance for generation in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get tolerant center distance
> _/battleroyale api IGameLootManager getTolerantCenterDistance_

- `return`: the tolerant center distance in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get max cached center
> _/battleroyale api IGameLootManager getMaxCachedCenter_

- `return`: the maximum number of cached center chunks in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get max queued chunk
> _/battleroyale api IGameLootManager getMaxQueuedChunk_

- `return`: the maximum number of chunks in the queue waiting to be processed in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get full generation frequency
> _/battleroyale api IGameLootManager getBfsFrequency_

- `return`: the frequency of a full generation in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Is instant start next generation
> _/battleroyale api IGameLootManager isInstantNextBfs_

- `return`: whether to start the next loot immediately [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get max cached loot chunk
> _/battleroyale api IGameLootManager getMaxCachedLootChunk_

- `return`: the maximum number of processed chunks to cache in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get clean cached chunk
> _/battleroyale api IGameLootManager getCleanCachedChunk_

- `return`: the number of cached chunks to remove during cleanup in [In-game loot Settings](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#In-game-loot-Settings)

##### Get simulation distance
> _/battleroyale api IGameLootManager getSimulationDistance_

- `return`: the max generation chunk distance

#### Game loot status
> _IGameLootStatus_

##### Get last full generation time
> _/battleroyale api IGameLootManager getLastBfsTime_

- `return`: game time when the last full loot started

##### Get last full generation processed loot
> _/battleroyale api IGameLootManager getLastBfsProcessedLoot_

- `return`: number of loot generated in the last full loot

##### Get current chunks waiting to be processed queue
> _/battleroyale api IGameLootManager queuedChunksRefSize_

- `return`: current number of chunks waiting to be processed

##### Get processed chunk cache size
> _/battleroyale api IGameLootManager processedChunkCacheSize_

- `return`: current number of cached processed chunks

##### Get cached player center chunks size
> _/battleroyale api IGameLootManager cachedPlayerCenterChunksSize_

- `return`: current number of cached player center chunks

##### Get cached center offset size
> _/battleroyale api IGameLootManager cachedCenterOffsetSize_

- `return`: current number of calculated center distance offsets

#### Game loot tester
> _IGameLootTester_

##### Is in chunks waiting to be processed queue
> _/battleroyale api IGameLootManager isInQueuedChunksRef [xyz]_

- xyz: coordinates
- `return`: whether the chunk at the coordinates is in the processing queue

##### Is in processed chunk cache
> _/battleroyale api IGameLootManager isInProcessedChunkCache [xyz]_

- xyz: coordinates
- `return`: whether the chunk at the coordinates is in the processed cache

##### Is in cached player center chunks
> _/battleroyale api IGameLootManager isInCachedCenterOffset [xyz]_

- xyz: coordinates
- `return`: whether the chunk at the coordinates is in the player center cache

#### Game loot operator
> _IGameLootOperator_

##### Force clear chunks waiting to be processed queue
> _/battleroyale api IGameLootManager forceClearQueuedChunkRef_

- `return`: 1

##### Force clear processed chunk cache
> _/battleroyale api IGameLootManager forceClearProcessedChunkCache_

- `return`: 1

##### Force clear cached player center chunks
> _/battleroyale api IGameLootManager forceClearPlayerCenterChunks_

- `return`: 1