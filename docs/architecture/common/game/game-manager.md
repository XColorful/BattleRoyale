[English](#English)

## 游戏管理器

- 外部只需要获取全局静态API`IGameManager`即可，`IGameManager`具体类再往下层层委派功能，而未将`IGameManager`拆分成不同类实现且未添加权限检查
- 外部调用视为拥有所有权限（即使只调用普通接口），不应调用的接口已经添加了`@Internal`或`@Deprecated`并说明
> _`IGameManager`继承的各接口只是对`游戏管理器`本身应有功能分类而进行拆分，如游戏管理的配置读写，游戏事件处理（本身就是`IGameManager`的职责），及其特殊状态的设置……_
>
> _本模组各`游戏子管理器`的默认实现类会在代码较多时使用protected抽离函数体到同包下代理的“门面模式”，使得各`游戏子管理器`对应的接口都像`IGameManager`提供了**1个**该职责下通用且功能全面的API_
>
> _`IGameManager`这个“部门”开放共享，访客只需要知道`IGameManager`而不需要知道其内部资源如何分层管理就能直接获取，让访客任意使用其功能并**自行调试**_

[![IGameManager](/docs/api/game/IGameManager.md)](/docs/api/game/IGameManager.md)

### 游戏主管理器

- 主管理器调度各子管理器
- 提供其下所有子管理器的获取接口

[![IGameMainManager](/docs/api/game/IGameMainManager.md)](/docs/api/game/IGameMainManager.md)

#### 游戏主管理器功能

- spectateGame：由`游戏进程管理器`接管，返回玩家是否能观战游戏，并使玩家观战游戏
- finishGame：区别于`stopGame`，仅允许`游戏进程管理器`调用该方法，并在符合条件时立即结束游戏并进行胜利结算

[![IGameFunc](/docs/api/game/IGameFunc.md)](/docs/api/game/IGameFunc.md)

#### 游戏信息

- getGameTime：获取当前`游戏时间`，单位为tick
- getGameId：获取当前`游戏ID`（UUID）
- getGlobalCenterOffset：获取`全局偏移`（用于切换地图）
- getWinnerTeamTotal：获取最大胜利队伍数
- getServerLevel：获取当前游戏维度

[![IGameInfoGetter](/docs/api/game/IGameInfoGetter.md)](/docs/api/game/IGameInfoGetter.md)

### 游戏ID

游戏管理器为每局游戏创建一个 UUID 作为`游戏ID`，同时还用于：
- `游戏物资刷新器`刷新物品/生成实体/更新方块实体时写入游戏ID（持久化保存）以辨别是否已刷新过/需要清理
- 让其他模组刷新物品/生成实体后手动写入游戏ID进行标记，以**防止**被`游戏物资刷新器`**自动清理**

[![IGameApiGetter](/docs/api/game/IGameApiGetter.md)](/docs/api/game/IGameApiGetter.md)
[![IGameIdReadApi](/docs/api/game/IGameIdReadApi.md)](/docs/api/game/IGameIdReadApi.md)
[![IGameIdWriteApi](/docs/api/game/IGameIdWriteApi.md)](/docs/api/game/IGameIdWriteApi.md)

### 选用子管理器配置

本模组支持任意数量的配置预设，而每局游戏只能选中其一

[![IGameConfigGetter](/docs/api/game/IGameConfigGetter.md)](/docs/api/game/IGameConfigGetter.md)

手动设置配置ID后需要再次`initGameConfig`以重新读取

[![IGameConfigSetter](/docs/api/game/IGameConfigSetter.md)](/docs/api/game/IGameConfigSetter.md)

### 设置游戏管理器属性

- 游戏步长用于**跳过**`游戏时间`，特别是用于**快速测试区域配置**，而**不**应用于加速游戏
- 游戏管理器缓存一个`全局偏移`，使`区域管理器`和`出生管理器`能方便地切换地图并调整出生地点
- 每局游戏在同一维度（_ServerLevel_）下更新游戏状态，玩家离开维度则无法获取，且无法接收游戏消息（服务端→客户端通信）

[![IGameStatusSetter](/docs/api/game/IGameStatusSetter.md)](/docs/api/game/IGameStatusSetter.md)

### 游戏事件处理

将游戏事件委派给`游戏进程管理器`处理，更改部分规则即可转化为其他游戏类型
> - 该接口已经由模组事件处理器调用，游戏管理器应只在`游戏进程管理器`接管前后发送相应事件
> - 更改事件处理应通过替换`游戏进程管理器`实现

[![IGameEventReceiver](/docs/api/game/IGameEventReceiver.md)](/docs/api/game/IGameEventReceiver.md)

# English

## Game Manager

- Externally, only the static API `IGameManager` needs to be accessed. The concrete `IGameManager` class then delegates functionality layer by layer, without splitting `IGameManager` into different classes or adding permission checks._
- External calls are treated as having full permissions (even when only calling ordinary interfaces). Interfaces that should not be called externally have been annotated with `@Internal` or `@Deprecated`._
> _The interfaces inherited by `IGameManager` are simply functionalities categorized and broken down by the `Game Manager` itself, such as game management configuration reading and writing, game event handling (which is inherently the responsibility of `IGameManager`), and the setting of special states, etc._
>
> _The default implementation classes of each `Game Sub-Manager` in this mod will use the `protected` keyword and extract function bodies to a proxy within the same package—a "facade pattern"—when the code is extensive. This ensures that each `Game Sub-Manager`'s corresponding interface provides a single, comprehensive API for its specific responsibility, just like `IGameManager`._
>
> _The `IGameManager` "department" is open and shared. Visitors only need to know `IGameManager`, without needing to know how its internal resources are hierarchically managed, to directly access it, allow visitors to freely use its functions, and **debug on their own**._

[![IGameManager](/docs/api/game/IGameManager.md)](/docs/api/game/IGameManager.md)

### Game Main Manager

- The Main Manager orchestrates all Sub-Managers.
- Provides getter interfaces for all its Sub-Managers.

[![IGameMainManager](/docs/api/game/IGameMainManager.md)](/docs/api/game/IGameMainManager.md)

#### Game Main Manager Function

- spectateGame: Handled by the `Game Process Manager`. Returns whether the player can spectate the game and places the player into spectator mode.
- finishGame: Distinct from `stopGame`. Only the `Game Process Manager` is allowed to call this method, which immediately ends the game and settles the victor(s) if conditions are met.

[![IGameFunc](/docs/api/game/IGameFunc.md)](/docs/api/game/IGameFunc.md)

#### Game Information

- getGameTime: Gets the current `game time` in ticks.
- getGameId: Gets the current `Game ID` (UUID).
- getGlobalCenterOffset: Gets the `Global Offset` (for map switching/relocation).
- getWinnerTeamTotal: Gets the maximum number of winning teams allowed.
- getServerLevel: Gets the current game dimension/level.

[![IGameInfoGetter](/docs/api/game/IGameInfoGetter.md)](/docs/api/game/IGameInfoGetter.md)

### Game ID

The Game Manager creates a UUID as the `Game ID` for each game session, which is also used for:
- The `Game Loot Manager` writes the Game ID when refreshing items/spawning entities/updating block entities (for persistent storage) to determine if they have been refreshed/need cleanup.
- Allowing other mods to manually write the Game ID after refreshing items/spawning entities for marking, to **prevent** being **automatically cleaned up** by the `Game Loot Manager`.

[![IGameApiGetter](/docs/api/game/IGameApiGetter.md)](/docs/api/game/IGameApiGetter.md)

[![IGameIdReadApi](/docs/api/game/IGameIdReadApi.md)](/docs/api/game/IGameIdReadApi.md)

[![IGameIdWriteApi](/docs/api/game/IGameIdWriteApi.md)](/docs/api/game/IGameIdWriteApi.md)

### Select Sub-Manager Configuration

This mod supports an arbitrary number of configuration presets, but only one can be selected per game session.

[![IGameConfigGetter](/docs/api/game/IGameConfigGetter.md)](/docs/api/game/IGameConfigGetter.md)

After manually setting the config ID, `initGameConfig` must be called again to re-read the configuration.

[![IGameConfigSetter](/docs/api/game/IGameConfigSetter.md)](/docs/api/game/IGameConfigSetter.md)

### Set Game Manager Property

- The Game Step is used to **skip** `game time`, particularly for **quickly testing zone configurations**, and should **not** be used to speed up the game.
- The Game Manager caches a `Global Offset`, allowing the `Zone Manager` and `Spawn Manager` to easily switch maps and adjust spawn locations.
- Each game session updates its state within the same dimension (_ServerLevel_). Players who leave this dimension cannot retrieve the state or receive game messages (server → client communication).

[![IGameStatusSetter](/docs/api/game/IGameStatusSetter.md)](/docs/api/game/IGameStatusSetter.md)

### Game Event Handling

Game events are delegated to the `Game Process Manager` for handling; changing some rules can transform the game into a different game type.
> - This interface is already called by the mod's event handler. The Game Manager should only send corresponding events before and after the Game Process Manager takes over.
> - Changing event handling should be achieved by replacing the implementation of the Game Process Manager.

[![IGameEventReceiver](/docs/api/game/IGameEventReceiver.md)](/docs/api/game/IGameEventReceiver.md)