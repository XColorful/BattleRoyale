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
