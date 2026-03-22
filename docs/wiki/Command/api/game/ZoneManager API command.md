[English](#English)

### 区域管理器
> _IZoneManager_

> _/battleroyale api zoneManager [...]_

##### 是否有足够区域开始游戏
> _/battleroyale api zoneManager hasEnoughZoneToStart_

- `返回值`：当前游戏区域是否足够开始游戏

##### 随机区域功能延迟
> _/battleroyale api zoneManager randomizeZoneTickOffset_

- `返回值`：1

#### 游戏区域读取
> _IGameZoneReadApi_

##### 获取游戏区域
> _/battleroyale api zoneManager getGameZone [id]_

- id：区域ID
- `返回值`：是否存在对应的游戏区域

##### 游戏区域
> _IGameZone_

> _/battleroyale api zoneManager getGameZone [id] [...]_

- 若不存在对应的游戏区域，`返回值`为 -1

###### 获取游戏区域生成延迟
> _/battleroyale api zoneManager getGameZone [id] getZoneDelay_

- `返回值`：当前[区域生成延迟](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#单个配置)

###### 获取游戏是否创建
> _/battleroyale api zoneManager getGameZone [id] isCreated_

- `返回值`：当前游戏区域是否初始化成功

###### 获取游戏区域是否现存
> _/battleroyale api zoneManager getGameZone [id] isPresent_

- `返回值`：当前游戏区域是否正在执行阶段

###### 获取游戏区域是否结束
> _/battleroyale api zoneManager getGameZone [id] isFinished_

- `返回值`：当前游戏区域是否已执行完成

##### 游戏区域功能
> _/battleroyale api zoneManager getGameZone [id] tickableZone [...]_

- 若不存在对应的游戏区域，`返回值`为 -1

###### 获取游戏区域功能是否就绪
> _/battleroyale api zoneManager getGameZone [id] tickableZone isReady_

- `返回值`：当前游戏区域功能是否可以执行 funcTick

###### 获取游戏区域功能执行频率
> _/battleroyale api zoneManager getGameZone [id] tickableZone getTickFrequency_

- `返回值`：当前游戏区域功能 funcTick 频率，单位 tick

###### 设置游戏区域功能执行频率
> _/battleroyale api zoneManager getGameZone [id] tickableZone setTickFrequency [tickFreq]_

- tickFreq：funcTick 频率
- `返回值`：1

###### 获取游戏区域功能执行偏移
> _/battleroyale api zoneManager getGameZone [id] tickableZone getTickOffset_

- `返回值`：游戏区域功能 funcTick 的时间偏移

###### 设置游戏区域功能执行偏移
> _/battleroyale api zoneManager getGameZone [id] tickableZone setTickOffset [tickOffset]_

- tickOffset：funcTick 的时间偏移
- `返回值`：1

###### 对玩家执行区域功能
> _/battleroyale api zoneManager getGameZone [id] tickableZone playerFunc [player]_

- player：用实体选择器选中并获取游戏玩家
- 若不存在对应的游戏玩家，`返回值`为 -2
- 若无法获取游戏维度，`返回值`为 -3
- `返回值`：1

###### 获取游戏区域移动延迟
> _/battleroyale api zoneManager getGameZone [id] tickableZone getShapeMoveDelay_

- `返回值`：[区域移动延迟](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域功能词条)

###### 获取游戏区域移动时长
> _/battleroyale api zoneManager getGameZone [id] tickableZone getShapeMoveTime_

- `返回值`：[区域移动时长](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域功能词条)

##### 游戏区域形状
> _/battleroyale api zoneManager getGameZone [id] spatialZone [...]_

- 若不存在对应的游戏区域，`返回值`为 -1

###### 是否在区域内
> _/battleroyale api zoneManager getGameZone [id] spatialZone isWithinZone_
> 
> _/battleroyale api zoneManager getGameZone [id] spatialZone isWithinZone [xyz]_
> 
> _/battleroyale api zoneManager getGameZone [id] spatialZone isWithinZone [xyz] [progress]_

- xyz：指定的测试位置，如无则取指令 _/execute at_ 位置
- progress：区域形状进度，范围[0, 1]
- `返回值`：是否处于执行逻辑的区域范围内

###### 获取游戏区域形状是否确定
> _/battleroyale api zoneManager getGameZone [id] spatialZone isDetermined_

- `返回值`：区域形状起止状态是否已经确定

###### 获取区域形状是否有坏形状
> _/battleroyale api zoneManager getGameZone [id] spatialZone hasBadShape_

- `返回值`：区域过程是否有含有几何约束异常的状态

###### 获取区域形状边数
> _/battleroyale api zoneManager getGameZone [id] spatialZone getSegments_

- `返回值`：区域形状边数

# English

### Zone manager
> _IZoneManager_

> _/battleroyale api zoneManager [...]_

##### Has enough zone to start
> _/battleroyale api zoneManager hasEnoughZoneToStart_

- `return value`: whether current game zones are sufficient to start the game

##### Randomize zone tick offset
> _/battleroyale api zoneManager randomizeZoneTickOffset_

- `return value`: 1

#### Game zone read api
> _IGameZoneReadApi_

##### Get game zone
> _/battleroyale api zoneManager getGameZone [id]_

- id: zone ID
- `return value`: whether the corresponding game zone exists

##### Game zone
> _IGameZone_

> _/battleroyale api zoneManager getGameZone [id] [...]_

- if the game zone does not exist, the `return value` is -1

###### Get game zone delay
> _/battleroyale api zoneManager getGameZone [id] getZoneDelay_

- `return value`: current [zone generation delay](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Single-zone-config)

###### Is created
> _/battleroyale api zoneManager getGameZone [id] isCreated_

- `return value`: whether the current game zone was successfully initialized

###### Is present
> _/battleroyale api zoneManager getGameZone [id] isPresent_

- `return value`: whether the current game zone is in its execution phase

###### Is finished
> _/battleroyale api zoneManager getGameZone [id] isFinished_

- `return value`: whether the current game zone has finished execution

##### Game zone function
> _/battleroyale api zoneManager getGameZone [id] tickableZone [...]_

- if the game zone does not exist, the `return value` is -1

###### Is ready
> _/battleroyale api zoneManager getGameZone [id] tickableZone isReady_

- `return value`: whether the current game zone function is ready to execute funcTick

###### Get tick frequency
> _/battleroyale api zoneManager getGameZone [id] tickableZone getTickFrequency_

- `return value`: the game zone's current zone function tick frequency, in ticks

###### Set tick frequency
> _/battleroyale api zoneManager getGameZone [id] tickableZone setTickFrequency [tickFreq]_

- tickFreq: funcTick frequency
- `return value`: 1

###### Get tick offset
> _/battleroyale api zoneManager getGameZone [id] tickableZone getTickOffset_

- `return value`: the game zone's current zone function tick time offset

###### Set tick offset
> _/battleroyale api zoneManager getGameZone [id] tickableZone setTickOffset [tickOffset]_

- tickOffset: zone function tick time offset
- `return value`: 1

###### Execute player function
> _/battleroyale api zoneManager getGameZone [id] tickableZone playerFunc [player]_

- player: selects game player using an entity selector
- If the game player does not exist, the `return value` is -2.
- If the game dimension cannot be retrieved, the `return value` is -3.
- `return value`: 1

###### Get shape move delay
> _/battleroyale api zoneManager getGameZone [id] tickableZone getShapeMoveDelay_

- `return value`: [delay in zone movement](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-function-entry)

###### Get shape move time
> _/battleroyale api zoneManager getGameZone [id] tickableZone getShapeMoveTime_

- `return value`: [duration of zone movement](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-function-entry)

##### Game zone shape
> _/battleroyale api zoneManager getGameZone [id] spatialZone [...]_

- if the game zone does not exist, the `return value` is -1

###### Is within zone
> _/battleroyale api zoneManager getGameZone [id] spatialZone isWithinZone_
> 
> _/battleroyale api zoneManager getGameZone [id] spatialZone isWithinZone [xyz]_
> 
> _/battleroyale api zoneManager getGameZone [id] spatialZone isWithinZone [xyz] [progress]_

- xyz: specified test location, the _/execute at_ position is used if not specified
- progress: the progress of the zone shape, range [0, 1]
- `return value`: whether the position is within the zone's execution logic range

###### Is determined
> _/battleroyale api zoneManager getGameZone [id] spatialZone isDetermined_

- `return value`: whether the start/end states of the zone shape are determined

###### Has bad shape
> _/battleroyale api zoneManager getGameZone [id] spatialZone hasBadShape_

- `return value`: whether the zone process contains states with geometric constraint anomalies

###### Get segments
> _/battleroyale api zoneManager getGameZone [id] spatialZone getSegments_

- `return value`: the number of segments in the zone shape