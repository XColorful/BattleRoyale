[English](#English)

## 游戏进程管理器

- checkIfGameShouldEnd：完整检查游戏状态，如符合条件则直接调用`游戏管理器`游戏主管理器功能结束游戏并进行胜利结算

[![IGameProcessManager](/docs/api/game/process/IGameProcessManager.md)](/docs/api/game/process/IGameProcessManager.md)

#### 游戏进程管理器启动流程

- startGame：执行一次`checkAndUpdateInvalidGamePlayer`，供`游戏时间`为1时使用，并清除无效玩家

### 游戏进程管理

#### 游戏状态管理

- checkAndUpdateInvalidGamePlayer：检查所有玩家，更新不在线时长及最后有效位置，处理无效玩家
- teleportToLobbyInGame：玩家手动[传送至大厅](https://github.com/XColorful/BattleRoyale/wiki/Game-command#传送至大厅)，通常应视为离开游戏而直接淘汰
- teleportAfterGame：游戏结束后对胜利玩家与其余玩家进行一次传送
- spectateGame：只能由`游戏管理器`调用，返回玩家是否能观战游戏，并使玩家观战游戏
- healGamePlayers：开始游戏时由`游戏管理器`调用，恢复所有游戏玩家状态
- finishGameAddWinner：结算胜利玩家，调用`游戏管理器`添加胜利玩家及胜利队伍

[![IGameManagement](/docs/api/game/process/IGameManagement.md)](/docs/api/game/process/IGameManagement.md)

#### 游戏事件处理

由`游戏管理器`派发的事件，如大逃杀游戏在玩家死亡时即淘汰该玩家对应的游戏玩家
> - 该接口只能由`游戏管理器`调用
> - 如有特殊机制如大逃杀游戏可能将倒地事件判定应为淘汰事件，则应调用`游戏管理器`并由其发送相关事件

[![IGameEventHandler](/docs/api/game/process/IGameEventHandler.md)](/docs/api/game/process/IGameEventHandler.md)

#### 发送游戏消息

- sendWinnerResult：在聊天栏发送胜利结算信息
- notifyWinner：向胜利玩家发送消息
- sendGameSpectateMessage：提供观战游戏指令快捷方式
- sendDownMessage：游戏玩家倒地时聊天栏消息
- sendReviveMessage：游戏玩家复活时聊天栏消息
- sendEliminateMessage：游戏玩家被淘汰时聊天栏消息

[![IGameNotification](/docs/api/game/process/IGameNotification.md)](/docs/api/game/process/IGameNotification.md)

# English

## Game Process Manager

- checkIfGameShouldEnd: Performs a complete check of the game status; if conditions are met, it directly calls the `Game Manager`'s main functions to end the game and proceed with victory settlement.

[![IGameProcessManager](/docs/api/game/process/IGameProcessManager.md)](/docs/api/game/process/IGameProcessManager.md)

#### Game Process Manager Startup Flow

- startGame: Executes `checkAndUpdateInvalidGamePlayer` once for use when `game time` is 1, and clears invalid players.

### Game Process Management

#### Game Status Management

- checkAndUpdateInvalidGamePlayer: Checks all players, updates offline duration and last valid position, and handles invalid players. 
- teleportToLobbyInGame: Player manually [Teleport to lobby](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Teleport-to-lobby), which should typically be treated as leaving the game and resulting in elimination.
- teleportAfterGame: Performs a single teleportation for winning players and remaining players after the game ends.
- spectateGame: Can only be called by the `Game Manager`. Returns whether the player can spectate the game and enables spectator mode for the player.
- healGamePlayers: Called by the `Game Manager` at the start of the game to restore the state of all game players.
- finishGameAddWinner: Settles the winning players, calling the `Game Manager` to add winning players and teams.

[![IGameManagement](/docs/api/game/process/IGameManagement.md)](/docs/api/game/process/IGameManagement.md)

#### Game Event Handling

Events dispatched by the `Game Manager`. For example, in a BattleRoyale game, when a player dies, the corresponding game player is eliminated.
> - This interface can only be called by the `Game Manager`.
> - If there are special mechanisms (e.g., a Battle Royale game might treat a downed event as an elimination event), it should call the Game Manager which then dispatches the relevant events.

[![IGameEventHandler](/docs/api/game/process/IGameEventHandler.md)](/docs/api/game/process/IGameEventHandler.md)

#### Sending Game Message

- sendWinnerResult: Sends the victory settlement information in the chat.
- notifyWinner: Sends a message to the winning players.
- sendGameSpectateMessage: Provides a shortcut for the spectate game command.
- sendDownMessage: Chat message when a game player is downed.
- sendReviveMessage: Chat message when a game player is revived.
- sendEliminateMessage: Chat message when a game player is eliminated.

[![IGameNotification](/docs/api/game/process/IGameNotification.md)](/docs/api/game/process/IGameNotification.md)