[English](#English)

### 游戏大厅管理器
> _IGameLobbyManager_

> _/battleroyale api gameLobbyManager [...]_

#### 游戏大厅读取
> _IGameLobbyReadApi_

##### 发送大厅传送消息
> _/battleroyale api gameLobbyManager sendLobbyTeleportMessage [player] [isWinner]_

- player：实体选择器
- 若实体选择器不为玩家，`返回值`为 0
- isWinner：是否为获胜玩家消息类型
- `返回值`：1

##### 获取游戏大厅是否创建
> _/battleroyale api gameLobbyManager isLobbyCreated_

- `返回值`：当前是否已创建游戏大厅

##### 获取游戏大厅是否启用无敌
> _/battleroyale api gameLobbyManager lobbyMuteki_

- `返回值`：游戏大厅是否启用[大厅无敌](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取游戏大厅是否启用治疗
> _/battleroyale api gameLobbyManager lobbyHeal_

- `返回值`：游戏大厅是否启用[大厅治疗](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取游戏大厅是否更改游戏模式
> _/battleroyale api gameLobbyManager _

- `返回值`：传送至游戏大厅是否[更改游戏模式](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取游戏大厅是否吐出背包
> _/battleroyale api gameLobbyManager teleportDropInventory_

- `返回值`：传送至大厅是否[吐出背包物品](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 获取游戏大厅是否清空背包
> _/battleroyale api gameLobbyManager teleportClearInventory_

- `返回值`：传送至大厅是否[清空背包](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

##### 测试是否处于游戏大厅内
> _/battleroyale api gameLobbyManager isInLobbyRange [xyz]_

在指令 _/execute in_ 维度测试：
- xyz：指定的测试位置
- `返回值`：测试位置是否位于大厅范围内

##### 测试游戏大厅无敌是否生效
> _/battleroyale api gameLobbyManager canMuteki [player]_

- player：实体选择器，指定测试生物
- 若实体选择器不为生物，`返回值`为 0
- `返回值`：游戏大厅无敌是否对测试生物生效

#### 大厅功能
> _ILobbyFuncApi_

##### 大厅治疗
> _/battleroyale api gameLobbyManager healPlayer [player]_

- player：实体选择器
- 若实体选择器不为生物，`返回值`为 0
- `返回值`：1

##### 大厅传送
> _/battleroyale api gameLobbyManager teleportToLobby [player]_

- player：实体选择器
- 若实体选择器不为生物，`返回值`为 0
- `返回值`：是否成功传送

##### 设置大厅
> _/battleroyale api gameLobbyManager [pos]_
> 
> _/battleroyale api gameLobbyManager [pos] [xyz]_
> 
> _/battleroyale api gameLobbyManager [pos] [xyz] [lobbyMuteki] [lobbyHeal] [lobbyChangeGamemode] [teleportDropInventory] [teleportClearInventory]_

手动[设置大厅](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)：
- pos：大厅坐标
- xyz：大厅的规模
- lobbyMuteki：是否启用[大厅无敌](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)
- lobbyHeal：是否启用[大厅治疗](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)
- lobbyChangeGamemode：传送至游戏大厅是否[更改游戏模式](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)
- teleportDropInventory：传送至大厅是否[吐出背包物品](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)
- teleportClearInventory传送至大厅是否[清空背包](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#大逃杀规则配置)

# English

### Game lobby manager
> _IGameLobbyManager_

> _/battleroyale api IGameLobbyManager [...]_

#### Game lobby read api
> _IGameLobbyReadApi_

##### Send lobby teleport message
> _/battleroyale api gameLobbyManager sendLobbyTeleportMessage [player] [isWinner]_

- player: entity selector
- If the entity selector is not a player, the `return value` is 0.
- isWinner: whether it is a winner message type
- `return value`: 1

##### Is lobby created
> _/battleroyale api gameLobbyManager isLobbyCreated_

- `return value`: whether the game lobby is currently created

##### Has lobby muteki
> _/battleroyale api gameLobbyManager lobbyMuteki_

- `return value`: whether the lobby has [lobbyMuteki](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) enabled

##### Has lobby heal
> _/battleroyale api gameLobbyManager lobbyHeal_

- `return value`: whether the lobby has [lobbyHeal](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) enabled

##### Has lobby change gamemode
> _/battleroyale api gameLobbyManager _

- `return value`: whether to [change the gamemode](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) when teleporting to the lobby

##### Has lobby teleport drop inventory
> _/battleroyale api gameLobbyManager teleportDropInventory_

- `return value`: whether to [drop the player's inventory](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) when teleporting to the lobby

##### Has teleport clear inventory
> _/battleroyale api gameLobbyManager teleportClearInventory_

- `return value`: whether to [clear player's inventory](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) when teleporting to the lobby

##### Is in lobby range
> _/battleroyale api gameLobbyManager isInLobbyRange [xyz]_

Test in the _/execute in_ dimension:
- xyz: specified test position
- `return value`: whether the test position is within the lobby range

##### Can muteki
> _/battleroyale api gameLobbyManager canMuteki [player]_

- player: entity selector, specifies the test living entity
- If the entity selector is not a living entity, the `return value` is 0.
- `return value`: whether game lobby invulnerability is effective for the test living entity

#### Lobby function api
> _ILobbyFuncApi_

##### Lobby heal
> _/battleroyale api gameLobbyManager healPlayer [player]_

- player: entity selector
- If the entity selector is not a living entity, the `return value` is 0.
- `return value`: 1

##### Teleport to lobby
> _/battleroyale api gameLobbyManager teleportToLobby [player]_

- player: entity selector
- If the entity selector is not a living entity, the `return value` is 0.
- `return value`: whether the teleport was successful

##### Set lobby
> _/battleroyale api gameLobbyManager [pos]_
> 
> _/battleroyale api gameLobbyManager [pos] [xyz]_
> 
> _/battleroyale api gameLobbyManager [pos] [xyz] [lobbyMuteki] [lobbyHeal] [lobbyChangeGamemode] [teleportDropInventory] [teleportClearInventory]_

Manually [sets the lobby](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule):
- pos: lobby coordinates
- xyz: the size of the lobby
- lobbyMuteki: whether to enable [lobbyMuteki](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule)
- lobbyHeal: whether to enable [lobbyHeal](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule)
- lobbyChangeGamemode: whether to [change the gamemode](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) when teleporting to the lobby
- teleportDropInventory: whether to [drop the player's inventory](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) when teleporting to the lobby
- teleportClearInventory: whether to [clear player's inventory](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#BattleRoyale-gamerule) when teleporting to the lobby