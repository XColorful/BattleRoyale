[English](#English)

## 区域管理器

- setStackZoneConfig：加载配置时叠加区域配置，用于快速测试多个区域配置合并后的效果
- randomizeZoneTickOffset：随机偏移区域功能延迟，均摊区域功能计算时间
- getCommonZoneContext：获取游戏区域更新上下文，包含`游戏玩家`列表
- getZoneContextInGame：获取游戏中的游戏区域更新上下文，包含`未被淘汰的游戏玩家`列表

[![IZoneManager](/docs/api/game/zone/IZoneManager.md)](/docs/api/game/zone/IZoneManager.md)

#### 区域管理器启动流程

- initGameConfig：读取区域配置
- startGame：执行`randomizeZoneTickOffset`

### 获取游戏区域

游戏区域默认实现为`GameZone`，若需要替换则应基于`GameZone`的实现方式
- getGameZones：获取`游戏区域`列表
- getCurrentGameZones：获取当前`游戏时间`更新的`游戏区域`列表
- getCurrentGameZones：获取指定`游戏时间`更新的`游戏区域`列表
- getGameZone：通过`区域管理器`管理的**唯一区域ID**查询`游戏区域`，不小于0
> 默认`区域管理器`维护的**唯一区域ID**对应`GameZone`的**固定区域ID**，不建议修改该机制

[![IGameZoneReadApi](/docs/api/game/zone/IGameZoneReadApi.md)](/docs/api/game/zone/IGameZoneReadApi.md)

### 游戏区域

游戏区域包含3个可排列组合的类型
- [区域功能类型](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域功能词条)
- [区域形状类型](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域形状词条)
- [区域特殊类型](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域特殊词条)

[![IGameZone](/docs/api/game/zone/gamezone/IGameZone.md)](/docs/api/game/zone/gamezone/IGameZone.md)

[![ITickableZone](/docs/api/game/zone/gamezone/ITickableZone.md)](/docs/api/game/zone/gamezone/ITickableZone.md)

[![ISpatialZone](/docs/api/game/zone/gamezone/ISpatialZone.md)](/docs/api/game/zone/gamezone/ISpatialZone.md)

[![IAdditionalZone](/docs/api/game/zone/gamezone/IAdditionalZone.md)](/docs/api/game/zone/gamezone/IAdditionalZone.md)

# English

## Zone Manager

- setStackZoneConfig: Stacks zone configurations when loading to quickly test the combined effect of multiple zone configurations.
- randomizeZoneTickOffset: Randomly offsets the zone function delay to distribute the zone function calculation time (load balancing).  
- getCommonZoneContext: Gets the game zone update context, including the list of `Game Players`.  
- getZoneContextInGame: Gets the in-game zone update context, including the list of `Non-Eliminated Game Players`.  

[![IZoneManager](/docs/api/game/zone/IZoneManager.md)](/docs/api/game/zone/IZoneManager.md)

#### Zone Manager Startup Flow

- initGameConfig: Reads zone configuration.
- startGame: Executes `randomizeZoneTickOffset`.

### Get Game Zone

The default implementation of a game zone is `GameZone`. If replacement is needed, it should be based on the implementation of `GameZone`.
- getGameZones: Gets the list of `Game Zones`. 
- getCurrentGameZones: Gets the list of `Game Zones` that update at the current `game time`.
- getCurrentGameZones: Gets the list of `Game Zones` that update at the specified `game time`.
- getGameZone: Queries a `Game Zone` by the **Unique Zone ID** managed by the `Zone Manager` (must be ≥ 0).
> By default, the **Unique Zone ID** maintained by the `Zone Manager` corresponds to the **Fixed Zone ID** of the `GameZone`. Modifying this mechanism is not recommended.

[![IGameZoneReadApi](/docs/api/game/zone/IGameZoneReadApi.md)](/docs/api/game/zone/IGameZoneReadApi.md)

### Game Zone

A Game Zone includes 3 combinable types:
- [Zone Function type](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-function-entry)
- [Zone Shape type](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-shape-entry)
- [Zone Special type](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-special-entry)

[![IGameZone](/docs/api/game/zone/gamezone/IGameZone.md)](/docs/api/game/zone/gamezone/IGameZone.md)

[![ITickableZone](/docs/api/game/zone/gamezone/ITickableZone.md)](/docs/api/game/zone/gamezone/ITickableZone.md)

[![ISpatialZone](/docs/api/game/zone/gamezone/ISpatialZone.md)](/docs/api/game/zone/gamezone/ISpatialZone.md)

[![IAdditionalZone](/docs/api/game/zone/gamezone/IAdditionalZone.md)](/docs/api/game/zone/gamezone/IAdditionalZone.md)