[English](#English)

# 临时数据
_/battleroyale temp [pubgmc/initStackZoneConfig/gameStep/clear] [boolean]_

需要权限等级2

## 功能类
_/battleroyale temp [...]_

### 启用实体选择器
_/battleroyale temp entitySelector [boolean]_

是否允许在游戏启动时注册实体选择器

#### 启用实体选择器类型
_/battleroyale temp entitySelector [type] [boolean]_

启用指定实体选择器类型：

|type|说明|选择器|
|---|---|---|
|_selector.gameplayers_|全部游戏玩家|_@gameplayers_|
|_selector.nongameplayers.player_|全部非游戏玩家（玩家）|_@nongameplayers.player_|
|_selector.gameplayers.player_|全部游戏玩家（玩家）|_@gameplayers.player_|
|_selector.gameplayers.bot_|全部游戏玩家（人机）|_@gameplayers.bot_|
|_selector.gameplayers.downed_|全部倒地游戏玩家|_@gameplayers.downed_|
|_selector.standinggameplayers_|全部未被淘汰的游戏玩家|_@standinggameplayers_|
|_selector.nonstandinggameplayers.player_|全部非未被淘汰的游戏玩家（玩家）|_@nonstandinggameplayers.player_|
|_selector.standinggameplayers.player_|全部未被淘汰的游戏玩家（玩家）|_@standinggameplayers.player_|
|_selector.standinggameplayers.bot_|全部未被淘汰的游戏玩家（人机）|_@standinggameplayers.bot_|
|_selector.eliminatedgameplayers_|全部被淘汰的游戏玩家|_@eliminatedgameplayers_|
|_selector.eliminatedgameplayers.player_|全部被淘汰的游戏玩家（玩家）|_@eliminatedgameplayers.player_|
|_selector.eliminatedgameplayers.bot_|全部被淘汰的游戏玩家（人机）|_@eliminatedgameplayers.bot_|

### 启用PUBGMC兼容
_/battleroyale temp pubgmc [boolean]_

使[PUBGMC兼容](https://github.com/XColorful/BattleRoyale/wiki/PUBGMC-compatibility)生效

### 启用区域配置叠加
_/battleroyale temp initStackZoneConfig [boolean]_

使[读取配置](https://github.com/XColorful/BattleRoyale/wiki/Game-command#读取配置)不清除已加载的[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config)，若区域ID相同则使用新读取的进行覆盖
- 该设置不会保存至临时数据，关闭游戏后仍需要手动启用
- 大逃杀游戏结束时仍然会进行清理，该功能设计为仅用于临时测试配置
> 该功能等价于将新读取的配置添加至原配置文件（按顺序读取时会覆盖已有同id配置）

### 设置大逃杀游戏步长
_/battleroyale temp gameStep [inverval]_

使大逃杀游戏时长每游戏刻增加 _inverval_
- 该设置不会保存至临时数据，关闭游戏后仍需要手动启用
> 该功能设计为快速预览区域配置是否符合预期，而不会处理步长范围内的每游戏时刻对应的大逃杀游戏tick

### 启用TaCZ子弹处理器
_/battleroyale temp tacz bullerHandler [boolean]_

> 该功能已于 0.5.1 移至[TaCZ护甲缩放](https://github.com/XColorful/TaCZ-Armor-Scaling)

## 清理数据
_/battleroyale temp clear_

需要权限等级3

### 删除所有临时数据
_/battleroyale temp clear_

删除所有[临时数据](https://github.com/XColorful/BattleRoyale/wiki/Temp-data)文件

# English
_/battleroyale temp [pubgmc/clear] [boolean]_

Require permission level 2

## Function category
_/battleroyale temp [...]_

### Enable entity selector
_/battleroyale temp entitySelector [boolean]_

Determines whether to register entity selectors during game startup

#### Enable entity selector type
_/battleroyale temp entitySelector [type] [boolean]_

Enables the specified entity selector type:

|type|Description|Selector|
|---|---|---|
|_selector.gameplayers_|All game players|_@gameplayers_|
|_selector.nongameplayers.player_|All non-game players (Player)|_@nongameplayers.player_|
|_selector.gameplayers.player_|All game players (Player)|_@gameplayers.player_|
|_selector.gameplayers.bot_|All game players (Bot)|_@gameplayers.bot_|
|_selector.gameplayers.downed_|All downed game players|_@gameplayers.downed_|
|_selector.standinggameplayers_|All standing game players|_@standinggameplayers_|
|_selector.nonstandinggameplayers.player_|All non-standing game players (Player)|_@nonstandinggameplayers.player_|
|_selector.standinggameplayers.player_|All standing game players (Player)|_@standinggameplayers.player_|
|_selector.standinggameplayers.bot_|All standing game players (Bot)|_@standinggameplayers.bot_|
|_selector.eliminatedgameplayers_|All eliminated game players|_@eliminatedgameplayers_|
|_selector.eliminatedgameplayers.player_|All eliminated game players (Player)|_@eliminatedgameplayers.player_|
|_selector.eliminatedgameplayers.bot_|All eliminated game players (Bot)|_@eliminatedgameplayers.bot_|

### Enable PUBGMC compatibility
_/battleroyale temp pubgmc [boolean]_

To put [PUBGMC compatibility](https://github.com/XColorful/BattleRoyale/wiki/PUBGMC-compatibility#English) into effect

### Enable zone configuration stacking
_/battleroyale temp initStackZoneConfig [boolean]_

Enables [Read configuration](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Read-configuration) to not clear the loaded [Zone config](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English). If the zone ID is the same, the newly loaded one will be overwritten.
- This setting is not saved to temporary data and must be manually enabled after closing the game.
- Cleanup will still occur after the BattleRoyale game ends. This feature is intended for temporary testing purposes only.
> This function is equivalent to appending the newly loaded configuration to the original configuration (reading sequentially will overwrite existing configurations with the same ID).

### Set the BattleRoyale game step
_/battleroyale temp gameStep [inverval]_

Increases the BattleRoyale game time by _inverval_ per game tick.
- This setting is not saved to temporary data and must be manually enabled after closing the game.
> This feature is designed to quickly preview whether the zone configuration is as expected, and does not process the BattleRoyale game ticks corresponding to each game time within the step range.

### Enable TaCZ bullet handler
_/battleroyale temp tacz bullerHandler [boolean]_

> This feature was moved to [TaCZ Armor Scaling](https://github.com/XColorful/TaCZ-Armor-Scaling) in version 0.5.1

## Clear data
_/battleroyale temp clear_

Requires permission level 3

### Delete all temporary data
_/battleroyale temp clear_

Delete all [Temporary data](https://github.com/XColorful/BattleRoyale/wiki/Temp-data#English) files