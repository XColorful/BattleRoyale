[English](#English)

# 出生配置

## 单个配置

- id：出生配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
- preZoneCenterOffset：（可选）以指定区域的起始中心为原点进行偏移
- entry：出生词条
```json
{
	"id": 0,
	"name": "Random ground spawn",
	"color": "#FFFFFFAA",
	"preZoneCenterOffset": -1,
	"entry": {
		出生词条
	}
}
```

## 出生词条

### 原版出生类型

#### 传送
- spawnType："teleport"
- spawnShapeType：传送区域形状，"square"方形，"rectangle"矩形，"circle"圆形
- center：传送区域底面的中心点坐标
- dimension：方形和圆形取dimension.x作为半边长/半径，确定传送区域的水平范围（忽略y方向）
- detailType：传送词条类型
- teamTogether：同队伍玩家是否一起传送
- findGround：是否传送至该点的地面，模组会在游戏前 _hangTime_ tick不断尝试寻找地面，超时则从该点1145.14高度处下落
- randomRange：确定传送点后，随机往x、z方向偏移[-randomRange, randomRange]，往y方向偏移[0, randomRange]
- hangTime：当传送位置区块未加载或为虚空（无法通过射线检测）时，让玩家停留在空中的时间

##### 随机传送
- detailType："random"
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "square",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "random",
	"teamTogether": false,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300
}
```

##### 固定点位传送
- detailType："fixed"
- fixedPos：可用的固定传送点坐标
- needShuffle：打乱可选的点位
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "square",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "fixed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"fixedPos": [
		"0.0,-60.0,0.0",
		"-50.0,-60.0,-50.0",
		"50.0,-60.0,-50.0",
		"50.0,-60.0,-50.0",
		"-50.0,-60.0,50.0"
	],
	"needShuffle": false
}
```

##### 网格分布传送
- detailType："distributed"
> - 当 _spawnShapeType_ 为"square"或"rectange"时，使用网格采样（Jittered Grid Sampling），该算法计算的点位天然在边界内部
> - 当 _spawnShapeType_ 为"circle"时，使用双圆心网格分布或黄金螺旋分布
- fixedSimulationCount：固定模拟数量
- playerFactorContribution：玩家比例贡献
> 最终模拟的数量为：_固定模拟数量_ + _玩家数量上限_ * _玩家比例贡献_，若模拟数量不足则循环选取
- allowOnBorder：是否允许点位在边界上
- globalShrinkRatio：全局缩放比例
- needShuffle：打乱可选的点位
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "rectangle",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "distributed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"fixedSimulationCount": 0,
	"playerFactorContribution": 1.0,
	"allowOnBorder": false,
	"globalShrinkRatio": 1.0,
	"needShuffle": true,
}
```

###### 双圆心网格分布传送
_（spawnShapeType为"circle"时可用）_
- 当 _allowOnBorder_ 为false时，缩放比例为 _r(N) / r(N+1)_，其中 _r(N)_ 是当模拟数量为N时，刚好包含N个点位的圆的半径长，模组会自动找到包含≥n个点位的圆并使用其包含的点位数量N
> - 双圆心网格计算的点位在N>1时将有4k个点位在边界上
> - 以(0, 0)为中心，半径r=18的圆内包含约1000个网格点
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "circle",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "distributed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"useGoldenSpiral": false,
	"fixedSimulationCount": 0,
	"playerFactorContribution": 1.0,
	"allowOnBorder": false,
	"globalShrinkRatio": 1.0,
	"needShuffle": true,
}
```

###### 黄金螺旋分布传送
_（spawnShapeType为"circle"时可用）_
- useGoldenSpiral：是否启用黄金螺旋分布
- 当 _allowOnBorder_ 为true时，分布算法会应用 _1 - 1 / √(N+4)_ 缩放
> 黄金螺旋计算的点位在N>1时有且仅有1个点位在边界上
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "circle",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "distributed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"useGoldenSpiral": true,
	"fixedSimulationCount": 0,
	"playerFactorContribution": 1.0,
	"allowOnBorder": false,
	"globalShrinkRatio": 1.0,
	"needShuffle": true,
}
```

### 特殊出生类型

#### 飞机
暂未实现

- spawnType："plane"
- spawnShapeType：飞行区域形状，"square"方形，"rectangle"矩形，"circle"圆形
- center：飞行区域底面的中心点坐标
- dimension：方形和圆形取dimension.x作为半边长/半径，确定飞行区域的水平范围（忽略y方向）
- detailType："fixed"固定航线，"random"随机航线
- planeHeight：飞行高度
- planeSpeed：飞行速度（单位：格/tick）
- fixedFlightTime：是否固定飞行时长，因飞行区域形状不同而不同
- routeRatio：暂未实现
其他：暂未实现

# English

## Single spawn config

- id: unique spawn id
- name: name the config, can be repeated
- color: no function for now
- preZoneCenterOffset: (optional) offset from the specified zone start center
- entry: spawn entry
```json
{
	"id": 0,
	"name": "Random ground spawn",
	"color": "#FFFFFFAA",
	"preZoneCenterOffset": -1,
	"entry": {
		SPAWN ENTRY
	}
}
```

## Spawn entry

### Vanilla spawn types

#### Teleport
- spawnType: "teleport"
- spawnShapeType: shape of the teleport area, "square", "rectangle", "circle"
- center: coordinates of the center point of the bottom of the teleport area
- dimension: square and circle take dimension.x as half length/radius to determine the horizontal range of the teleport area (ignore y direction)
- detailType: type of teleport entry
- fixedPos: _(available when detailType is "fixed")_ Available fixed teleport point coordinates
- teamTogether: whether players on the same team are teleported together
- findGround: Whether to teleport to the ground at the teleport point. The mod will try to find the ground continuously in the first _hangTime_ tick of the game. If it times out, it will fall from the height of 1145.14 at the point
- randomRange: After determining the teleport point, randomly offset to [-randomRange, randomRange] in the x and z directions, and to [0, randomRange] in the y direction
- hangTime: How long to keep the player airborne when the teleport target chunk is not loaded or the area is considered void (cannot pass ray tracing).

##### Random teleport
- detailType: "random"
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "square",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "random",
	"teamTogether": false,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300
}
```

##### Fixed point teleport
- detailType: "fixed"
- needShuffle: Shuffles the list of available spawn points before selection.
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "square",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "fixed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"fixedPos": [
		"0.0,-60.0,0.0",
		"-50.0,-60.0,-50.0",
		"50.0,-60.0,-50.0",
		"50.0,-60.0,-50.0",
		"-50.0,-60.0,50.0"
	],
	"needShuffle": false
}
```

##### Grid distributed teleport
- detailType: "distributed"
> - When _spawnShapeType_ is "square" or "rectangle", Jittered Grid Sampling is used. The points calculated by this algorithm are naturally inside the boundary.
> - When _spawnShapeType_ is "circle", Double center grid distribution or Golden Spiral Distribution is used.
- fixedSimulationCount: fixed number of simulation points
- playerFactorContribution: player ratio contribution
> The final simulated quantity is: _fixedSimulationCount_ + _playerLimit_ * _playerFactorContribution_. If the simulated quantity is insufficient, selection will loop.
- allowOnBorder: Whether to allow spawn points to be exactly on the boundary.
- globalShrinkRatio: global scaling ratio applied to the distribution area
- needShuffle: Shuffles the list of available spawn points before selection.
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "rectangle",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "distributed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"fixedSimulationCount": 0,
	"playerFactorContribution": 1.0,
	"allowOnBorder": false,
	"globalShrinkRatio": 1.0,
	"needShuffle": true,
}
```

###### Double center grid distribution teleport
_(Available when spawnShapeType is "circle")_
- When _allowOnBorder_ is false, the scaling ratio is _r(N) / r(N+1)_, where _r(N)_ is the radius of the circle that just encloses N grid points. The mod automatically finds a circle that contains ≥ N points and uses its total point count N.
> - For N>1, the points calculated by the Double Center Grid will have 4k points exactly on the boundary.
> - A circle centered at (0,0) with a radius r=18 contains approximately 1000 grid points.
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "circle",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "distributed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"useGoldenSpiral": false,
	"fixedSimulationCount": 0,
	"playerFactorContribution": 1.0,
	"allowOnBorder": false,
	"globalShrinkRatio": 1.0,
	"needShuffle": true,
}
```

###### Golden Spiral Distribution Teleport
_(Available when spawnShapeType is "circle")_
- useGoldenSpiral: Whether to enable Golden Spiral distribution.
- When _allowOnBorder_ is true, the distribution algorithm applies a scaling factor of _1 - 1 / √(N+4)_
> For N>1, the Golden Spiral calculation results in only 1 point exactly on the boundary.
```json
{
	"spawnType": "teleport",
	"spawnShapeType": "circle",
	"center": "0.0,-60.0,0.0",
	"dimension": "128.0,0.0,128.0",
	"detailType": "distributed",
	"teamTogether": true,
	"findGround": true,
	"randomRange": 0.0,
	"hangTime": 300,
	"useGoldenSpiral": true,
	"fixedSimulationCount": 0,
	"playerFactorContribution": 1.0,
	"allowOnBorder": false,
	"globalShrinkRatio": 1.0,
	"needShuffle": true,
}
```

### Special spawn types

#### Plane
Not implemented yet

- spawnType: "plane"
- spawnShapeType: flight area shape, "square", "rectangle", "circle"
- center: center point coordinates of the bottom of the flight area
- dimension: square and circle take dimension.x as half side length/radius to determine the horizontal range of the flight area (ignore y direction)
- detailType: "fixed" for fixed route, "random" for random route
- planeHeight: flight altitude
- planeSpeed: flight speed (unit: grid/tick)
- fixedFlightTime: whether to fix the flight time, which varies depending on the shape of the flight area
- routeRatio: not yet implemented
Others: not yet implemented