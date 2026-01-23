[English](#English)

# 大逃杀指令
_/battleroyale game [load/init/start/stop/lobby/toLobby/offset/selected/spectate] [xyz]_

需要权限等级2

## 开始大逃杀游戏
_/battleroyale game [load/init/start]_

### 读取配置
_/battleroyale game load_

- 保存当前世界维度，作为大逃杀世界维度
- 根据大逃杀选用的配置id，读取配置文件
- 备份当前世界规则
- 允许大厅无敌生效
- 保留或重置队伍信息
- 将游戏管理器设置为已准备

### 初始化游戏
_/battleroyale game init_

若游戏管理器未准备，则执行一次 _/battleroyale game load_ ，仍未准备则取消指令
- 开始同步队伍信息
- 若允许自动加入队伍，则强制给所有玩家分配队伍并加入游戏
- 若允许自动加入队伍，新登录的玩家可自动加入游戏
- 应用配置的世界规则
- 将所有游戏玩家传送至大厅，可在[游戏配置](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#游戏配置)中禁用 _teleportWhenInitGame_
- 预生成出生信息（传送点位、航线）
- 根据当前是否已准备，将游戏管理器设置为已就绪

### 开始游戏
_/battleroyale game start_

若游戏管理器未就绪，则执行一次 _/battleroyale game init_ ，仍未就绪则取消指令
- 清除无队伍的游戏玩家
- 若无足够队伍开始游戏则取消执行
- 备份所有游戏玩家当前游戏模式
- 为游戏玩家应用配置的游戏模式
- 若无就绪的区域则取消执行
- 若无出生信息则取消执行
- 开始记录大逃杀游戏统计数据

## 结束游戏
_/battleroyale game stop_

### 强制结束游戏
_/battleroyale game stop_

- 将所有游戏玩家传送回大厅
- 取消新登录的玩家自动加入游戏
- 清除或保留队伍信息

## 大厅
_/battleroyale game [lobby/toLobby]_

**此命令不需要权限等级**

### 大厅信息
_/battleroyale game lobby_

- 查看大厅坐标
- 查看大厅规模
- 查看大厅是否启用无敌
- 查看大厅是否启用治疗

### 传送至大厅
_/battleroyale game toLobby_

- 游戏中执行该命令将被强制淘汰
- 传送前清零下落距离
> 安全传送，文明掉落
> 
> 传送不规范，玩家两行泪

## 全局偏移
_/battleroyale game offset [xyz]_

### 查看偏移
_/battleroyale game offset_

- 查询全局偏移精确值

### 区域偏移
_/battleroyale game offset [xyz]_

- 使区域的固定起止中心往三个方向分别偏移x、y、z
- 使固定出生方式的点位往三个方向分别偏移x、y、z
> 出生方式的偏移会被出生配置生效的preZoneCenterOffset覆盖

## 查询选定游戏配置
_/battleroyale game selected_

### 查询全部选定游戏配置
_/battleroyale game selected_

- 查询选用的[人机配置](https://github.com/XColorful/BattleRoyale/wiki/Bot-config)id及名称
- 查询选用的[游戏规则配置](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config)id及名称
- 查询选用的[出生配置](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config)id及名称
- 查询选用的[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config)文件名称及区域数量

## 观战游戏
_/battleroyale game spectate_

**此命令不需要权限等级**

### 切换旁观模式
_/battleroyale game spectate_

- 当游戏进行时，队伍被淘汰时可切换为旁观模式
- 当没有进行中的游戏时，切换为大逃杀默认游戏模式
> 可在[游戏配置](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#游戏配置)中更改观战规则

## 游戏信息
_/battleroyale game gameId_

### 查看游戏ID
_/battleroyale game gameId_

**此命令不需要权限等级**

- 查询当前游戏管理器的游戏ID

# English
_/battleroyale game [load/init/start/stop/lobby/toLobby/offset/selected/spectate] [xyz]_

Require permission level 2

## Start BattleRoyale game
_/battleroyale game [load/init/start]_

### Read configuration
_/battleroyale game load_

- Save the current world level as the BattleRoyale world level
- Read the configuration file according to the config id selected for the BattleRoyale
- Back up the current world gamerules
- Allow lobby muteki to take effect
- Keep or reset team information
- Set the game manager to be prepared

### Initialize the game
_/battleroyale game init_

If the game manager is not prepared, execute _/battleroyale game load_ once, and cancel the command if it is still not prepared
- Start synchronizing team information
- If allow auto join team, force all players to be assigned to a team and join the game
- If allow auto join team, newly logged in players can automatically join the game
- Apply the configured world gamerules
- Teleport all game players to the lobby, this can be disabled in [Game config](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Game-config) with _teleportWhenInitGame_
- Pre-generate spawn information (teleport point, plane route, etc.)
- Set the game manager to ready based on whether it is currently ready

### Start game
_/battleroyale game start_

If the game manager is not ready, execute _/battleroyale game init_ once, and cancel the command if it is still not ready
- Clear game players without a team
- Cancel execution if there are not enough teams to start the game
- Back up the current game mode for all game players
- Apply the configured game mode for the game players
- Cancel execution if there is no pending zone
- Cancel execution if there is no spawn information
- Start recording battle royale game statistics

## End the game
_/battleroyale game stop_

### Forcibly end the game
_/battleroyale game stop_

- Teleport all game players back to the lobby
- Cancel the automatic joining of newly logged in players
- Clear or retain team information

## Lobby
_/battleroyale game [lobby/toLobby]_

**This command doesn't require permission level**

### Lobby information
_/battleroyale game lobby_

- Check lobby coordinates
- Check lobby size
- Check whether invulnerable is enabled in the lobby
- Check whether heal is enabled in the lobby

### Teleport to lobby
_/battleroyale game toLobby_

- Executing this command in the game will result in forced elimination
- Reset fall distance before teleporting
> Safe teleportation, civilized drop
> 
> Irregular teleportation, players shed tears

## Global offset
_/battleroyale game offset [xyz]_

### Check offset
_/battleroyale game offset_

- Query the exact value of the global offset

### Zone offset
_/battleroyale game offset [xyz]_

- Shifts the fixed start and end center points of the zone along the x, y, and z axes.
- Shifts the fixed spawn point along the x, y, and z axes.
> The spawn offset will be overwritten by the effective preZoneCenterOffset in the spawn configuration

## Query selected game config
_/battleroyale game selected_

### Query all selected game config
_/battleroyale game selected_

- Query the selected [bot](https://github.com/XColorful/BattleRoyale/wiki/Bot-config#English) id and name
- Query the selected [gamerule](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#English) id and name
- Query the selected [spawn](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config#English) id and name
- Query the selected [zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English) file name and zone quantity

## Spectate game
_/battleroyale game spectate_

**This command doesn't require permission level**

### Switch spectator mode
_/battleroyale game spectate_

- When game is in progress, switch to spectator mode if game team is eliminated
- When there's no game in progress, switch to default BattleRoyale gamemode
> Spectator rules can be changed in the [Game config](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Game-config)

## Game info
_/battleroyale game gameId_

### Check game ID
_/battleroyale game gameId_

**This command doesn't require permission level**

- Query Game Manager's current game ID