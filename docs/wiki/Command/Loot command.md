[English](#English)

# 手动刷新
_/battleroyale loot [generate/stop/chunk/pos/player] [xyz/reset/generate/...]_

需要权限等级2
- **游戏进行时无法执行**

## 执行刷新
_/battleroyale loot [generate/stop/chunk/pos] [xyz]_

> 自0.4.7起，执行刷新不再更新游戏ID或写入[游戏管理器](https://github.com/XColorful/BattleRoyale/wiki/Temp-data#游戏管理器)临时数据

### 刷新物资
_/battleroyale loot generate_

- 手动添加当前已加载区块到待处理队列
- 每tick处理若干个区块刷新（默认为5）
- 处理过程中开始大逃杀游戏将被打断
- 可手动[停止刷新](#停止刷新)

### 区块刷新
_/battleroyale loot chunk [xyz]_

对坐标所在的区块执行[刷新物资](#刷新物资)
- **立即执行**

### 方块刷新
_/battleroyale loot pos [xyz]_

对坐标所在的方块执行[刷新物资](#刷新物资)
- **立即执行**
- 如无法执行刷新则提示不可用

### 停止刷新
_/battleroyale loot stop_

立即结束当前执行的[刷新物资](#刷新物资)并结算

## 背包刷新
_/battleroyale loot [player] [id] [reset/generate] [...]_

> 效果同[背包区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#背包区)

### 重置背包
_/battleroyale loot [player] [id] reset_

- id：[物资刷新器配置](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#物资刷新配置)唯一id
- 清空玩家背包
- 对每位玩家分别用物资刷新配置生成物品列表
- 每次刷新的游戏ID相同
- 填充至玩家背包

### 背包物品刷新
_/battleroyale loot [player] [id] generate [skipNonEmpty] [dropBeforeReplace] [firstSlotIndex] [lastSlotIndex]_

- id：[物资刷新器配置](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#物资刷新配置)唯一id
- 参数同[背包区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#背包区)
- 对每位玩家分别用物资刷新配置生成物品列表
- 每次刷新的游戏ID相同

# English
_/battleroyale loot [generate/stop/chunk/pos/player] [xyz/reset/generate/...]_

Require permission level 2
- **Cannot be executed while the game is in progress**

## Execute loot
_/battleroyale loot [generate/stop/chunk/pos] [xyz]_

> Since version 0.4.7, executing a manual refresh no longer updates the Game ID or writes to the [Game Manager](https://github.com/XColorful/BattleRoyale/wiki/Temp-data#Game-Manager) temporary data.

### Generate loot
_/battleroyale loot generate_

- Manually add currently loaded chunks to the queue to be processed
- Process a number of chunk loot per tick (default is 5)
- Starting the BattleRoyale game during processing will be interrupted
- Can be manually terminated via [Stop Generation](#Stop-generation).

### Chunk loot
_/battleroyale loot chunk [xyz]_

Executes [Generate Loot](#Generate-loot) for the chunk at the specified coordinates.
- **Immediate execution**

### Block loot
_/battleroyale loot pos [xyz]_

Executes [Generate loot](#Generate-loot) for the specific block at the coordinates.
- **Immediate execution.**
- Displays an "Unavailable" message if loot generation cannot be performed

### Stop generation
_/battleroyale loot stop_

Immediately terminates the current [Generate loot](#Generate-loot) task and performs final calculations.

## Inventory loot
_/battleroyale loot [player] [id] [reset/generate] [...]_

> Same effect as [Inventory Zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Inventory-Zone)

### Reset inventory
_/battleroyale loot [player] [id] reset_

- id: Unique id of [Loot spawner config](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#Loot)
- Clears the player's inventory
- Generates item lists separately for each player using the loot spawner config
- The game id remains the same for each generation
- Populates the player's inventory

### Inventory item generation
_/battleroyale loot [player] [id] generate [skipNonEmpty] [dropBeforeReplace] [firstSlotIndex] [lastSlotIndex]_

- id: Unique id of [Loot spawner config](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#Loot)
- Parameters are consistent with [Inventory zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Inventory-zone)
- Generates item lists separately for each player using the loot spawner config
- The game id remains the same for each generation