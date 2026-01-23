# 手动刷新
_/battleroyale loot generate_

需要权限等级2
- **游戏进行时无法执行**

## 执行刷新
_/battleroyale loot generate_

- 更新游戏ID并写入[游戏管理器](https://github.com/XColorful/BattleRoyale/wiki/Temp-data#游戏管理器)临时数据
- 手动添加当前已加载区块到待处理队列
- 每tick处理若干个区块刷新（默认为5）
- 处理过程中开始大逃杀游戏将被打断

# English
_/battleroyale loot generate_

Require permission level 2
- **Cannot be executed while the game is in progress**

## Execute loot
_/battleroyale loot generate_

- Update game ID and write temporary data to [Game Manager](https://github.com/XColorful/BattleRoyale/wiki/Temp-data#Game-Manager)
- Manually add currently loaded chunks to the queue to be processed
- Process a number of chunk loot per tick (default is 5)
- Starting the BattleRoyale game during processing will be interrupted