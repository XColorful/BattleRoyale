[English](#English)

# PUBGMC兼容

模组默认不包含PUBGMC兼容指令，需要在 _./minecraft/battleroyale/temp/registry.json_ 里添加"pubgmcCommand": true
```json
// ./minecraft/battleroyale/temp/registry.json
{
	"pubgmcCommand": true
}
```

需要权限等级2

## 游戏指令
_/game [init/start/map/lobby/select/leave/reloadConfigs/map] [map/xyz/type/create/map] [radius/map/delete] [center] [xz] [side]_

### 初始化游戏
_/game init_

执行[初始化游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#初始化游戏)

### 开始游戏
_/game start_
_/game start [mapName]_

执行[开始游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#开始游戏)

### 大厅信息
_/game lobby_

**此命令不需要权限等级**

执行[大厅信息](https://github.com/XColorful/BattleRoyale/wiki/Game-command#大厅信息)

### 设置大厅
_/game lobby [xyz] [radius]_

手动修改大厅坐标及维度
> 注意：执行[初始化游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#初始化游戏)和[开始游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#开始游戏)前若未成功执行过[读取配置](https://github.com/XColorful/BattleRoyale/wiki/Game-command#读取配置)将自动执行一次，会从配置文件中读取大厅信息并覆盖

### 选择游戏模式
_/game select pubgmc:battle_royale_

输出"已选择游戏类型“battleroyale”"

### 结束游戏
_/game stop_

执行[强制结束游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#强制结束游戏)

### 离开游戏
_/game leave_

**此命令不需要权限等级**

执行[离开队伍](https://github.com/XColorful/BattleRoyale/wiki/Team-command#离开队伍)

### 重载配置
_/game reloadConfigs_

执行[重载全部配置](https://github.com/XColorful/BattleRoyale/wiki/Reload-command#重载全部配置)

### 创建地图
_/game map create [mapName] center [xz] [side]_

执行[区域偏移](https://github.com/XColorful/BattleRoyale/wiki/Game-command#区域偏移)，等价于 _/battleroyale game offset [x] 0 [z]_

### 删除地图
_/game map [mapName] delete_

执行[区域偏移](https://github.com/XColorful/BattleRoyale/wiki/Game-command#区域偏移)，等价于 _/battleroyale game offset 0 0 0_

## 物资刷新指令
_/generator generate_

### 刷新物资
_/generator generate_

执行[刷新物资](https://github.com/XColorful/BattleRoyale/wiki/Loot-command#刷新物资)

# English

To enable PUBGMC commands, add "pubgmcCommand": true to the registry.json file located at ./minecraft/battleroyale/temp/registry.json
```json
// ./minecraft/battleroyale/temp/registry.json
{
	"pubgmcCommand": true
}
```

Require permission level 2

## Game command
_/game [init/start/map/lobby/select/leave/reloadConfigs/map] [map/xyz/type/create/map] [radius/map/delete] [center] [xz] [side]_

### Initialize the game
_/game init_

Execute [Initialize the game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Initialize-the-game)

### Start game
_/game start_
_/game start [mapName]_

Execute [Start game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Start-game)

### Lobby information
_/game lobby_

**This command doesn't require permission level**

Execute [Lobby information](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Lobby-information)

### Set lobby
_/game lobby [xyz] [radius]_

Manually modify lobby coordinates and dimensions
> Note: If [Read configuration](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Read-configuration) has not been successfully executed before executing [Initialize the game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Initialize-the-game) and [Start game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Start-game), it will be automatically executed once, and the lobby informatino will be read from the configuration file and overwritten.

### Select game type
_/game select pubgmc:battle_royale_

Send "Game type 'battleroyale' has been selected successfully"

### End the game
_/game stop_

Execute [Forcibly end the game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Forcibly-end-the-game)

### Leave game
_/game leave_

**This command doesn't require permission level**

Execute [Leave the team](https://github.com/XColorful/BattleRoyale/wiki/Team-command#Leave-the-team)

### Reload configs
_/game reloadConfigs_

Execute [Reload all config](https://github.com/XColorful/BattleRoyale/wiki/Reload-command#Reload-all-config)

### Create map
_/game map create [mapName] center [xz] [side]_

Execute [Zone offset](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Zone-offset), which is equivalent to _/battleroyale game offset [x] 0 [z]_

### Delete map
_/game map [mapName] delete_

Execute [Zone offset](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Zone-offset), which is equivalent to _/battleroyale game offset 0 0 0_

## Loot command
_/generator generate_

### Generate loot
_/generator generate_

Execute [Generate loot](https://github.com/XColorful/BattleRoyale/wiki/Loot-command#Generate-loot)