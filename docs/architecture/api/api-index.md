[English](#English)

# API索引

## 算法

- [IAlgorithmApi](/docs/api/algorithm/IAlgorithmApi.md)：算法全能易用门面
	- [IDistribution](/docs/api/algorithm/IDistribution.md)：提供分布算法

## 客户端

- Event：事件
	- [IRenderGUIEventPost](/docs/api/client/event/IRenderGuiEventPost.md)：GUI渲染事件
	- [IRenderLevelStageEvent](/docs/api/client/event/IRenderLevelStageEvent.md)：世界渲染（区域渲染）
- Game：游戏
	- [IClientGameDataManager](/docs/api/client/game/IClientGameDataManager.md)：客户端游戏数据管理
	    - [IClientTeamDataManager](/docs/api/client/game/sub/IClientTeamDataManager.md)：客户端游戏队伍数据管理
	    - [IClientZoneDataManager](/docs/api/client/game/sub/IClientZoneDataManager.md)：客户端游戏区域数据管理
- Init：初始化
	- [IClientSetup](/docs/api/client/init/IClientSetup.md)：客户端初始化
      - 注册容器（方块实体`BlockEntity`）GUI
	- [IModEntityRender](/docs/api/client/init/IModEntityRender.md)：注册方块实体`BlockEntity`渲染
- Render：渲染
	- [IBlockModelRenderer](/docs/api/client/render/IBlockModelRenderer.md)：方块实体`BlockEntity`模型渲染接口
		- 用于渲染方块实体`BlockEntity`本身，或定制渲染（渲染物资刷新器 _battleroyale:loot_spawner_ 内的物品）
	- [IClientGuiRenderer](/docs/api/client/render/IClientGuiRenderer.md)：客户端GUI渲染接口
		- IClientGameInfoRenderer：客户端游戏信息显示
        - IClientTeamInfoRenderer：客户端队伍信息显示
	- [IClientLevelRenderer](/docs/api/client/render/IClientLevelRenderer.md)：客户端世界渲染接口
		- IClientSimpleZoneRenderer：普通区域渲染
		> - IClientSpectateRenderer：观战玩家（全体游戏玩家）渲染
		> - IClientTeamRenderer：队伍（队友）渲染
		- IClientZoneRenderer：区域渲染（包含圆/椭球渲染边数的参数获取）

## 双端

- [ISideOnly](/docs/api/common/ISideOnly.md)：提供是否运行在正确的端上的判定

### 效果
> ./api/effect

- [IEffectManager](/docs/api/effect/IEffectManager.md)：效果管理器（全能易用门面）
> - [IEffectMainManager](/docs/api/effect/IEffectMainManager.md)：效果主管理器，提供子管理器及热插拔
- [IEffectSubManager](/docs/api/effect/IEffectSubManager.md)：效果子管理器（主/子管理器同构，即`IEffectManager`也是`IEffectSubManager`）
	- [IBoostManager](/docs/api/effect/type/IBoostManager.md)：能量管理器（速度、回血效果）
	- [IFireworkManager](/docs/api/effect/type/IFireworkManager.md)：烟花管理器（持续生成烟花）
	- [IMutekiManager](/docs/api/effect/type/IMutekiManager.md)：无敌管理器（取消伤害）
	- [IParticleManager](/docs/api/effect/type/IParticleManager.md)：粒子管理器（持续生成粒子）

### 游戏
> ./api/game

- [IGameManager](/docs/api/game/IGameManager.md)：游戏管理器（全能易用门面）
	> - [IGameMainManager](/docs/api/game/IGameMainManager.md)：主游戏管理器，提供子管理器及热插拔
	> - [IGameApiGetter](/docs/api/game/IGameApiGetter.md)：获取游戏ID（UUID）读写API、游戏内容只读API
	> - [IGameStatusSetter](/docs/api/game/IGameStatusSetter.md)：游戏状态设置接口
- [IGameSubManager](/docs/api/game/IGameSubManager.md)：游戏子管理器（主/子管理器同构，即`IGameManager`也是`IGameSubManager`）
	- Gamerule：游戏规则
		- [IGameruleManager](/docs/api/game/gamerule/IGameruleManager.md)：游戏规则管理器
	- Lobby：大厅
		- [IGameLobbyManager](/docs/api/game/lobby/IGameLobbyManager.md)：游戏大厅管理器
	- Loot：物资
		- [IGameLootManager](/docs/api/game/loot/IGameLootManager.md)：游戏物资刷新管理器
	- Process：进程
		- [IGameProcessManager](/docs/api/game/process/IGameProcessManager.md)：游戏进程管理器
	- Spawn：出生
		- [ISpawnManager](/docs/api/game/spawn/ISpawnManager.md)：出生管理器
	- Stats：统计数据
		- [IStatsManager](/docs/api/game/stats/IStatsManager.md)：统计管理器
	- Team：队伍
		- [ITeamManager](/docs/api/game/team/ITeamManager.md)：队伍管理器
	- Zone：区域
		- [IZoneManager](/docs/api/game/zone/IZoneManager.md)：区域管理器

### 物资
> ./api/loot

- [ILootObject](/docs/api/loot/ILootObject.md)：物资对象，可刷新物资和读写游戏ID（UUID）

## 模组联动

- JourneyMap：
	- [IJmApi](/docs/api/compat/journeymap/IJmApi.md)：JourneyMap API
- TaCZ：
	- [ITaczEventRegister](/docs/api/compat/tacz/ITaczEventRegister.md)：TaCZ 事件注册（倒地禁枪）

## 配置

- [IConfigManager](/docs/api/config/IConfigManager.md)：主配置管理器（仅用于分类），包含若干子配置管理器
- [IConfigSubManager](/docs/api/config/IConfigSubManager.md)：子配置管理器
- [IManagerName](/docs/api/config/IManagerName.md)：配置管理器NameKey，与CommandArg（_xiao.battleroyale.common.command.CommandArg.java_）中配置指令对应的字符串一致
- [IModConfigManager](/docs/api/config/IModConfigManager.md)：模组配置管理器（全能易用门面），包含若干主/子配置管理器
	- [IConfigEntry](/docs/api/config/sub/IConfigEntry.md)：（全配置类型）基本词条功能
	- [IConfigSingleEntry](/docs/api/config/sub/IConfigSingleEntry.md)：单个完整词条

- Client：客户端配置
	- Display：显示配置词条
	- Render：渲染配置词条
- Common：双端配置
	- Effect：效果配置词条
		- Particle：粒子配置词条
	- Game：游戏配置词条
		- Bot：人机配置词条
		- Gamerule：游戏规则配置词条
		- Spawn：出生配置词条
		- Zone：区域配置词条
	- Loot：物资刷新配置词条
	- Server：服务端配置词条
		- Performance：性能配置词条
		- Utility：实用配置词条

## 数据

- DevDataTag：开发者工具数据标签
- TempDataTag：临时数据标签

## 事件

- [EventPriority](/docs/api/event/EventPriority.md)：事件优先级，同 Forge/NeoForge

事件处理器接口：
- [ICustomEventHandler](/docs/api/event/ICustomEventHandler.md)：自定义事件处理器接口，供扩展模组用
- [IEventHandler](/docs/api/event/IEventHandler.md)：模组事件处理器接口

监听事件：
- [ICustomEventRegister](/docs/api/event/ICustomEventRegister.md)：注册监听自定义事件
- [IEventRegister](/docs/api/event/IEventRegister.md)：注册监听模组事件

发布事件：
- [ICustomEventPoster](/docs/api/event/ICustomEventPoster.md)：自定义事件发布器

### 模组事件
> ./api/event

- [EventType](/docs/api/event/EventType.md)：模组事件类型
- [IEvent](/docs/api/event/IEvent.md)：模组事件接口
> - ILivingDamageEvent：Forge/NeoForge 生物受击事件
> - ILivingDeathEvent：Forge/NeoForge 生物死亡事件
> - IPlayerLoggedInEvent：玩家登录事件
> - IPlayerLoggedOutEvent：玩家登出事件
> - IServerTickEvent：服务端 tick 事件

### 自定义事件
> ./api/event

- [CustomEventType](/docs/api/event/CustomEventType.md)：自定义事件类型
- [ICustomEvent](/docs/api/event/ICustomEvent.md)：自定义事件接口

#### 自定义客户端事件
> ./api/event/client

- Render：渲染
	- [SpecialZoneRenderEvent](/docs/api/event/client/render/SpecialZoneRenderEvent.md)：客户端特殊渲染事件

#### 自定义游戏事件
> ./api/event/game

- [AbstractGameEvent](/docs/api/event/game/AbstractGameEvent.md)：游戏事件，默认可取消
- [AbstractGameStatsEvent](/docs/api/event/game/AbstractGameStatsEvent.md)：游戏记录事件，不可取消
- Finish：结束事件
	- [GameCompleteEvent](/docs/api/event/game/finish/GameCompleteEvent.md)：游戏正常完成事件
    > - GameCompleteFinishEvent
	- [GameStopEvent](/docs/api/event/game/finish/GameStopEvent.md)：游戏停止事件，由指令强制执行或游戏正常完成触发
	> - GameStopFinishEvent
	- [ServerStopEvent](/docs/api/event/game/finish/ServerStopEvent.md)：服务器关闭事件
	> ServerStopFinishEvent
- Game：游戏进程
	- [GamePlayerDeathEvent](/docs/api/event/game/game/GamePlayerDeathEvent.md)：游戏玩家死亡事件
	> - GamePlayerDeathFinishEvent
	- [GamePlayerDownEvent](/docs/api/event/game/game/GamePlayerDownEvent.md)：游戏玩家倒地事件
	> - GamePlayerDownFinishEvent
	- [GamePlayerReviveEvent](/docs/api/event/game/game/GamePlayerReviveEvent.md)：游戏玩家扶起事件
	> - GamePlayerReviveFinishEvent
	- [GameSpectateEvent](/docs/api/event/game/game/GameSpectateEvent.md)：玩家观战事件，取消则阻止观战
- Spawn：出生
	- [GameLobbyTeleportEvent](/docs/api/event/game/spawn/GameLobbyTeleportEvent.md)：游戏大厅传送事件，取消则阻止传送
	> - GameLobbyTeleportFinishEvent
- Starter：游戏启动流程
	- [GameLoadEvent](/docs/api/event/game/starter/GameLoadEvent.md)：[读取配置](/docs/wiki/Command/Game%20Command.md#读取配置)
	> - GameLoadFinishEvent
	- [GameInitEvent](/docs/api/event/game/starter/GameInitEvent.md)：[初始化游戏](/docs/wiki/Command/Game%20Command.md#初始化游戏)
	> - GameInitFinishEvent
	- [GameStartEvent](/docs/api/event/game/starter/GameStartEvent.md)：[开始游戏](/docs/wiki/Command/Game%20Command.md#开始游戏)
	> - GameStartFinishEvent
- Team：队伍
	- [InvitePlayerEvent](/docs/api/event/game/team/InvitePlayerEvent.md)：邀请玩家加入队伍
	> - InvitePlayerCompleteEvent
	- [RequestPlayerEvent](/docs/api/event/game/team/RequestPlayerEvent.md)：玩家请求加入队伍
	> - RequestPlayerCompleteEvent
- Tick：更新
	- [GameLootBfsEvent](/docs/api/event/game/tick/GameLootBfsEvent.md)：物资刷新BFS事件
	> - GameLootBfsFinishEvent
	- [GameLootEvent](/docs/api/event/game/tick/GameLootEvent.md)：物资刷新事件
	> - GameLootFinishEvent
	- [GameTickEvent](/docs/api/event/game/tick/GameTickEvent.md)：游戏管理器 Tick 事件，先于游戏进程管理器对子管理器的调度
	> - GameTickFinishEvent
	- [ZoneTickEvent](/docs/api/event/game/tick/ZoneTickEvent.md)：区域管理器 Tick 事件
	> - ZoneTickFinishEvent
- Zone：区域
	- [AirdropEvent](/docs/api/event/game/zone/AirdropEvent.md)：补给箱事件，由特殊功能词条[补给箱区](/docs/wiki/Configuration/Zone%20special%20function.md#补给箱区)发送
	- [CustomZoneEvent](/docs/api/event/game/zone/CustomZoneEvent.md)：区域通用事件，由特殊功能词条[通用事件区](/docs/wiki/Configuration/Zone%20special%20function.md#通用事件区)发送
	- [EntityEvent](/docs/api/event/game/zone/EntityEvent.md)：实体刷新事件，由特殊功能词条[实体刷新区](/docs/wiki/Configuration/Zone%20special%20function.md#实体刷新区)发送
	- [ZoneCompleteEvent](/docs/api/event/game/zone/ZoneCompleteEvent.md)：区域完成事件，在区域 Tick 时触发
	- [ZoneCreatedEvent](/docs/api/event/game/zone/ZoneCreatedEvent.md)：区域创建事件

#### 自定义物资事件
> ./api/event/loot

- Generate：物资刷新
	- CustomGenerateEvent：物资刷新通用事件，由[通用事件刷新](/docs/wiki/Configuration/General%20loot%20config.md#通用事件刷新)词条发送

## 初始化

- Registry：平台无关的注册
	- IMenuTypeFactory：封装网络菜单创建逻辑
	- IRegistrar：注册对象集合接口
	- IRegistrarFactory：创建平台无关的注册对象集合
	- IRegistryObject：平台无关的注册对象引用接口
- ICommandRegistry：注册指令，让平台调用
- ICommonSetup：模组启动后的行为，让平台调用
- ICompatInit：联动模组初始化，让平台调用
- IModEvent：模组事件，让平台调用

## 我的世界

- [IMcRegistry](/docs/api/minecraft/IMcRegistry.md)：`ResourceLocation` 操作，查询模组加载

## 网络

- Message：网络消息
	- [IMessageManager](/docs/api/network/message/IMessageManager.md)：网络消息管理器
	> - Game：游戏
	> 	- [GameTag](/docs/api/network/message/game/GameTag.md)：游戏消息标签
	> - Team：队伍
	> 	- [GameTeamTag](/docs/api/network/message/team/GameTeamTag.md)：游戏队伍消息标签
	> - Zone：区域
	> 	- [GameZoneTag](/docs/api/network/message/zone/GameZoneTag.md)：游戏区域消息标签
- [INetworkAdapter](/docs/api/network/INetworkAdapter.md)：抽象由平台实现的注册网络消息、向玩家发送消息
- [INetworkHook](/docs/api/network/INetworkHook.md)：网络钩子，用于打开容器GUI

## 实用

- [ILobbyFuncApi](/docs/api/utility/ILobbyFuncApi.md)：大厅功能API，适用于游戏大厅和生存大厅
- [ILobbyReadApi](/docs/api/utility/ILobbyReadApi.md)：大厅只读API，适用于游戏大厅和生存大厅

# English

## Algorithm

- [IAlgorithmApi](/docs/api/algorithm/IAlgorithmApi.md): Algorithm almighty easy-to-use facade
	- [IDistribution](/docs/api/algorithm/IDistribution.md): Provides distribution algorithms

## Client

- Event:
	- [IRenderGUIEventPost](/docs/api/client/event/IRenderGuiEventPost.md): GUI render event
	- [IRenderLevelStageEvent](/docs/api/client/event/IRenderLevelStageEvent.md): World render (zone render)
- Game:
	- [IClientGameDataManager](/docs/api/client/game/IClientGameDataManager.md): Client game data management
	    - [IClientTeamDataManager](/docs/api/client/game/sub/IClientTeamDataManager.md): Client game team data management
	    - [IClientZoneDataManager](/docs/api/client/game/sub/IClientZoneDataManager.md): Client game zone data management
- Init:
	- [IClientSetup](/docs/api/client/init/IClientSetup.md): Client initialization
      - Register container (`BlockEntity`) GUI
	- [IModEntityRender](/docs/api/client/init/IModEntityRender.md): Register `BlockEntity` render
- Render:
	- [IBlockModelRenderer](/docs/api/client/render/IBlockModelRenderer.md): `BlockEntity` model renderer interface
		- Used for rendering `BlockEntity` itself, or custom rendering (rendering items in loot spawner _battleroyale:loot_spawner_ )
	- [IClientGuiRenderer](/docs/api/client/render/IClientGuiRenderer.md): Client GUI renderer interface
		- IClientGameInfoRenderer: Client game info display
        - IClientTeamInfoRenderer: Client team info display
	- [IClientLevelRenderer](/docs/api/client/render/IClientLevelRenderer.md): Client world renderer interface
		- IClientSimpleZoneRenderer: Simple zone renderer
		> - IClientSpectateRenderer: Spectate player (all game players) renderer
		> - IClientTeamRenderer: Team (teammate) renderer
		- IClientZoneRenderer: Zone renderer (includes parameter retrieval for circle/ellipsoid render segments)

## Common

- [ISideOnly](/docs/api/common/ISideOnly.md): Provides determination of whether running on the correct side

### Effect
> ./api/effect

- [IEffectManager](/docs/api/effect/IEffectManager.md): Effect manager (almighty easy-to-use facade)
> - [IEffectMainManager](/docs/api/effect/IEffectMainManager.md): Main effect manager, provides sub-managers and hot-swapping
- [IEffectSubManager](/docs/api/effect/IEffectSubManager.md): Effect sub-manager (isomorphic to main/sub manager, i.e., `IEffectManager` is also `IEffectSubManager`)
	- [IBoostManager](/docs/api/effect/type/IBoostManager.md): Boost manager (speed, regeneration effect)
	- [IFireworkManager](/docs/api/effect/type/IFireworkManager.md): Firework manager (continuous generate firework)
	- [IMutekiManager](/docs/api/effect/type/IMutekiManager.md): Muteki (Invincible) manager (cancel damage)
	- [IParticleManager](/docs/api/effect/type/IParticleManager.md): Particle manager (continuous generate particle)

### Game
> ./api/game

- [IGameManager](/docs/api/game/IGameManager.md): Game manager (almighty easy-to-use facade)
	> - [IGameMainManager](/docs/api/game/IGameMainManager.md): Main game manager, provides sub-managers and hot-swapping
	> - [IGameApiGetter](/docs/api/game/IGameApiGetter.md): Get Game ID (UUID) read/write API, game content read-only API
	> - [IGameStatusSetter](/docs/api/game/IGameStatusSetter.md): Game status setter interface
- [IGameSubManager](/docs/api/game/IGameSubManager.md): Game sub-manager (isomorphic to main/sub manager, i.e., `IGameManager` is also `IGameSubManager`)
	- Gamerule:
		- [IGameruleManager](/docs/api/game/gamerule/IGameruleManager.md): Game rule manager
	- Lobby:
		- [IGameLobbyManager](/docs/api/game/lobby/IGameLobbyManager.md): Game lobby manager
	- Loot:
		- [IGameLootManager](/docs/api/game/loot/IGameLootManager.md): Game loot generation manager
	- Process:
		- [IGameProcessManager](/docs/api/game/process/IGameProcessManager.md): Game process manager
	- Spawn:
		- [ISpawnManager](/docs/api/game/spawn/ISpawnManager.md): Spawn manager
	- Stats:
		- [IStatsManager](/docs/api/game/stats/IStatsManager.md): Statistics manager
	- Team:
		- [ITeamManager](/docs/api/game/team/ITeamManager.md): Team manager
	- Zone:
		- [IZoneManager](/docs/api/game/zone/IZoneManager.md): Zone manager

### Loot
> ./api/loot

- [ILootObject](/docs/api/loot/ILootObject.md): Loot object, can generate loot and read/write Game ID (UUID)

## Mod Compat

- JourneyMap:
	- [IJmApi](/docs/api/compat/journeymap/IJmApi.md): JourneyMap API
- TaCZ:
	- [ITaczEventRegister](/docs/api/compat/tacz/ITaczEventRegister.md): TaCZ event register (disable gun when downed)

## Config

- [IConfigManager](/docs/api/config/IConfigManager.md): Main config manager (only for classification), contains several sub-config managers
- [IConfigSubManager](/docs/api/config/IConfigSubManager.md): Sub-config manager
- [IManagerName](/docs/api/config/IManagerName.md): Config manager NameKey, consistent with the string corresponding to the config command in CommandArg (_xiao.battleroyale.common.command.CommandArg.java_)
- [IModConfigManager](/docs/api/config/IModConfigManager.md): Mod config manager (almighty easy-to-use facade), contains several main/sub config managers
	- [IConfigEntry](/docs/api/config/sub/IConfigEntry.md): (All config types) Basic entry function
	- [IConfigSingleEntry](/docs/api/config/sub/IConfigSingleEntry.md): Single complete entry

- Client: Client config
	- Display: Display config entry
	- Render: Render config entry
- Common: Two-side config
	- Effect: Effect config entry
		- Particle: Particle config entry
	- Game: Game config entry
		- Bot: Bot config entry
		- Gamerule: Game rule config entry
		- Spawn: Spawn config entry
		- Zone: Zone config entry
	- Loot: Loot generation config entry
	- Server: Server config entry
		- Performance: Performance config entry
		- Utility: Utility config entry

## Data

- DevDataTag: Developer tool data tag
- TempDataTag: Temporary data tag

## Event

- [EventPriority](/docs/api/event/EventPriority.md): Event priority, same as Forge/NeoForge

Event Handler Interface:
- [ICustomEventHandler](/docs/api/event/ICustomEventHandler.md): Custom event handler interface, for extension mods
- [IEventHandler](/docs/api/event/IEventHandler.md): Mod event handler interface

Listen Event:
- [ICustomEventRegister](/docs/api/event/ICustomEventRegister.md): Register to listen for custom events
- [IEventRegister](/docs/api/event/IEventRegister.md): Register to listen for mod events

Post Event:
- [ICustomEventPoster](/docs/api/event/ICustomEventPoster.md): Custom event poster

### Mod Event
> ./api/event

- [EventType](/docs/api/event/EventType.md): Mod event type
- [IEvent](/docs/api/event/IEvent.md): Mod event interface
> - ILivingDamageEvent: Forge/NeoForge living damage event
> - ILivingDeathEvent: Forge/NeoForge living death event
> - IPlayerLoggedInEvent: Player logged in event
> - IPlayerLoggedOutEvent: Player logged out event
> - IServerTickEvent: Server tick event

### Custom Event
> ./api/event

- [CustomEventType](/docs/api/event/CustomEventType.md): Custom event type
- [ICustomEvent](/docs/api/event/ICustomEvent.md): Custom event interface

#### Custom Client Event
> ./api/event/client

- Render: Render
	- [SpecialZoneRenderEvent](/docs/api/event/client/render/SpecialZoneRenderEvent.md): Client special zone render event

#### Custom Game Event
> ./api/event/game

- [AbstractGameEvent](/docs/api/event/game/AbstractGameEvent.md): Game event, cancellable by default
- [AbstractGameStatsEvent](/docs/api/event/game/AbstractGameStatsEvent.md): Game stats event, not cancellable
- Finish: Finish event
	- [GameCompleteEvent](/docs/api/event/game/finish/GameCompleteEvent.md): Game normal completion event
    > - GameCompleteFinishEvent
	- [GameStopEvent](/docs/api/event/game/finish/GameStopEvent.md): Game stop event, triggered by command force execution or game normal completion
	> - GameStopFinishEvent
	- [ServerStopEvent](/docs/api/event/game/finish/ServerStopEvent.md): Server stop event
	> ServerStopFinishEvent
- Game: Game process
	- [GamePlayerDeathEvent](/docs/api/event/game/game/GamePlayerDeathEvent.md): Game player death event
	> - GamePlayerDeathFinishEvent
	- [GamePlayerDownEvent](/docs/api/event/game/game/GamePlayerDownEvent.md): Game player down event
	> - GamePlayerDownFinishEvent
	- [GamePlayerReviveEvent](/docs/api/event/game/game/GamePlayerReviveEvent.md): Game player revive event
	> - GamePlayerReviveFinishEvent
	- [GameSpectateEvent](/docs/api/event/game/game/GameSpectateEvent.md): Player spectate event, cancel to prevent spectating
- Spawn:
	- [GameLobbyTeleportEvent](/docs/api/event/game/spawn/GameLobbyTeleportEvent.md): Game lobby teleport event, cancel to prevent teleportation
	> - GameLobbyTeleportFinishEvent
- Starter: Game startup process
	- [GameLoadEvent](/docs/api/event/game/starter/GameLoadEvent.md): [Read configuration](/docs/wiki/Command/Game%20command.md#Read%20configuration)
	> - GameLoadFinishEvent
	- [GameInitEvent](/docs/api/event/game/starter/GameInitEvent.md): [Initialize the game](/docs/wiki/Command/Game%20command.md#Initialize%20the%20game)
	> - GameInitFinishEvent
	- [GameStartEvent](/docs/api/event/game/starter/GameStartEvent.md): [Start game](/docs/wiki/Command/Game%20command.md#Start%20game)
	> - GameStartFinishEvent
- Team:
	- [InvitePlayerEvent](/docs/api/event/game/team/InvitePlayerEvent.md): Invite player to join team
	> - InvitePlayerCompleteEvent
	- [RequestPlayerEvent](/docs/api/event/game/team/RequestPlayerEvent.md): Player requests to join team
	> - RequestPlayerCompleteEvent
- Tick: Update
	- [GameLootBfsEvent](/docs/api/event/game/tick/GameLootBfsEvent.md): Loot generation BFS event
	> - GameLootBfsFinishEvent
	- [GameLootEvent](/docs/api/event/game/tick/GameLootEvent.md): Loot generation event
	> - GameLootFinishEvent
	- [GameTickEvent](/docs/api/event/game/tick/GameTickEvent.md): Game manager tick event, prior to game process manager scheduling sub-managers
	> - GameTickFinishEvent
	- [ZoneTickEvent](/docs/api/event/game/tick/ZoneTickEvent.md): Zone manager tick event
	> - ZoneTickFinishEvent
- Zone
	- [AirdropEvent](/docs/api/event/game/zone/AirdropEvent.md): Airdrop event, sent by special function entry [Airdrop Zone](/docs/wiki/Configuration/Zone%20special%20function.md#Airdrop%20zone)
	- [CustomZoneEvent](/docs/api/event/game/zone/CustomZoneEvent.md): Zone common event, sent by special function entry [Common event zone](/docs/wiki/Configuration/Zone%20special%20function.md#Common%20event%20zone)
	- [EntityEvent](/docs/api/event/game/zone/EntityEvent.md): Entity generation event, sent by special function entry [Entity loot zone](/docs/wiki/Configuration/Zone%20special%20function.md#Entity%20loot%20zone)
	- [ZoneCompleteEvent](/docs/api/event/game/zone/ZoneCompleteEvent.md): Zone complete event, triggered during zone tick
	- [ZoneCreatedEvent](/docs/api/event/game/zone/ZoneCreatedEvent.md): Zone created event

#### Custom Loot Event
> ./api/event/loot

- Generate: Loot generation
	- CustomGenerateEvent: Loot generation common event, sent by [Common event loot](/docs/wiki/Configuration/General%20loot%20config.md#Common%20event%20loot) entry

## Init

- Registry: Platform-independent registration
	- IMenuTypeFactory: Encapsulate network menu creation logic
	- IRegistrar: Registration object collection interface
	- IRegistrarFactory: Create platform-independent registration object collection
	- IRegistryObject: Platform-independent registration object reference interface
- ICommandRegistry: Register commands, called by platform
- ICommonSetup: Behavior after mod startup, called by platform
- ICompatInit: Compat mod initialization, called by platform
- IModEvent: Mod event, called by platform

## Minecraft

- [IMcRegistry](/docs/api/minecraft/IMcRegistry.md): `ResourceLocation` operation, query mod loading

## Network

- Message: Network message
	- [IMessageManager](/docs/api/network/message/IMessageManager.md): Network message manager
	> - Game:
	> 	- [GameTag](/docs/api/network/message/game/GameTag.md): Game message tag
	> - Team:
	> 	- [GameTeamTag](/docs/api/network/message/team/GameTeamTag.md): Game team message tag
	> - Zone:
	> 	- [GameZoneTag](/docs/api/network/message/zone/GameZoneTag.md): Game zone message tag
- [INetworkAdapter](/docs/api/network/INetworkAdapter.md): Abstract registration of network messages and sending messages to players implemented by platform
- [INetworkHook](/docs/api/network/INetworkHook.md): Network hook, used to open container GUI

## Utility

- [ILobbyFuncApi](/docs/api/utility/ILobbyFuncApi.md): Lobby function API, applicable to game lobby and survival lobby
- [ILobbyReadApi](/docs/api/utility/ILobbyReadApi.md): Lobby read-only API, applicable to game lobby and survival lobby