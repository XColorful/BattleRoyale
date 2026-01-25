[English](#English)

# 架构总览

> 本文档作为项目架构的导航索引

## 设计哲学与准则

核心架构思想与开发准则

- [设计哲学](./design/design-philosophy.md)
- [开发准则](./design/development-principles.md)

## 项目结构

基于`xiao.battleroyale`顶层包的模块划分

### 算法
> _./algorithm_

- [算法门面](./algorithm/algorithm-facade.md)：算法接口，供模组外部调用
  - [分布算法](./algorithm/distribution.md)：黄金螺旋分布、双圆心网格分布、网格采样

### API
> _./api_

- [API索引](./api/api-index.md)：通过接口分类和职责介绍进行筛选，并传送至 _./docs/api_ 下内容以查看详细参数

### 方块
> _./block_

- 普通方块：一般路过的草方块
  - 无
- 方块实体：有功能的不一般方块
  - [刷新方块](./block/loot-block.md)：物资刷新方块、实体生成方块
  - [配置方块](./block/config-block.md)：区域控制器

### 客户端
> _./client_

- 客户端事件：
	- [客户端游戏事件处理器](./client/event/client-game-event-handler.md)
	- [客户端渲染事件处理器](./client/event/client-render-event-handler.md)
- 游戏相关：
	- [客户端游戏数据管理器](./client/game/client-game-data-manager.md)
		- [客户端游戏信息数据](./client/game/data/client-game-data.md)
		- [客户端单个区域数据](./client/game/data/client-single-zone-data.md)
		- [客户端队伍数据](./client/game/data/client-team-data.md)
- GUI界面：
	- [物资刷新方块GUI](./client/gui/loot-spawner-gui.md)
- 客户端初始化：
	- [客户端初始化](./client/init/client-setup.md)：GUI注册
	- [模组实体渲染](./client/init/mod-entity-render.md)：注册方块实体`BlockEntity`渲染器
- 渲染：
	- [方块模型渲染器](./client/renderer/block-model-renderer.md)：渲染方块实体`BlockEntity`
		- [实体刷新方块渲染器](./client/renderer/block/entity-spawner-renderer.md)
		- [物资刷新方块渲染器](./client/renderer/block/loot-spawner-renderer.md)
    - [客户端GUI渲染器](./client/renderer/client-gui-renderer.md)
	    - [游戏信息渲染器](./client/renderer/gui/game-info-renderer.md)
	    - [队伍信息渲染器](./client/renderer/gui/team-info-renderer.md)
	- [客户端世界渲染器](./client/renderer/client-level-renderer.md)
		- [二维区域形状](./client/renderer/level/shape-2d.md)
        - [三维区域形状](./client/renderer/level/shape-3d.md)
        - [观战玩家渲染器](./client/renderer/level/spectate-player-renderer.md)
        - [队伍成员渲染](./client/renderer/level/team-member-renderer.md)
        - [区域渲染](./client/renderer/level/zone-renderer.md)
	- [自定义渲染类型](./client/renderer/custom-render-type.md)：注册半透明、透明渲染方式

### 指令
> _./command_

- [客户端指令](./command/client-command.md)：
- [指令参数名列表](./command/command-arg.md)：同时作为ConfigManager和ConfigSubManager必须使用的NameKey
- [服务端指令](./command/server-command.md)：
- 命令：
	- BackupCommand：[备份指令](/docs/wiki/Command/Backup%20command.md)
	- BoostCommand：[能量指令](/docs/wiki/Command/Boost%20command.md)
	- ConfigCommand：[配置指令](/docs/wiki/Command/Config%20command.md)
	- ExampleCommand：[生成配置](/docs/wiki/Command/Example%20command.md)
	- FireworkCommand：[烟花指令](/docs/wiki/Command/Firework%20command.md)
	- GameCommand：[大逃杀指令](/docs/wiki/Command/Game%20command.md)
	- LootCommand：[手动刷新](/docs/wiki/Command/Loot%20command.md)
	- MutekiCommand：[无敌指令](/docs/wiki/Command/Muteki%20command.md)
	- ParticleCommand：[粒子指令](/docs/wiki/Command/Particle%20command.md)
	- ReloadCommand：[重载配置](/docs/wiki/Command/Reload%20command.md)
	- SaveCommand：[保存配置](/docs/wiki/Command/Save%20command.md)
	- TeamCommand：[队伍管理](/docs/wiki/Command/Team%20command.md)
	- TempCommand：[临时数据](/docs/wiki/Command/Temp%20data%20command.md)
	- UtilityCommand：[实用功能](/docs/wiki/Command/Utility%20command.md)

### 核心机制
> _./common_

### 效果
> _./common/effect_

- [EffectManager](./common/effect/effect-manager.md)：效果管理器（全能易用门面）
	- [BoostManager](./common/effect/boost/boost-manager.md)：能量管理器
	- [FireworkManager](./common/effect/firework/firework-manager.md)：烟花管理器
	- [MutekiManager](./common/effect/muteki/muteki-manager.md)：无敌管理器
	- [ParticleManager](./common/effect/particle/particle-manager.md)：粒子管理器

### 游戏
> _./common/game_

- [GameManager](./common/game/game-manager.md)：游戏管理器（全能易用门面）
> - [GameIdHelper](./common/game/game-id-helper.md)：游戏ID读写
> - [GameUtilsFunction](./common/game/game-utils-function.md)：游戏实用功能，无摔传送
- Gamerule：游戏规则
	- [GameruleManager](./common/game/gamerule/gamerule-manager.md)：游戏规则管理器
- Lobby：大厅
	- [GameLobbyManager](./common/game/lobby/game-lobby-manager.md)：游戏大厅管理器
- Loot：物资
	- [BfsCalculator](./common/game/loot/bfs-calculator.md)：BFS预计算器
	- [GameLootManager](./common/game/loot/game-loot-manager.md)：游戏物资刷新管理器
- Process：游戏进程
	- BattleRoyale：大逃杀游戏进程
		- [BRGameProcessManager](./common/game/process/battleroyale/br-game-process-manager.md)：游戏进程管理器（大逃杀游戏）
- Spawn：出生
	- [SpawnManager](./common/game/spawn/spawn-manager.md)：出生管理器
	- Vanilla：原版出生方式
		- [TeleportSpawner](./common/game/spawn/vanilla/teleport-spawner.md)：传送
- Stats：统计数据
	- [StatsManager](./common/game/stats/stats-manager.md)：统计管理器
- Team：队伍
	- [GamePlayer](./common/game/team/game-player.md)：游戏玩家类
	- [GameTeam](./common/game/team/game-team.md)：游戏队伍类
	- [TeamManager](./common/game/team/team-manager.md)：队伍管理器
	- [TeamData](./common/game/team/team-data.md)：存放游戏队伍数据，内部封装复杂数据维护
- Zone：区域
	- [GameZone](./common/game/zone/game-zone.md)：游戏区域类
	- [ZoneData](./common/game/zone/zone-data.md)：存放游戏区域数据，内部封装便利功能
	- [ZoneManager](./common/game/zone/zone-manager.md)：区域管理器
	- Additional：区域额外数据
		- [AdditionalRender](./common/game/zone/additional/additional-render.md)：收发客户端额外渲染的网络消息并发送客户端事件
	- Spatial：区域范围判定
		- 二维判定形状：
	    > - [CircleShape](./common/game/zone/spatial/circle-shape.md)：[圆形](/docs/wiki/Configuration/Zone%202D%20shape.md#圆形)
	    > - [EllipseShape](./common/game/zone/spatial/ellipse-shape.md)：[椭圆](/docs/wiki/Configuration/Zone%202D%20shape.md#椭圆)
	    > - [HexagonShape](./common/game/zone/spatial/hexagon-shape.md)：[平顶正六边形](/docs/wiki/Configuration/Zone%202D%20shape.md#平顶正六边形)
	    > - [PolygonShape](./common/game/zone/spatial/polygon-shape.md)：[尖顶正多边形](/docs/wiki/Configuration/Zone%202D%20shape.md#尖顶正多边形)
        > - [RectangleShape](./common/game/zone/spatial/rectangle-shape.md)：[长方形](/docs/wiki/Configuration/Zone%202D%20shape.md#矩形)
        > - [SquareShape](./common/game/zone/spatial/square-shape.md)：[方形](/docs/wiki/Configuration/Zone%202D%20shape.md#方形)
        > - [StarShape](./common/game/zone/spatial/star-shape.md)：[星形](/docs/wiki/Configuration/Zone%202D%20shape.md#星形)
		- 三维判定形状：
        > - [CubeShape](./common/game/zone/spatial/cube-shape.md)：[正方体](/docs/wiki/Configuration/Zone%203D%20shape.md#正方体)
        > - [CuboidShape](./common/game/zone/spatial/cuboid-shape.md)：[长方体](/docs/wiki/Configuration/Zone%203D%20shape.md#长方体)
        > - [EllipsoidShape](./common/game/zone/spatial/ellipsoid-shape.md)：[椭球](/docs/wiki/Configuration/Zone%203D%20shape.md#椭球)
        > - [SphereShape](./common/game/zone/spatial/sphere-shape.md)：[球](/docs/wiki/Configuration/Zone%203D%20shape.md#球)
	- Tickable：区域功能 Tick
		- 简单功能：
        > - [BoostFunc](./common/game/zone/tickable/boost-func.md)：[能量区](/docs/wiki/Configuration/Zone%20simple%20function.md#能量区)
        > - [EffectFunc](./common/game/zone/tickable/effect-func.md)：[效果区](/docs/wiki/Configuration/Zone%20simple%20function.md#效果区)
        > - [FireworkFunc](./common/game/zone/tickable/firework-func.md)：[烟花区](/docs/wiki/Configuration/Zone%20simple%20function.md#烟花区)
        > - [InventoryFunc](./common/game/zone/tickable/inventory-func.md)：[背包区](/docs/wiki/Configuration/Zone%20simple%20function.md#背包区)
        > - [MessageFunc](./common/game/zone/tickable/message-func.md)：[消息区](/docs/wiki/Configuration/Zone%20simple%20function.md#消息区)
        > - [MutekiFunc](./common/game/zone/tickable/muteki-func.md)：[无敌区](/docs/wiki/Configuration/Zone%20simple%20function.md#无敌区)
        > - [NoFunc](./common/game/zone/tickable/no-func.md)：[无功能区](/docs/wiki/Configuration/Zone%20simple%20function.md#无功能区)
        > - [ParticleFunc](./common/game/zone/tickable/particle-func.md)：[粒子区](/docs/wiki/Configuration/Zone%20simple%20function.md#粒子区)
        > - [SafeFunc](./common/game/zone/tickable/safe-func.md)：[安全区](/docs/wiki/Configuration/Zone%20simple%20function.md#安全区)
        > - [UnsafeFunc](./common/game/zone/tickable/unsafe-func.md)：[不安全区](/docs/wiki/Configuration/Zone%20simple%20function.md#不安全区)
		- 特殊功能：
	        - 事件区：
	        > - [AirdropEvent](./common/game/zone/tickable/event/airdrop-func.md)：[补给箱区](/docs/wiki/Configuration/Zone%20special%20function.md#补给箱区)
	        > - [EntityFunc](./common/game/zone/tickable/event/entity-func.md)：[实体刷新区](/docs/wiki/Configuration/Zone%20special%20function.md#实体刷新区)
	        > - [EventFunc](./common/game/zone/tickable/event/event-func.md)：[通用事件区](/docs/wiki/Configuration/Zone%20special%20function.md#通用事件区)

### 物资
> _./common/loot_

- [CommonLootManager](./common/loot/common-loot-manager.md)：通用物资刷新管理器
- [InventoryGenerator](./common/loot/inventory-generator.md)：背包物资刷新机制
- [LootGenerator](./common/loot/loot-generator.md)：物资刷新机制

### 网络消息
> _./common/message_

- [MessageManager](./common/message/message-manager.md)：网络消息管理器（全能易用门面）
- Game：游戏
	- [GameInfoMessageManager](./common/message/game/game-info-message-manager.md)：游戏信息消息管理器
	> GameInfoMessage：游戏信息消息
	- [SpectateMessageManager](./common/message/game/spectate-message-manager.md)：观战消息管理器
	> - SpectateMessage：观战消息
- Team：队伍
	- [TeamMessageManager](./common/message/team/team-message-manager.md)：队伍消息管理器
	> - TeamMessage：队伍成员消息
- Zone：区域
	- [ZoneMessageManager](./common/message/zone/zone-message-manager.md)：区域消息管理器
	> - ZoneMessage：区域消息

### 服务端
> _./common/server_

- Performance：性能
	- [PerformanceManager](./common/server/performance/performance-manager.md)：性能管理器
- Utility：实用
	- [SurvivalLobby](./common/server/utility/survival-lobby.md)：生存大厅
	- UtilityManager：实用功能管理器

### 模组联动
> _./compat_

[模组联动](./compat/compat-index.md)：
- [自定义大逃杀扩展](./compat/cbraddon/cbr-addon.md)
- [旅行地图](./compat/journeymap/journeymap.md)
- [玩家救援](./compat/playerrevive/player-revive.md)
- [PUBGMC](./compat/pubgmc/pubgmc.md)
- [永恒枪械工坊：零](./compat/tacz/tacz.md)

### 配置
> _./config_

[模组配置](./config/mod-config.md)：
> - [FolderConfigData](./config/folder-config-data.md)：配置文件夹数据，存放单个文件夹的配置
- Client：客户端配置
  - [显示配置管理器](./config/client/display/display-config-manager.md)：管理[显示配置](/docs/wiki/Configuration/Display%20config.md)
  - [渲染配置管理器](./config/client/render/render-config-manager.md)：管理[渲染配置](/docs/wiki/Configuration/Render%20config.md)
- Common：双端配置
  - [效果配置管理器](./config/common/effect/effect-config-manager)：
    - [粒子配置管理器](./config/common/effect/particle/particle-config-manager.md)：管理[粒子配置](/docs/wiki/Configuration/Particle%20config.md)
  - [游戏配置管理器](./config/common/game/game-config-manager.md)：
    - [游戏规则配置管理器](./config/common/game/gamerule/gamerule-config-manager.md)：管理[游戏规则配置](/docs/wiki/Configuration/Gamerule%20config.md)
    > - 生成游戏规则配置：
    > 	- CFHC游戏规则
    > 	- PUBG游戏规则
    - [出生配置管理器](./config/common/game/spawn/spawn-config-manager.md)：管理[出生配置](/docs/wiki/Configuration/Spawn%20config.md)
    > - 生成出生配置：
    > 	- CFHC出生配置
    > 	- PUBG区域出生配置（传送）
    > - 出生配置类型
    - [区域配置管理器](./config/common/game/zone/zone-config-manager.md)：管理[区域配置](/docs/wiki/Configuration/Zone%20config.md)
    > - 自定义区域配置：
    > 	- [特殊渲染协议](./config/common/game/zone/custom/special-render-protocol.md)：[方向渲染](/docs/wiki/Configuration/Zone%20special%20client.md#方向渲染)
    > - 生成区域配置：
    > 	- CFHC区域配置
    > 	- 鞘翅附加区
    > 	- 模组封面
    > 	- PUBG区域
    > 	- UHC区域
    > - 区域配置类型
  - [物资刷新配置管理器](./config/common/loot/loot-config-manager.md)：管理[通用刷新配置](/docs/wiki/Configuration/General%20loot%20config.md)
	  - ~~airdrop：补给箱配置~~
	  - ~~airdrop_special：特殊补给箱~~
	  - entity_spawner：实体生成器配置
	  - loot_spawner：物资刷新器配置
	  - ~~secret_room~~（暂未使用）
  > - 生成物资刷新配置：
  > 	- CBRG（TaCZ枪包）物资刷新配置
  > 	- CFHC物资刷新配置
  > 	- 马载具物资刷新配置
  > 	- TaCZ物资刷新配置
  > - 物资刷新配置类型
  - [服务端配置管理器](./config/common/server/server-config-manager.md)：
	  - [性能配置管理器](./config/common/server/performance/performance-config-manager.md)：管理[性能配置](/docs/wiki/Configuration/Performance%20config.md)
      > - 生成性能配置：
      > 	- CFHC性能配置
	  - [实用配置管理器](./config/common/server/utility/utility-config-manager.md)：管理[实用配置](/docs/wiki/Configuration/Utility%20config.md)

### 模组数据
> _./data_

[数据管理器](./data/data-manager.md)
- IO：可读写数据
	- [开发者工具数据](./data/io/dev-data.md)
	- 游戏管理员数据
	- [临时数据](./data/io/temp-data.md)

### 开发者相关

- 调试：
	- 指令：
	> - [调试指令](./developer/debug/command/debug-command.md)
	> - [本地调试指令](./developer/debug/command/local-debug-command.md)
	- 调试显示文本
- 游戏管理员：
	- 指令：
	> - [游戏管理员指令](./developer/gm/command/gm-command.md)

### 事件
> _./event_

- 内置自定义扩展事件处理器：_./event/custom_
	- 客户端：
		- [SpecialRenderHandler](./event/custom/client/special-render-handler.md)：客户端特殊渲染处理器
- [内置事件处理器索引](./event/handler/handler-index.md)：_./event/handler_
	- Effect：效果
	- Game：游戏
	- Loot：物资
	- Message：网络消息
	- Server：服务端
	- Util：实用
- [事件处理器框架](./event/event-handler.md)：_./event/AbstractEventHandler_
- [事件分发器](./event/event-poster.md)
- [事件注册器](./event/event-register.md)

### 初始化
> _./init_

- Registry：模组注册表
	- ModBlocks：模组方块
	- ModCreativeTabs：创造模式栏
	- ModDamageTypes：模组伤害类型（[安全区](/docs/wiki/Configuration/Zone%20simple%20function.md#安全区)/[不安全区](/docs/wiki/Configuration/Zone%20simple%20function.md#不安全区)伤害）
	- ~~ModEntities：模组实体~~（暂未使用）
	- ModItems：模组物品
	- ModMenuTypes：模组 GUI 类型（物资刷新方块 GUI 界面）
	- ModSounds：
- CommandRegistry：指令注册
- CommonSetup：双端初始化
- CompatInit：联动模组初始化
- ModEvent：模组事件

### 背包
> _./inventory_

- 物资刷新方块 GUI 界面

### 网络
> _./network_

- 网络消息：
  - S2C游戏信息
  - S2C观战信息
  - S2C队伍信息
  - S2C区域信息
- NetworkHandler：网络处理器（注册网络消息、发送消息）
- NetworkHook：网络钩子，打开方块实体 GUI（物资刷新方块）

### 资源
> _./resource_

- 资源加载器：（暂未使用）

### 工具
> _./util_

- ChatUtils：封装如何向玩家发送聊天栏消息、标题等
- ClassUtils：基本数据类，用于简化算法和优化性能
- ColorUtils：处理颜色字符串、游戏内颜色等
- CommandUtils：向指令添加元素更便捷
- GameUtils：游戏时间、游戏玩家相关
- JsonUtils：各种数据类型的Json序列化便捷封装
- ListUtils：列表操作
- NBTUtils：NBT序列化
- SendUtils：封装网络消息的发送，使网络处理器（`NetworkHandler`）与项目主体解耦
- StringUtils：解析/转换各种格式字符串、构建字符串
- Vec3Utils：向量随机偏移、向量加减/线性插值等

# English