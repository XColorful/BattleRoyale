[English](#English)

## 游戏大厅管理器

#### 游戏大厅管理器启动流程

- initGameConfig：读取配置，使[传送至大厅](https://github.com/XColorful/BattleRoyale/wiki/Game-command#传送至大厅)生效
- initGame：根据配置决定是否将玩家传送至大厅，模组默认的`游戏进程管理器`已经提前调度`队伍管理器`以保证获取到刚创建的游戏玩家

[![IGameLobbyManager](/docs/api/game/lobby/IGameLobbyManager.md)](/docs/api/game/lobby/IGameLobbyManager.md)

#### 大厅功能

- healPlayer：恢复游戏玩家状态
- teleportToLobby：传送回大厅

[![ILobbyFuncApi](/docs/api/server/utility/ILobbyFuncApi.md)](/docs/api/server/utility/ILobbyFuncApi.md)

#### 大厅API

- lobbyLevelKey：大厅所在维度的 _ResourceKey_
- canMuteki：当前位置及状态是否可以受**大厅无敌**保护

[![IGameLobbyReadApi](/docs/api/game/lobby/IGameLobbyReadApi.md)](/docs/api/game/lobby/IGameLobbyReadApi.md)

[![ILobbyReadApi](/docs/api/server/utility/ILobbyReadApi.md)](/docs/api/server/utility/ILobbyReadApi.md)

# English

## Game Lobby Manager

#### Game Lobby Manager Startup Flow

- initGameConfig: Reads configuration to enable [Teleport to lobby](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Teleport-to-lobby).
- initGame: Decides whether to teleport players to the lobby based on configuration. The mod's default `Game Process Manager` has pre-scheduled the `Team Manager` to ensure that newly created players are acquired.

[![IGameLobbyManager](/docs/api/game/lobby/IGameLobbyManager.md)](/docs/api/game/lobby/IGameLobbyManager.md)

#### Lobby Function

- healPlayer: Restores the game player's state.
- teleportToLobby: Teleports back to the lobby.

[![ILobbyFuncApi](/docs/api/server/utility/ILobbyFuncApi.md)](/docs/api/server/utility/ILobbyFuncApi.md)

#### Lobby API

- lobbyLevelKey: The _ResourceKey_ of the dimension where the lobby is located.
- canMuteki: Whether the current position and state allow the entity to be protected by **Lobby Invulnerability**.

[![IGameLobbyReadApi](/docs/api/game/lobby/IGameLobbyReadApi.md)](/docs/api/game/lobby/IGameLobbyReadApi.md)

[![ILobbyReadApi](/docs/api/server/utility/ILobbyReadApi.md)](/docs/api/server/utility/ILobbyReadApi.md)