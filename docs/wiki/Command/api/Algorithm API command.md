[English](#English)

## 算法API指令
> _IAlgorithmApi_

> _/battleroyale api algorithm [...]_

### 均匀分布算法
> _IDistribution_

> _/battleroyale api algorithm [distributionType] [...]_

distributionType：分布类型
- 矩形网格：rectangleGrid
- 黄金螺旋：goldenSpiral
- 双圆心网格：circleGrid

#### 生成均匀分布点位
> _/battleroyale api algorithm [distributionType] [pos] [xyz] [count] [allowOnBorder] [globalShrinkRatio]_
> 
> _/battleroyale api algorithm [distributionType] [xyz] [count] [allowOnBorder] [globalShrinkRatio]_

生成均匀分布点位，保存至相应分布类型的缓存：
- pos：分布的中心位置，如无则取指令 _/execute at_ 位置
- xyz：分布的XYZ分量
- count：模拟的点位数量
- allowOnBorder：是否允许点位在边界上
- globalShrinkRatio：全局缩放比例

#### 打乱点位
> _/battleroyale api algorithm [distributionType] shuffle_

打乱[缓存的点位](#生成均匀分布点位)列表：
- `返回值`：[缓存的点位](#生成均匀分布点位)数量

#### 限量点位
> _/battleroyale api algorithm [distributionType] [minPoint] [maxPoint]_

将[缓存的点位](#生成均匀分布点位)取第 _minPoint_ 到 _maxPoint_ 个：
- `返回值`：[缓存的点位](#生成均匀分布点位)数量

#### 随机偏移点位
> _/battleroyale api algorithm [distributionType] [xyz] [rangeType]_

将[缓存的点位](#生成均匀分布点位)逐个进行随机偏移：
- 若[缓存的点位](#生成均匀分布点位)数量为空，`返回值`为 -1
- xyz：随机偏移的输入向量
- rangeType：随机偏移类型
	- randomAdjustXYZ：以输入向量为基准，往XYZ正反方向随机偏移
	- randomAdjustXZExpandY：以输入向量为基准，往XZ正反方向，Y正方向随机偏移
	- scaleXYZ：以输入向量为基准，缩放XYZ方向
	- randomCircleXZExpandY：以输入向量为基准，取X分量为半径，在XZ平面的圆内随机取点，Y正方向随机偏移
	- randomSphereXYZ：以输入向量为基准，取Y分量为半径，在XYZ球内随机偏移
- `返回值`：[缓存的点位](#生成均匀分布点位)数量

#### 计划传送
> _/battleroyale api algorithm [distributionType] teleport [findGround] [maxHangTime] [...]_

- findGround：是否传送至该点的地面，模组会在前 _maxHangTime_ tick不断尝试寻找地面，超时则从该点1145.14高度处下落
- maxHaneTime：当传送位置区块未加载或为虚空（无法通过射线检测）时，让玩家停留在空中的时间
> 注：原版 _/spreadplayers_ 会立即加载区块（阻塞线程），而该命令是异步逐个加载点位所在区块；若某点位对应虚空，则该位置将始终无法找到地面

##### 计划传送游戏玩家
> _/battleroyale api algorithm [distributionType] teleport [findGround] [maxHangTime] gamePlayer [type]_

在游戏维度创建计划传送：
- type：游戏玩家列表
	- gamePlayers：所有游戏玩家
	- standingGamePlayers：未被淘汰的游戏玩家
- 若不包含游戏玩家或[缓存的点位](#生成均匀分布点位)为空，`返回值`为 0
- `返回值`：是否成功创建计划传送

##### 计划传送生物
> _/battleroyale api algorithm [distributionType] teleport [findGround] [maxHangTime] player [all]_

在指令 _/execute in_ 维度创建计划传送：
- all：实体选择器
- 若实体选择器为空或[缓存的点位](#生成均匀分布点位)为空，`返回值`为 0
- 若实体选择器过滤非生物实体后为空，则创建计划失败，影响`返回值`
- `返回值`：是否成功创建计划传送

# English

## Algorithm API command
> _IAlgorithmApi_

> _/battleroyale api algorithm [...]_

### Uniform distribution algorithm
> _IDistribution_

> _/battleroyale api algorithm [distributionType] [...]_

distributionType: Distribution type
- Rectangle grid: rectangleGrid
- Golden spiral: goldenSpiral
- Double center grid: circleGrid

#### Generate uniform distribution algorithm
> _/battleroyale api algorithm [distributionType] [pos] [xyz] [count] [allowOnBorder] [globalShrinkRatio]_
> 
> _/battleroyale api algorithm [distributionType] [xyz] [count] [allowOnBorder] [globalShrinkRatio]_

Generates uniformly distributed points and saves them to the cache of the corresponding distribution type:
- pos: center position of the distribution, the _/execute at_ position is used if not specified
- xyz: XYZ components of the distribution
- count: number of simulated points
- allowOnBorder: Whether to allow spawn points to be exactly on the boundary.
- globalShrinkRatio: global scaling ratio applied to the distribution area

#### Shuffle points
> _/battleroyale api algorithm [distributionType] shuffle_

Shuffles the list of [cached points](#Generate-uniform-distribution-algorithm):
- `return value`: number of [cached points](#Generate-uniform-distribution-algorithm)

#### Bound points
> _/battleroyale api algorithm [distributionType] [minPoint] [maxPoint]_

Selects the $minPoint^{th}$ to $maxPoint^{th}$ points from the [cached points](#Generate-uniform-distribution-algorithm):
- `return value`: number of [cached points](#Generate-uniform-distribution-algorithm)

#### Random offset points
> _/battleroyale api algorithm [distributionType] [xyz] [rangeType]_

Randomly offsets each [cached points](#Generate-uniform-distribution-algorithm):
- If the [cached points](#Generate-uniform-distribution-algorithm) count is empty, the `return value` is -1.
- xyz: input vector for random offset
- rangeType: Random offset type
	- randomAdjustXYZ: Randomly offset in both positive and negative XYZ directions based on the input vector.
	- randomAdjustXZExpandY: Randomly offset in positive/negative XZ and positive Y directions based on the input vector.
	- scaleXYZ: Scale XYZ directions based on the input vector.
	- randomCircleXZExpandY: Randomly offset within a circle on the XZ plane with X component as the radius, and randomly offset in the positive Y direction based on the input vector.
	- randomSphereXYZ: Randomly offset within a sphere with Y component as the radius based on the input vector.
- `return value`: number of [cached points](#Generate-uniform-distribution-algorithm)

#### Schedule teleport
> _/battleroyale api algorithm [distributionType] teleport [findGround] [maxHangTime] [...]_

- findGround: Whether to teleport to the ground at the teleport point. The mod will try to find the ground continuously in the first _maxHangTime_ tick of the game. If it times out, it will fall from the height of 1145.14 at the point
- maxHaneTime: How long to keep the player airborne when the teleport target chunk is not loaded or the area is considered void (cannot pass ray tracing).
> Note: The vanilla _/spreadplayers_ command loads chunks immediately (blocking the thread), whereas this command loads chunks asynchronously for each point. If a point corresponds to the void, the ground will never be found at that location.

##### Schedule teleport game players
> _/battleroyale api algorithm [distributionType] teleport [findGround] [maxHangTime] gamePlayer [type]_

Creates a scheduled teleport in the game dimension:
- type: Game player list
	- gamePlayers: all game players
	- standingGamePlayers: non-eliminated game players
- If there are no game players or the [cached points](#Generate-uniform-distribution-algorithm) are empty, the `return value` is 0.
- `return value`: Whether the scheduled teleport was successfully created.

##### Schedule teleport living entities
> _/battleroyale api algorithm [distributionType] teleport [findGround] [maxHangTime] player [all]_

Creates a scheduled teleport in the _/execute in_ dimension:
- all: entity selector
- If the entity selector is empty or the [cached points](#Generate-uniform-distribution-algorithm) are empty, the `return value` is 0.
- If the entity selector is empty after filtering out non-living entities, the creation fails, affecting the `return value`.
- `return value`: Whether the scheduled teleport was successfully created.