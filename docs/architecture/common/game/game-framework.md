[English](#English)

# 游戏框架

游戏框架设计为**主管理器**调度各**子管理器**，将各功能拆分至子管理器：

|游戏子管理器|对应接口名称|职责简述|
|:--|:--|:--|
|游戏管理器（主管理器）|`IGameManager`|调度其他游戏子管理器|
|游戏规则管理器|`IGameruleManager`|设置游戏规则，切换游戏模式|
|游戏大厅管理器|`IGameLobbyManager`|提供大厅传送、大厅无敌|
|游戏物资刷新管理器|`IGameLootManager`|持续执行物资刷新|
|游戏进程管理器|`IGameProcessManager`|更新游戏状态、实现特定游戏类型|
|出生管理器|`ISpawnManager`|游戏开始时传送玩家|
|统计管理器|`IStatsManager`|记录游戏统计数据|
|队伍管理器|`ITeamManager`|管理游戏队伍、游戏玩家|
|区域管理器|`IZoneManager`|持续更新游戏区域|

### 游戏子管理器

主管理器及子管理器都实现的接口
> 用指令调用主管理器执行某功能，主管理器应调度所有子管理器也执行相应功能

[![IGameSubManager](/docs/api/game/IGameSubManager.md)](/docs/api/game/IGameSubManager.md)

#### 游戏流程

读取配置 → 初始化游戏 → 开始游戏 → 持续运行/提前结束
- initGameConfig：从配置文件读取游戏配置，该阶段不应有多余操作
- initGame：初始化游戏，注册相关事件处理器、集中创建玩家队伍、预计算性能密集型事务等
- startGame：若成功开始游戏，则当前`游戏时间`为 0，下一刻`游戏时间`为1并调度子管理器
- onGameTick：每`游戏时间`（游戏刻）调度子管理器，更新游戏状态
- stopGame：强制停止游戏，不进行结算

##### 特殊流程

“一键开始游戏”：执行`startGame`时，若有子管理器未通过`isReady`则应自动执行一次`initGame`；执行`initGame`时，若有子管理器未通过`isConfigPrepared`则应自动执行一次`initGameConfig`
> 在`initGame`和`startGame`刚执行时立即进行一次检查

连续初始化/配置时效性：主管理器在`initGame`成功后，会使自身通过`isReady`但不通过`isConfigPrepared`，使下次`initGame`时重新执行`initGameConfig`，以确保配置时效性

游戏人数检查：主管理器执行`initGame`后，队伍管理器（因玩家人数不够）未通过`isReady`应立即提示“人数不足”；若不希望在`initGame`时提示“人数不足”，可以将人数检查推迟至`startGame`以避免
> 模组默认的主管理器不检查队伍管理器的`isReady`，使得会提示“人数不足”但不影响`startGame`开头的检查

## 游戏管理器
> 前往[游戏管理器](./game-manager.md)

## 游戏规则管理器
> 前往[游戏规则管理器](./gamerule/gamerule-manager.md)

## 游戏大厅管理器
> 前往[游戏大厅管理器](./lobby/game-lobby-manager.md)

## 游戏物资刷新管理器
> 前往[游戏物资刷新管理器](./loot/game-loot-manager.md)

## 游戏进程管理器
> 前往[游戏进程管理器](./process/battleroyale/br-game-process-manager.md)

## 出生管理器
> 前往[出生管理器](./spawn/spawn-manager.md)

## 统计管理器
> 前往[统计管理器](./stats/stats-manager.md)

## 队伍管理器
> 前往[队伍管理器](./team/team-manager.md)

## 区域管理器
> 前往[区域管理器](./zone/zone-manager.md)

# English

The game framework is designed with a **Main Manager** orchestrating various **Sub-Managers**, splitting functionalities into distinct components:

|Game Sub-Manager|Corresponding Interface|Responsibility Summary|
|---|---|---|
|Game Manager (Main)|`IGameManager`|Orchestrates other Game Sub-Managers.|
|Gamerule Manager|`IGameruleManager`|Sets game rules and switches game modes.|
|Game Lobby Manager|`IGameLobbyManager`|Provides lobby teleportation and lobby invulnerability.|
|Game Loot Manager|`IGameLootManager`|Continuously executes loot generation.|
|Game Process Manager|`IGameProcessManager`|Updates game state and implements specific game types/flows.|
|Spawn Manager|`ISpawnManager`|Teleports players at the start of the game.|
|Statistics Manager|`IStatsManager`|Records game statistics.|
|Team Manager|`ITeamManager`|Manages game teams and game players.|
|Zone Manager|`IZoneManager`|Continuously updates game zones.|

### Game Sub-Manager

The common interface implemented by both the Main Manager and all Sub-Managers.

> When a command invokes the Main Manager to execute a function, the Main Manager should delegate the execution of the corresponding function to all Sub-Managers.

[![IGameSubManager](/docs/api/game/IGameSubManager.md)](/docs/api/game/IGameSubManager.md)

#### Game Lifecycle

Read Config → Initialize Game → Start Game → Continuous Operation / Early Termination
- initGameConfig: Reads game configuration from config files; no excessive operations should occur at this stage.
- initGame: Initializes the game, including registering relevant event handlers, centralizing the creation of player teams, pre-calculating performance-intensive tasks, etc.
- startGame: If the game successfully starts, the current `game time` is 0, the next tick the `game time` will be 1, and Sub-Managers are dispatched.
- onGameTick: Dispatches Sub-Managers every `game time` (game tick) to update the game state.
- stopGame: Forcefully stops the game without performing victory settlement/cleanup.

##### Special Flow

"One-Click Start Game": When executing `startGame`, if any Sub-Manager is not `isReady`, `initGame` should be executed automatically once. When executing `initGame`, if any Sub-Manager is not `isConfigPrepared`, `initGameConfig` should be executed automatically once.
> A check is immediately performed right after `initGame` and `startGame` are executed.

Sequential Initialization / Config Freshness: After the Main Manager successfully executes `initGame`, it will set itself to be `isReady` but not `isConfigPrepared`. This ensures that the next time `initGame` is called, it will be forced to re-execute `initGameConfig`, guaranteeing config freshness.

Player Count Check: If the Team Manager fails `isReady` after the Main Manager executes `initGame` (e.g., due to insufficient player count), a "Not enough players" message should be immediately prompted. If you do not want to prompt "Not enough players" during `initGame`, the player count check can be deferred until `startGame`.
> The mod's default Main Manager does not check the Team Manager's `isReady`, which will cause the "Not enough players" prompt but does not affect the check at the start of `startGame`.

## Game Manager
> Go to [Game Manager](./game-manager.md#English)

## Gamerule Manager
> Go to [Gamerule Manager](./gamerule/gamerule-manager.md#English)

## Game Lobby Manager
> Go to [Game Lobby Manager](./lobby/game-lobby-manager.md#English)

## Game Loot Manager
> Go to [Game Loot Manager](./loot/game-loot-manager.md#English)

## Game Process Manager
> Go to [Game Process Manager](./process/battleroyale/br-game-process-manager.md#English)

## Spawn Manager
> Go to [Spawn Manager](./spawn/spawn-manager.md#English)

## Statistics Manager
> Go to [Statistics Manager](./stats/stats-manager.md#English)

## Team Manager
> Go to [Team Manager](./team/team-manager.md#English)

## Zone Manager
> Go to [Zone Manager](./zone/zone-manager.md#English)