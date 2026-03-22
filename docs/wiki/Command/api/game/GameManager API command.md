[English](#English)

### 游戏管理器
> _IGameManager_

> _/battleroyale api gameManager [...]_

#### 游戏信息获取
> _IGameInfoGetter_

##### 游戏时间
> _/battleroyale api gameManager getGameTime_

- `返回值`：当前游戏时间，单位 tick

##### 是否在游戏中
> _/battleroyale api gameManager isInGame_

- `返回值`：当前是否在游戏中

##### 全局偏移
> _/battleroyale api gameManager getGlobalCenterOffset [resourceLocation]_

将[全局偏移](https://github.com/XColorful/BattleRoyale/wiki/Game-command#全局偏移)存入指定`命令存储`
- resourceLocation：标识符
- `返回值`：1

##### 最大游戏时长
> _/battleroyale api gameManager getMaxGameTime_

- `返回值`：[最大游戏时长](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获胜队伍总数
> _/battleroyale api gameManager getWinnerTeamTotal_

- `返回值`：[获胜队伍总数](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 最少队伍数量
> _/battleroyale api gameManager _

- `返回值`：开始大逃杀游戏[需要的队伍数量](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 是否有队伍获胜
> _/battleroyale api gameManager hasWinner_

- `返回值`：当前状态是否有队伍获胜

##### 剩余自动重开游戏次数
> _/battleroyale api gameManager getRemainRestartTime_

- `返回值`：当前剩余的[自动重开游戏次数](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#游戏配置)

#### 游戏功能
> _IGameFunc_

##### 发送观战消息
> _/battleroyale api gameManager sendGameSpectateMessage [player]_

- player：玩家选择器
向玩家[发送观战消息](https://github.com/XColorful/BattleRoyale/wiki/GameProcessManager-API-command#发送观战消息)，自动填入[onlyGamePlayerSpectate](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#游戏配置)
- `返回值`：1

##### 结算游戏
> _/battleroyale api gameManager finishGame [hasWinner]_

强制结算并停止游戏：
- hasWinner：是否按获胜条件添加获胜玩家及获胜队伍
- `返回值`：1

##### 增加游戏时间并处理
> _/battleroyale api gameManager addGameTimeAndTick_

强制增加 1 tick 游戏时间并处理
- `返回值`：1

#### 游戏状态设置
> _IGameStatusSetter_

##### 添加死亡事件后结算检查
> _/battleroyale api gameManager addFinishCheckAfterDeathEvent_

若当前正在游戏玩家死亡事件（_IGameMangaer::onPlayerDeath_）中，则在事件结束后添加游戏胜利条件检查：
- `返回值`：是否成功添加检查

##### 设置是否有获胜队伍
> _/battleroyale api gameManager setHasWinner [hasWinner]_

- `返回值`：是否成功设置

##### 清空获胜游戏玩家
> _/battleroyale api gameManager clearWinnerGamePlayers_

- `返回值`：是否成功清空

##### 清空获胜游戏队伍
> _/battleroyale api gameManager clearWinnerGameTeams_

- `返回值`：是否成功清空

##### 添加获胜游戏玩家
> _/battleroyale api gameManager addWinnerGamePlayer byPlayer [player] [withMembers] [withTeam]_
> 
> _/battleroyale api gameManager addWinnerGamePlayer byId [id] [withMembers] [withTeam]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID选中游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- withMembers：是否将游戏玩家队伍成员一并加入获胜游戏玩家
- withTeam：是否将游戏玩家所在游戏队伍加入获胜游戏队伍
- `返回值`：成功添加的游戏玩家数量

##### 添加获胜游戏队伍
> _/battleroyale api gameManager addWinnerGameTeam byPlayer [player] [withMembers]_
> 
> _/battleroyale api gameManager addWinnerGameTeam byId [id] [withMembers]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏队伍ID选中游戏队伍
- 若不存在对应的游戏玩家或游戏队伍，`返回值`为0
- withMembers：是否将游戏队伍成员一并加入获胜游戏玩家
- `返回值`：成功添加的游戏队伍数量和游戏玩家数量

##### 设置剩余自动重开游戏次数
> _/battleroyale api gameManager setRemainRestartTime [time]_

- time：设置当前[自动重开游戏次数](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#游戏配置)
- `返回值`：是否成功设置

#### 游戏配置获取
> _IGameConfigGetter_

##### 获取选中的游戏规则配置ID
> _/battleroyale api gameManager getGameruleConfigId_

- `返回值`：当前选中的[游戏规则配置](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#单个配置)id

##### 获取选中的出生配置ID
> _/battleroyale api gameManager getSpawnConfigId_

- `返回值`：当前选中的[出生配置](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config#单个配置)id

##### 获取选中的统计数据配置ID
> _/battleroyale api gameManager getStatsConfigId_

- `返回值`：当前选中的[统计数据配置](https://github.com/XColorful/BattleRoyale/wiki/Stats-config#单个配置)id

#####  获取选中的人机配置ID
> _/battleroyale api gameManager getBotConfigId_

- `返回值`：当前选中的[人机配置](https://github.com/XColorful/BattleRoyale/wiki/Bot-config#单个配置)id

# English

### Game manager
> _IGameManager_

> _/battleroyale api gameManager [...]_

#### Game info getter
> _IGameInfoGetter_

##### Game time
> _/battleroyale api gameManager getGameTime_

- `return value`: current game time in ticks

##### Is in game
> _/battleroyale api gameManager isInGame_

- `return value`: whether the game is currently in progress

##### Global offset
> _/battleroyale api gameManager getGlobalCenterOffset [resourceLocation]_

Saves the [Global offset](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Global-offset) to the specified `command storage`:
- resourceLocation: identifier
- `return value`: 1

##### Maximum game time
> _/battleroyale api gameManager getMaxGameTime_

- `return value`: [maximum BattleRoyale game time](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule)

##### Winner team total
> _/battleroyale api gameManager getWinnerTeamTotal_

- `return value`: [total number of winner teams](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule)

##### Required team to start
> _/battleroyale api gameManager _

- `return value`: the number of [teams required](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) to start BattleRoyale

##### Has winner
> _/battleroyale api gameManager hasWinner_

- `return value`: whether there is currently any winning team

##### Remaining automatic restart rounds
> _/battleroyale api gameManager getRemainRestartTime_

- `return value`: current remaining [automatic game restarts](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Game-config)

#### Game functions
> _IGameFunc_

##### Send spectate message
> _/battleroyale api gameManager sendGameSpectateMessage [player]_

- player: player selector
[Sends spectate message](https://github.com/XColorful/BattleRoyale/wiki/GameProcessManager-API-command#Send-spectate-message) to the player, automatically filling in [onlyGamePlayerSpectate](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Game-config)
- `return value`: 1

##### Finish game
> _/battleroyale api gameManager finishGame [hasWinner]_

Forcefully finishes and stops the game:
- hasWinner: whether to add winning players and teams based on winning conditions
- `return value`: 1

##### Add game time and tick
> _/battleroyale api gameManager addGameTimeAndTick_

Forcefully increases game time by 1 tick and processes it:
- `return value`: 1

#### Game status setter
> _IGameStatusSetter_

##### Add finish check after death event
> _/battleroyale api gameManager addFinishCheckAfterDeathEvent_

If currently within a game player death event (_IGameMangaer::onPlayerDeath_), adds a victory condition check after the event ends:
- `return value`: whether the check was successfully added

##### Set hasWinner
> _/battleroyale api gameManager setHasWinner [hasWinner]_

- `return value`: whether the status was successfully set

##### Clear winner game players
> _/battleroyale api gameManager clearWinnerGamePlayers_

- `return value`: whether the set was successfully cleared

##### Clear winner game teams
> _/battleroyale api gameManager clearWinnerGameTeams_

- `return value`: whether the set was successfully cleared

##### Add winner game player
> _/battleroyale api gameManager addWinnerGamePlayer byPlayer [player] [withMembers] [withTeam]_
> 
> _/battleroyale api gameManager addWinnerGamePlayer byId [id] [withMembers] [withTeam]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the player does not exist, the `return value` is 0.
- withMembers: whether to add game player's all team members to the winner game players
- withTeam: whether to add the player's game team to the winner game teams
- `return value`: number of successfully added game players

##### Add winner game team
> _/battleroyale api gameManager addWinnerGameTeam byPlayer [player] [withMembers]_
> 
> _/battleroyale api gameManager addWinnerGameTeam byId [id] [withMembers]_

- player: selects game player using an entity selector
- id: selects game team using a game team ID
- If the player or team does not exist, the `return value` is 0.
- withMembers: whether to add all game team members to the winner game players
- `return value`: number of successfully added game teams and players

##### Set remain restart time
> _/battleroyale api gameManager setRemainRestartTime [time]_

- time: sets the current number of [automatic game restarts](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Game-config)
- `return value`: whether the value was successfully set

#### Game config getter
> _IGameConfigGetter_

##### Get Gamerule config ID
> _/battleroyale api gameManager getGameruleConfigId_

- `return value`: currently selected [Gamerule config](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Single-gamerule-config) id

##### Get Spawn config ID
> _/battleroyale api gameManager getSpawnConfigId_

- `return value`: currently selected [Spawn config](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config#Single-spawn-config) id

##### Get Stats config ID
> _/battleroyale api gameManager getStatsConfigId_

- `return value`: currently selected [Stats config](https://github.com/XColorful/BattleRoyale/wiki/Stats-config#Single-stats-config) id

##### Get Bot config ID
> _/battleroyale api gameManager getBotConfigId_

- `return value`: currently selected [Bot config](https://github.com/XColorful/BattleRoyale/wiki/Bot-config#Single-bot-config) id