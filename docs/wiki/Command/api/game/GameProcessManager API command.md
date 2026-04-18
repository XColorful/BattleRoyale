[English](#English)

### 游戏进程管理器
> _IGameProcessManager_

> _/battleroyale api gameProcessManager [...]_

##### 检查并更新游戏停止状态
> _/battleroyale api gameProcessManager checkIfGameShouldEndAndFinish_

- 若不在游戏中，`返回值`为 0
- [检查并更新无效游戏玩家](#检查并更新无效游戏玩家)
- [更新游戏停止状态](#更新游戏停止状态)
- `返回值`：执行后是否不在游戏中（该指令是否成功结束游戏）

##### 更新游戏停止状态
> _/battleroyale api gameProcessManager finishGameIfShouldEnd_

- 若不在游戏中，`返回值`为 0
- 立即检查游戏胜利条件及结束条件
- `返回值`：执行后是否不在游戏中（该指令是否成功结束游戏）

#### 游戏管理
> _IGameManagement_

##### 检查并更新无效游戏玩家
> _/battleroyale api gameProcessManager checkAndUpdateInvalidGamePlayer_

立即更新最后记录的游戏玩家状态，淘汰无效玩家
- `返回值`：1

##### 游戏中传送至大厅
> _/battleroyale api gameProcessManager teleportToLobbyInGame [player]_

- player：实体选择器
- 若实体选择器不为生物，`返回值`为 0
- 传送玩家至大厅，如果正在游戏中则淘汰
- `返回值`：1

##### 游戏后传送
> _/battleroyale api gameProcessManager teleportAfterGame [teleportWinner] [teleportNonWinner]_

- 若无法获取游戏维度，`返回值`为 0
- 若在游戏中，将不会传送
- teleportWinner：是否传送获胜游戏玩家
- teleportNonWinner：是否传送非获胜游戏玩家
- 若不传送，则改为发送传送消息
- `返回值`：1

##### 观战游戏
> _/battleroyale api gameProcessManager spectateGame [player]_

- player：实体选择器
- 若实体选择器不为玩家，`返回值`为 0
- `返回值`：是否成功观战

##### 治疗全体游戏玩家
> _/battleroyale api gameProcessManager healGamePlayers_

- 若无法获取游戏维度，`返回值`为 0
- `返回值`：1

##### 结算并添加获胜者
> _/battleroyale api gameProcessManager finishGameAddWinner [hasWinner]_

- 若不在游戏中，`返回值`为 0
- 按获胜条件添加获胜游戏玩家和获胜游戏队伍
- `返回值`：执行后是否不在游戏中（该指令是否成功结束游戏）

#### 游戏通知
> _IGameNotification_

##### 发送获胜结果
> _/battleroyale api gameProcessManager sendWinnerResult_

- 若无法获取游戏维度，`返回值`为 0
- `返回值`：1

##### 通知获胜者
> _/battleroyale api gameProcessManager notifyWinner [player] [id]_

- player：用实体选择器选中并获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- id：[粒子配置](https://github.com/XColorful/BattleRoyale/wiki/Particle-config)id
- `返回值`：1

##### 发送观战消息
> _/battleroyale api gameProcessManager sendGameSpectateMessage [player] [allowSpectate]_

- player：实体选择器
- 若实体选择器不为玩家，`返回值`为 0
- allowSpectate：是否允许观战
- `返回值`：1

##### 发送倒地消息
> _/battleroyale api gameProcessManager sendDownMessage byPlayer [player]_
> 
> _/battleroyale api gameProcessManager sendDownMessage byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- 发送该游戏玩家倒地消息
- `返回值`：1

##### 发送复活消息
> _/battleroyale api gameProcessManager sendReviveMessage byPlayer [player]_
> 
> _/battleroyale api gameProcessManager sendReviveMessage byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- 发送该游戏玩家复活消息
- `返回值`：1

##### 发送淘汰消息
> _/battleroyale api gameProcessManager sendEliminateMessage byPlayer [player]_
> 
> _/battleroyale api gameProcessManager sendEliminateMessage byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 0
- 发送该游戏玩家淘汰消息
- `返回值`：1

### 游戏进程管理器（死斗模式）
> _IDeathMatchProcessManager_

> _/battleroyale api gameProcessManager deathMatch [...]_

- 若未[注册死斗模式游戏进程管理器](https://github.com/XColorful/BattleRoyale/wiki/Register-command#注册死斗模式游戏进程管理器)，`返回值`为 -1
 
#### 死斗模式信息获取
> _IDeathMatchInfoGetter_

##### 获取当前最大击杀数
> _/battleroyale api gameProcessManager deathMatch getCurrentMaxKill_

- `返回值`：当前最大游戏玩家或游戏队伍击杀数

#### 死斗模式数据管理
> _IDeathMatchDataManagement_

##### 添加游戏玩家击杀数
> _/battleroyale api gameProcessManager deathMatch addGamePlayerKill [addKill] byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch addGamePlayerKill [addKill] byId [id]_

- addKill：添加的击杀数
- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 -2
- `返回值`：是否计入成功

##### 添加游戏队伍击杀数
> _/battleroyale api gameProcessManager deathMatch addGameTeamKill [addKill] byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch addGameTeamKill [addKill] byId [id]_

- addKill：添加的击杀数
- player：用实体选择器选中并获取游戏玩家
- id：用游戏队伍ID选中游戏队伍
- 若不存在对应的游戏玩家或游戏队伍，`返回值`为 -2
- `返回值`：是否计入成功

##### 添加并跟踪再出生玩家
> _/battleroyale api gameProcessManager deathMatch addAndTrackRestandingGamePlayer byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch addAndTrackRestandingGamePlayer byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 -2
- `返回值`：是否添加成功

#### 死斗模式游戏管理
> _IDeathMatchGameManagement_

##### 再出生玩家
> _/battleroyale api gameProcessManager deathMatch respawnGamePlayer byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch respawnGamePlayer byId [id]_

- player：用实体选择器选中并获取游戏玩家
- id：用游戏玩家ID获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 -2
- `返回值`：是否成功再出生

# English

### Game process manager
> _IGameProcessManager_

> _/battleroyale api IGameProcessManager [...]_

##### Check if game should end and finish
> _/battleroyale api gameProcessManager checkIfGameShouldEndAndFinish_

- If the game is not in progress, the `return value` is 0.
- [Check and update invalid game player](#Check-and-update-invalid-game-player)
- [Finish game if should end](#Finish-game-if-should-end)
- `return value`: whether the game is not in progress after execution (whether this command successfully ended the game)

##### Finish game if should end
> _/battleroyale api gameProcessManager finishGameIfShouldEnd_

- If the game is not in progress, the `return value` is 0.
- Immediately checks game victory and ending conditions.
- `return value`: whether the game is not in progress after execution (whether this command successfully ended the game)

#### Game management
> _IGameManagement_

##### Check and update invalid game player
> _/battleroyale api gameProcessManager checkAndUpdateInvalidGamePlayer_

Immediately updates the last recorded status of game players and eliminates invalid players:
- `return value`: 1

##### Teleport to lobby in game
> _/battleroyale api gameProcessManager teleportToLobbyInGame [player]_

- player: entity selector
- If the entity selector is not a living entity, the `return value` is 0.
- Teleports the player to the lobby, eliminate the game player if the game is in progress.
- `return value`: 1

##### Teleport after game
> _/battleroyale api gameProcessManager teleportAfterGame [teleportWinner] [teleportNonWinner]_

- If the game dimension cannot be retrieved, the `return value` is 0.
- If the game is in progress, teleportation will not occur.
- teleportWinner: whether to teleport winner game players
- teleportNonWinner: whether to teleport non-winner game players
- if teleportation is disabled, a teleport message will be sent instead
- `return value`: 1

##### Spectate game
> _/battleroyale api gameProcessManager spectateGame [player]_

- player: entity selector
- If the entity selector is not a player, the `return value` is 0.
- `return value`: whether spectating was successfully enabled

##### Heal game players
> _/battleroyale api gameProcessManager healGamePlayers_

- If the game dimension cannot be retrieved, the `return value` is 0.
- `return value`: 1

##### Finish game add winner
> _/battleroyale api gameProcessManager finishGameAddWinner [hasWinner]_

- If the game is not in progress, the `return value` is 0.
- Adds winner game players and teams based on victory conditions.
- `return value`: whether the game is not in progress after execution (whether this command successfully ended the game)

#### Game notification
> _IGameNotification_

##### Send winner result
> _/battleroyale api gameProcessManager sendWinnerResult_

- If the game dimension cannot be retrieved, the `return value` is 0.
- `return value`: 1

##### Notify winner
> _/battleroyale api gameProcessManager notifyWinner [player] [id]_

- player: selects game player using an entity selector
- If the corresponding game player does not exist, the `return value` is 0.
- id: [Particle config](https://github.com/XColorful/BattleRoyale/wiki/Particle-config#English) id
- `return value`: 1

##### Send spectate message
> _/battleroyale api gameProcessManager sendGameSpectateMessage [player] [allowSpectate]_

- player: entity selector
- If the entity selector is not a player, the `return value` is 0.
- allowSpectate: whether spectating is allowed
- `return value`: 1

##### Send down message
> _/battleroyale api gameProcessManager sendDownMessage byPlayer [player]_
> 
> _/battleroyale api gameProcessManager sendDownMessage byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the corresponding game player does not exist, the `return value` is 0.
- Sends the "downed" message for the specified game player.
- `return value`: 1

##### Send revive message
> _/battleroyale api gameProcessManager sendReviveMessage byPlayer [player]_
> 
> _/battleroyale api gameProcessManager sendReviveMessage byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the corresponding game player does not exist, the `return value` is 0.
- Sends the "revived" message for the specified game player.
- `return value`: 1

##### Send eliminate message
> _/battleroyale api gameProcessManager sendEliminateMessage byPlayer [player]_
> 
> _/battleroyale api gameProcessManager sendEliminateMessage byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the corresponding game player does not exist, the `return value` is 0.
- Sends the "eliminated" message for the specified game player.
- `return value`: 1

### Game process manager (DeathMatch)
> _IDeathMatchProcessManager_

> _/battleroyale api gameProcessManager deathMatch [...]_

- If not [Register DeathMatch GameProcessManager](https://github.com/XColorful/BattleRoyale/wiki/Register-command#Register-DeathMatch-GameProcessManager), the `return value` is -1.

#### DeathMatch info getter
> _IDeathMatchInfoGetter_

##### Get current max kill
> _/battleroyale api gameProcessManager deathMatch getCurrentMaxKill_

- `return value`: the current maximum number of kills among game players or teams

#### DeathMatch data management
> _IDeathMatchDataManagement_

##### Add game player kill
> _/battleroyale api gameProcessManager deathMatch addGamePlayerKill [addKill] byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch addGamePlayerKill [addKill] byId [id]_

- addKill: the number of kills to add
- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is -2.
- `return value`: whether the record was successfully added

##### Add game team kill
> _/battleroyale api gameProcessManager deathMatch addGameTeamKill [addKill] byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch addGameTeamKill [addKill] byId [id]_

- addKill: the number of kills to add
- player: selects game player using an entity selector
- id: selects game team using a game team ID
- If the game player or team does not exist, the `return value` is -2.
- `return value`: whether the record was successfully added

##### Add and track restanding game player
> _/battleroyale api gameProcessManager deathMatch addAndTrackRestandingGamePlayer byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch addAndTrackRestandingGamePlayer byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is -2.
- `return value`: whether the addition was successful

#### DeathMatch game management
> _IDeathMatchGameManagement_

##### Respawn game player
> _/battleroyale api gameProcessManager deathMatch respawnGamePlayer byPlayer [player]_
> 
> _/battleroyale api gameProcessManager deathMatch respawnGamePlayer byId [id]_

- player: selects game player using an entity selector
- id: selects game player using a game player ID
- If the game player does not exist, the `return value` is -2.
- `return value`: whether the respawn was successful