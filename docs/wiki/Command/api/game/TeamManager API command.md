[English](#English)

### 队伍管理器
> _ITeamManager_

> _/battleroyale api teamManager [...]_

##### 是否自动加入
> _/battleroyale api teamManager shouldAutoJoin_

- `返回值`：是否[自动加入游戏](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取未满员队伍ID
> _/battleroyale api teamManager findNotFullTeamId_

- `返回值`：第一个未满员队伍ID，如无则返回 -1

##### 是否有足够玩家队伍开始游戏
> _/battleroyale api teamManager hasEnoughPlayerTeamToStart_

- `返回值`：当前游戏玩家和队伍是否足够开始游戏

#### 游戏队伍读取
> _IGameTeamReadApi_

##### 获取玩家上限
> _/battleroyale api teamManager getPlayerLimit_

- `返回值`：[游戏玩家总数上限](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取游戏玩家ID
> _/battleroyale api teamManager getGamePlayerId [player]_

- player：用实体选择器选中并获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：游戏玩家ID

##### 获取游戏玩家
> _/battleroyale api teamManager getGamePlayer [resourceLocation] [storagePath] [detailLevel] byPlayer [player]_
> 
> _/battleroyale api teamManager getGamePlayer [resourceLocation] [storagePath] [detailLevel] byId [id]_

将游戏玩家信息存入指定`命令存储`：
- resourceLocation：标识符
- storagePath：NBT路径
- detailLevel：详细程度，范围[0, 3]
- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID选中游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：1

##### 是否为未被淘汰的游戏玩家
> _/battleroyale api teamManager hasStandingGamePlayer [player]_

- player：用实体选择器选中并获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：游戏玩家在当前游戏是否未被淘汰

##### 是否只剩人机队伍
> _/battleroyale api teamManager onlyRemainBotTeam_

- `返回值`：当前游戏是否只剩人机玩家

##### 获取游戏队伍ID
> _/battleroyale api teamManager getGameTeamId byPlayer [player]_
> 
> _/battleroyale api teamManager getGameTeamId byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID选中游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：游戏玩家所在的游戏队伍ID

##### 获取游戏队伍
> _/battleroyale api teamManager getGameTeam [resourceLocation] [storagePath] [detailLevel] byPlayer [player]_
> 
> _/battleroyale api teamManager getGameTeam [resourceLocation] [storagePath] [detailLevel] byId [id]_

将游戏队伍信息存入指定`命令存储`：
- resourceLocation：标识符
- storagePath：NBT路径
- detailLevel：详细程度，范围[0, 3]
- player：用实体选择器选中并获取游戏玩家
- id：用游戏队伍ID选中游戏队伍
- 若不存在对应的游戏玩家或游戏队伍，`返回值`为0
- `返回值`：1

##### 获取游戏玩家总数
> _/battleroyale api teamManager getGamePlayersTotal_

- `返回值`：当前游戏玩家总数

##### 获取全部游戏玩家
> _/battleroyale api teamManager getGamePlayers [resourceLocation] [storagePath] [detailLevel]_

将所有游戏玩家信息存入指定`命令存储`：
- resourceLocation：标识符
- storagePath：NBT路径
- detailLevel：详细程度，范围[0, 3]
- `返回值`：1

##### 获取游戏队伍总数
> _/battleroyale api teamManager getGameTeamsTotal_

- `返回值`：当前游戏队伍总数

##### 获取全部游戏队伍
> _/battleroyale api teamManager getGameTeams [resourceLocation] [storagePath] [detailLevel]_

将所有游戏队伍信息存入指定`命令存储`：
- resourceLocation：标识符
- storagePath：NBT路径
- detailLevel：详细程度，范围[0, 3]
- `返回值`：1

##### 获取全部未被淘汰的游戏玩家数量
> _/battleroyale api teamManager getStandingGamePlayersTotal_

- `返回值`：当前未被淘汰的游戏玩家数量

##### 获取全部未被淘汰的游戏玩家
> _/battleroyale api teamManager getStandingGamePlayers [resourceLocation] [storagePath] [detailLevel]_

将所有未被淘汰的游戏玩家信息存入指定`命令存储`：
- resourceLocation：标识符
- storagePath：NBT路径
- detailLevel：详细程度，范围[0, 3]
- `返回值`：1

##### 获取全部未被淘汰的游戏队伍数量
> _/battleroyale api teamManager getStandingGameTeamsTotal_

- `返回值`：当前未被淘汰的游戏队伍数量

##### 获取全部未被淘汰的游戏队伍
> _/battleroyale api teamManager getStandingGameTeams [resourceLocation] [storagePath] [detailLevel]_

将所有未被淘汰的游戏队伍信息存入指定`命令存储`：
- resourceLocation：标识符
- storagePath：NBT路径
- detailLevel：详细程度，范围[0, 3]
- `返回值`：1

##### 获取随机未被淘汰的游戏玩家ID
> _/battleroyale api teamManager getRandomStandingGamePlayerId_

- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：任一游戏玩家ID

##### 获取非人机队伍总数
> _/battleroyale api teamManager getNonBotTeamCount_

- `返回值`：非人机队伍总数

##### 获取未被淘汰的非人机游戏队伍数量
> _/battleroyale api teamManager getStandingPlayerTeamCount_

- `返回值`：未被淘汰的非人机队伍数量

#### 队伍管理
> _ITeamManagement_

##### 静默强制淘汰游戏玩家
> _/battleroyale api teamManager forceEliminatePlayerSilence byPlayer [player]_
> 
> _/battleroyale api teamManager forceEliminatePlayerSilence byId [id]_

在游戏中强制淘汰玩家，不包含发送系统消息；成功淘汰后发送大厅传送消息：
- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID选中游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：是否成功淘汰

##### 强制从游戏队伍淘汰游戏玩家
> _/battleroyale api teamManager forceEliminatePlayerFromTeam [player]_

在游戏中强制淘汰玩家并向队友发送消息：
- player：用实体选择器选中并获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：是否成功淘汰

#### 队伍预管理
> _ITeamPreManagement_

##### 强制加入队伍
> _/battleroyale api teamManager forceJoinTeam [player]_

游戏未开始时强制玩家加入队伍，优先加入已有队伍，其次创建新队伍：
- player：玩家选择器
- `返回值`：玩家是否为游戏玩家

##### 强制将玩家移出队伍
> _/battleroyale api teamManager removePlayerFromTeam byPlayer [player]_
> 
> _/battleroyale api teamManager removePlayerFromTeam byId [id]_

游戏未开始时将玩家移出队伍：
- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID选中游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- `返回值`：是否移除成功

#### 队伍通知
> _ITeamNotification_

##### 发送玩家队伍ID
> _/battleroyale api teamManager sendPlayerTeamId [player]_

- player：用实体选择器选中并获取游戏玩家
- 若不存在对应的游戏玩家，则不发送消息
- `返回值`：是否存在对应的游戏玩家

#### 原版队伍
> _IVanillaTeam_

##### 退出原版队伍
> _/battleroyale api teamManager clearVanillaTeam_

在游戏维度为全体游戏玩家退出原版队伍
- `返回值`：1

# English

### Team manager
> _ITeamManager_

> _/battleroyale api teamManager [...]_

##### Should auto join
> _/battleroyale api teamManager shouldAutoJoin_

- `return value`: whether to [automatically join the game](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) 

##### Find not full team ID
> _/battleroyale api teamManager findNotFullTeamId_

- `return value`: the first not full team ID, returns -1 if none exists

##### Has enough player team to start
> _/battleroyale api teamManager hasEnoughPlayerTeamToStart_

- `return value`: whether the current game players and teams are sufficient to start the game

#### Game team read api
> _IGameTeamReadApi_

##### Get player limit
> _/battleroyale api teamManager getPlayerLimit_
- `return value`: [maximum total number of game players](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### Get game player ID
> _/battleroyale api teamManager getGamePlayerId [player]_

- player: selects game player using an entity selector
- If the game player does not exist, the `return value` is 0.
- `return value`: the game player ID

##### Get game player
> _/battleroyale api teamManager getGamePlayer [resourceLocation] [storagePath] [detailLevel] byPlayer [player]_
> 
> _/battleroyale api teamManager getGamePlayer [resourceLocation] [storagePath] [detailLevel] byId [id]_

Saves the game player's information to the specified `command storage`:
- resourceLocation: identifier
- storagePath: NBT path
- detailLevel: detailed level, range [0, 3]
- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is 0.
- `return value`: 1

##### Has standing game player
> _/battleroyale api teamManager hasStandingGamePlayer [player]_

- player: selects game player using an entity selector
- If the game player does not exist, the `return value` is 0.
- `return value`: whether the game player is not eliminated in the current game

##### Only remain bot team
> _/battleroyale api teamManager onlyRemainBotTeam_

- `return value`: whether only bot players remain in the current game

##### Get game team ID
> _/battleroyale api teamManager getGameTeamId byPlayer [player]_
> 
> _/battleroyale api teamManager getGameTeamId byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is 0.
- `return value`: the game team ID the game player belongs to

##### Get game team
> _/battleroyale api teamManager getGameTeam [resourceLocation] [storagePath] [detailLevel] byPlayer [player]_
> 
> _/battleroyale api teamManager getGameTeam [resourceLocation] [storagePath] [detailLevel] byId [id]_

Saves the game team's information to the specified `command storage`:
- resourceLocation: identifier
- storagePath: NBT path
- detailLevel: detailed level, range [0, 3]
- player: selects game player using an entity selector
- id: selects game team using a game team ID
- If the game player or team does not exist, the `return value` is 0.
- `return value`: 1

##### Get game players total
> _/battleroyale api teamManager getGamePlayersTotal_

- `return value`: the current total number of game players

##### Get all game players
> _/battleroyale api teamManager getGamePlayers [resourceLocation] [storagePath] [detailLevel]_

Saves all the game players' information to the specified `command storage`:
- resourceLocation: identifier
- storagePath: NBT path
- detailLevel: detailed level, range [0, 3]
- `return value`: 1

##### Get game teams total
> _/battleroyale api teamManager getGameTeamsTotal_

- `return value`: the current total number of game teams

##### Get all game teams
> _/battleroyale api teamManager getGameTeams [resourceLocation] [storagePath] [detailLevel]_

Saves all the game teams' information to the specified `command storage`:
- resourceLocation: identifier
- storagePath: NBT path
- detailLevel: detailed level, range [0, 3]
- `return value`: 1

##### Get standing game players total
> _/battleroyale api teamManager getStandingGamePlayersTotal_

- `return value`: the current total number of non-eliminated game players

##### Get all standing game players
> _/battleroyale api teamManager getStandingGamePlayers [resourceLocation] [storagePath] [detailLevel]_

Saves all the non-eliminated game players' information to the specified `command storage`:
- resourceLocation: identifier
- storagePath: NBT path
- detailLevel: detailed level, range [0, 3]
- `return value`: 1

##### Get standing game teams total
> _/battleroyale api teamManager getStandingGameTeamsTotal_

- `return value`: the current total number of non-eliminated game teams

##### Get all standing game teams
> _/battleroyale api teamManager getStandingGameTeams [resourceLocation] [storagePath] [detailLevel]_

Saves all the non-eliminated game teams' information to the specified `command storage`:
- resourceLocation: identifier
- storagePath: NBT path
- detailLevel: detailed level, range [0, 3]
- `return value`: 1

##### Get random standing game player ID
> _/battleroyale api teamManager getRandomStandingGamePlayerId_

- If the game player does not exist, the `return value` is 0.
- `return value`: any non-eliminated game player ID

##### Get non-bot team count
> _/battleroyale api teamManager getNonBotTeamCount_

- `return value`: the total number of non-bot teams

##### Get standing player team count
> _/battleroyale api teamManager getStandingPlayerTeamCount_

- `return value`: the total number of non-eliminated non-bot teams

#### Team management
> _ITeamManagement_

##### Force eliminate player silence
> _/battleroyale api teamManager forceEliminatePlayerSilence byPlayer [player]_
> 
> _/battleroyale api teamManager forceEliminatePlayerSilence byId [id]_

Forcefully eliminates the player during the game without sending messages; sends a lobby teleport message upon successful elimination:
- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is 0.
- `return value`: whether the elimination was successful

##### Force eliminate player from team
> _/battleroyale api teamManager forceEliminatePlayerFromTeam [player]_

Forcefully eliminates the player during the game and sends a message to teammates:
- player: selects game player using an entity selector
- If the game player does not exist, the `return value` is 0.
- `return value`: whether the elimination was successful

#### Team pre management
> _ITeamPreManagement_

##### Force join team
> _/battleroyale api teamManager forceJoinTeam [player]_

Forcefully joins the player to a team before the game starts, prioritizing existing teams or creating a new one:
- player: player selector
- `return value`: whether the player is a game player

##### Remove player from team
> _/battleroyale api teamManager removePlayerFromTeam byPlayer [player]_
> 
> _/battleroyale api teamManager removePlayerFromTeam byId [id]_

Removes the player from their team before the game starts:
- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is 0.
- `return value`: whether the removal was successful

#### Team notification
> _ITeamNotification_

##### Send player team ID
> _/battleroyale api teamManager sendPlayerTeamId [player]_

- player: selects game player using an entity selector
- If the game player does not exist, the `return value` is 0.
- `return value`: whether the game player exists

#### Vanilla team
> _IVanillaTeam_

##### Clear vanilla team
> _/battleroyale api teamManager clearVanillaTeam_

Makes all game players leave their vanilla teams within the game dimension:
- `return value`: 1