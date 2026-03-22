[English](#English)

### 统计管理器
> _IStatsManager_

> _/battleroyale api statsManager [...]_

##### 获取是否记录统计数据
> _/battleroyale api statsManager shouldRecordStats_

- `返回值`：是否[记录统计数据](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取是否为记录的游戏玩家
> _/battleroyale api statsManager isInRecordGamePlayers byPlayer [player]_
> 
> _/battleroyale api statsManager isInRecordGamePlayers byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：是否在记录的游戏玩家中

##### 保存统计数据
> _/battleroyale api statsManager saveStats [filePath]_

立即保存统计数据
- filePath：保存的文件路径
- `返回值`：1

# English

### Statistics manager
> _IStatsManager_

> _/battleroyale api statsManager [...]_

##### Should record stats
> _/battleroyale api statsManager shouldRecordStats_

- `return value`: whether to [record the statistics](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) of the game

##### Is in record game players
> _/battleroyale api statsManager isInRecordGamePlayers byPlayer [player]_
> 
> _/battleroyale api statsManager isInRecordGamePlayers byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- if the game player does not exist, the `return value` is 0
- `return value`: whether the player is in the record game players list

##### Save stats
> _/battleroyale api statsManager saveStats [filePath]_

Immediately saves the statistics:
- filePath: the file path to save to
- `return value`: 1