[English](#English)

# 区域配置

游戏开始时，区域配置按“先zoneDelay，后zoneId”的方式排序

## 单个配置

- zoneId：区域配置唯一id
- zoneName：为该区域命名，可重复
- zoneColor：区域在客户端渲染的颜色
- preZoneDelayId：（可选）叠加先前区域延迟
- zoneDelay：区域生成延迟
- zoneTime：区域持续时间
- zoneFunc：区域功能词条
- zoneShape：区域形状词条
- zoneSpecial：区域特殊词条
> preZoneDelayId生效规则：
> 所有区域按id从小到大排序依次执行叠加，查找到zondId存在则叠加并更新自身延迟
> 例如区域0延迟90秒，区域1延迟60秒，区域1叠加区域0后变为150秒，此时区域2若叠加区域1则可获得150秒延迟
```json
{
	"zoneId": 0,
	"zoneName": "Blue opaque border",
	"zoneColor": "#0000FFFF",
	"preZoneDelayId": -1,
	"zoneDelay": 0,
	"zoneTime": 12000,
	"zoneFunc": {
		"zoneFuncType": 区域功能类型
		区域功能属性
	},
	"zoneShape": {
		"zoneShapeType": 区域形状类型
		区域形状属性
	},
	"zoneSpecial": {
		"zoneSpecialType": 区域特殊类型
		区域特殊属性
	}
}
```

## 区域功能词条

### 简单功能词条

- moveDelay：区域移动延迟，移动路径为两点间直线段
- moveTime：区域移动时长，0表示立即移动至结束位置
- tickFrequency：区域功能触发频率
- tickOffset：区域功能触发延迟，设置为-1则随机取[0, tickFrequency)
```json
"zoneFunc": {
	"zoneFuncType": 区域功能类型
	"moveDelay": 0,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1
}
```

#### [安全区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#安全区)

#### [不安全区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#不安全区)

#### [烟花区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#烟花区)

#### [无敌区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#无敌区)

#### [能量区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#能量区)

#### [粒子区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#粒子区)

#### [效果区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#效果区)

#### [消息区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#消息区)

#### [背包区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#背包区)

#### [无功能区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#无功能区)

### 特殊功能词条

#### [通用事件区](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#通用事件区)

#### [补给箱区](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#补给箱区)

#### [实体刷新区](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#实体刷新区)

#### [强制刷新区](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#强制刷新区)

#### [撤离区](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#撤离区)

#### [占领区](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#占领区)

## 区域形状词条

### 二维判定形状

- zoneShapeType：形状类型
- start：起始位置词条
- end：终点位置词条
- center：区域底面中心点
- dimension：因形状而异
- rotation：在水平平面旋转区域
- allowBadShape：是否允许负维度，因形状而异
```json
"zoneShape": {
	"zoneShapeType": 形状类型
	"start": {
		"center": {
			起始中心词条
		},
		"dimension": {
			起始维度词条
		},
		"rotation": {
			起始旋转词条
		}
	},
	"end": {
		"center": {
			终点中心词条,
			"rangeAsStartDimScale": false,
			"useCircleRange", false
		},
		"dimension": {
			终点维度词条
		},
		"rotation": {
			终点旋转词条
		}
	},
	"allowBadShape": 启用坏形状
}
```

#### 起止中心

- centerType："fixed"固定点位，"previous"先前区域点位，"relative"取先前区域点位再偏移，"lockPlayer"锁定玩家位置
- fixed：_（centerType为"fixed"时可用）_ 区域底面中心坐标
- previousZoneId：_（centerType为"previous"或"relative"时可用）_ start、end词条分别使用区域id选定先前区域
- progress：_（centerType为"previous"或"relative"时可用）_ 范围[0,1]，选定区域开始到结束进度中某时刻
- relative：_（centerType为"relative"时可用）_ 在获取到先前区域坐标后加上该向量
- playerId：_（centerType为"lockPlayer"时可用）_ 在玩家列表里选定该id的玩家，0则表示随机，没找到则该区域生成失败
- selectStanding：_（centerType为"lockPlayer"时可用）_ 是否在未被淘汰玩家列表里锁定玩家位置
- randomRange：确定坐标后，x、z方向速记偏移[-randomRange, randomRange]
- playerCenterLerp：取坐标到玩家中心点的向量，将坐标加上该倍数的向量（例如设置为1，则等价于玩家中心点，设置为0.5，则为距离中点）

#### 终点中心
- rangeAsStartDimScale：（可选）使用randomRange * 起始维度作为随机向量（分别取dim.x，dim.y，dim.z作为三个方向的随机量）
- useCircleRange：（可选） _rangeAsStartDimScale_ 是否使用圆形随机
> - 2D形状则取向量x分量为半径在平面内圆上等概率随机
> - 3D形状则在球内等概率随机

#### 起止维度

- dimensionType："fixed"固定维度，"previous"先前区域维度，"relative"先前区域维度再调整
- fixed：_（dimensionType为"fixed"时可用）_ 区域维度，y方向为中心点起往上的高度
- previousZoneId：_（dimensionType为"previous"或"relative"时可用）_ start、end词条分别使用区域id选定先前区域
- progress：_（centerType为"previous"或"relative"时可用）_ 范围[0,1]，选定区域开始到结束进度中某时刻
- relative：_（dimensionType为"relative"时可用）_ 在获取到先前区域维度后加上该向量
- randomRange：确定维度后，x、z方向随机调整[-randomRange, randomRange]
- scale：_（dimensionType为"previous"或"relative"时可用）_ 在随机调整维度后，x、z方向缩放 _scale_ 倍
- randomRange：确定坐标后，x、z方向速记偏移[-randomRange, randomRange]

#### 起止旋转
- rotationType："fixed"固定角度，"previous"先前区域角度，"relative"取先前区域角度再旋转，"lockPlayer"锁定玩家视角
- fixed：_（rotationType为"fixed"时可用）_ 区域Y轴旋转角度，正值为顺时针旋转
- previousZoneId：_（rotationType为"previous"或"relative"时可用）_ start、end词条分别使用区域id选定先前区域
- progress：_（rotationType为"previous"或"relative"时可用）_ 范围[0,1]，选定区域开始到结束进度中某时刻
- relative：_（rotationType为"relative"时可用）_ 在获取到先前区域旋转后加上该角度
- playerId：_（rotationType为"lockPlayer"时可用）_ 在玩家列表里选定该id的玩家，0则表示随机，没找到则该区域生成失败
- scale：_（rotationType为"previous"或"relative"时可用）_ 在随机调整角度后，角度缩放 _scale_ 倍
- randomRange：确定角度后，随机旋转[-randomRange, randomRange]

#### 坏形状
值为 _true_ 或 _false_ ，默认为false
- 若在生成区域时的预计算期间判断会出现维度为负，且不允许坏形状，则会至少进行正值校正

#### [圆形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#圆形)

#### [方形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#方形)

#### [矩形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#矩形)

#### [平顶正六边形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#平顶正六边形)

#### [尖顶正多边形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#尖顶正多边形)

#### [星形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#星形)

#### [椭圆](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#椭圆)

#### [不规则多边形](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#不规则多边形)
暂未实现

### 立体判定形状

#### 起止中心

- randomRange：确定坐标后，x、y、z方向随机偏移[-randomRange, randomRange]

#### 起止维度

- randomRange：确定维度后，x、y、z方向随机调整[-randomRange, randomRange]

#### [球](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#球)

#### [半球](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#半球)

#### [正方体](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#正方体)

#### [长方体](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#长方体)

#### [椭球](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#椭球)

## 区域特殊词条

```json
"zoneSpecial": {
	"zoneSpecialType": 区域特殊类型
	区域特殊属性
}
```

### 特殊客户端功能

#### [附加渲染](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-client#附加渲染)


# English

At the start of the game, the zone configs are sorted in the order of "zoneDelay first, then zoneId"

## Single zone config

- zoneId: unique zone id
- zoneName: name the zone, can be repeated
- zoneColor: color of the zone rendered on the client side
- preZoneDelayId: (Optional) add previous zone delay
- zoneDelay: zone generation delay
- zoneTime: zone duration
- zoneFunc: zone function entry
- zoneShape: zone shape entry
- zoneSpecial: zone special entry
> preZoneDelayId effective rules:
> All zones are sorted from small to large by id and superimposed in sequence. If zondId exists, it is superimposed and its own delay is updated.
> For example, zone 0 has a delay of 90 seconds, zone 1 has a delay of 60 seconds, and zone 1 becomes 150 seconds by adding zone 0. At this time, if zone 2 add zone 1, it can get a 150-second delay
```json
{
	"zoneId": 0,
	"zoneName": "Blue opaque border",
	"zoneColor": "#0000FFFF",
	"preZoneDelayId": -1,
	"zoneDelay": 0,
	"zoneTime": 12000,
	"zoneFunc": {
		"zoneFuncType": ZONE FUNCTION TYPE
		ZONE FUNC PROPERTY
	},
	"zoneShape": {
		"zoneShapeType": ZONE SHAPE TYPE
		ZONE SHAPE PROPERTY
	},
	"zoneSpecial": {
		"zoneSpecialType": ZONE SPECIAL TYPE
		ZONE SPECIAL PROPERTY
	}
}
```

## Zone function entry

### Zone simple function

- moveDelay: delay in area movement, the movement path is a straight line segment between two points
- moveTime: duration of area movement, 0 means immediate movement to the end position
- tickFrequency: zone function trigger frequency
- tickOffset: zone function trigger delay, set to -1 to randomly select [0, tickFrequency)
```json
"zoneFunc": {
	"zoneFuncType": ZONE FUNCTION TYPE
	"moveDelay": 0,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1
}
```

#### [Safe zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Safe-zone)

#### [Unsafe zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Unsafe-zone)

#### [Firework zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Firework-zone)

#### [Muteki zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Muteki-zone)

#### [Boost zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Boost-zone)

#### [Particle zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Particle-zone)

#### [Effect zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Effect-zone)

#### [Message zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Message-zone)

#### [Inventory zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Inventory-zone)

#### [No function zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#No-function-zone)

### Zone special function

#### [Common event zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#Common-event-zone)

#### [Airdrop zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#Airdrop-zone)

#### [Entity loot zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#Entity-loot-zone)

#### [Forced loot zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#Forced-loot-zone)

#### [Extraction zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#Extraction-zone)

#### [Capture zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-function#Capture-zone)

## Zone shape entry

### 2D shape

- zoneShapeType: shape type
- start: starting position entry
- end: end position entry
- center: center point of the bottom surface of the zone
- dimension: varies according to the shape
- rotation: rotate zone in horizontal plane
- allowBadShape: whether allow negative dimension, varies according to the shape
```json
"zoneShape": {
	"zoneShapeType": ZONE SHAPE TYPE
	"start": {
		"center": {
			START CENTER ENTRY
		},
		"dimension": {
			START DIMENSION ENTRY
		},
		"rotation": {
			START ROTATION ENTRY
		}
	},
	"end": {
		"center": {
			END CENTER ENTRY,
			"rangeAsStartDimScale": false,
			"useCircleRange", false
		},
		"dimension": {
			END DIMENSION ENTRY
		},
		"rotation": {
			END ROTATION ENTRY
		}
	},
	"allowBadShape": ALLOW BAD SHAPE
}
```

#### Start/End center

- centerType: "fixed" for fixed point, "previous" for previous zone point, "relative" for previous zone point and then offset
- fixed: _(available when centerType is "fixed")_ The coordinates of the center of the bottom surface of the zone
- previousZoneId: _(available when centerType is "previous" or "relative")_ The start and end entries use the zoneId to select the previous zone respectively
- progress: _(available when centerType is "previous" or "relative")_ The range is [0,1], the selected zone starts and ends at a certain moment in the progress
- relative: _(available when centerType is "relative")_ Add this vector after getting the coordinates of the previous zone
- playerId: _(available when centerType is "lockPlayer")_ Select the player with this id in the player list, 0 means random, if not found, the zone generation fails
- selectStanding: _(available when centerType is "lockPlayer")_ Whether to select the player's position in the list of non-eliminated players
- randomRange: After determining the coordinates, the x and z directions are offset in shorthand [-randomRange, randomRange]
- playerCenterLerp: Takes the vector from the current coordinate to the player center point, and adds that vector multiplied by this value to the coordinate (e.g., set to 1 is equivalent to the player center point, set to 0.5 is the midpoint).

#### End center
- rangeAsStartDimScale: (Optional) Use randomRange * starting dimension as random vector (take dim.x, dim.y, dim.z as random quantities in three directions respectively)
- useCircleRange: (Optional) _rangeAsStartDimScale_ Whether to use circular randomness.
> - For 2D shapes, the vector x component is used as the radius to randomly generate random numbers with equal probability on the circle in the plane.
> - For 3D shapes, the vector x component is used as the radius to randomly generate random numbers with equal probability on the circle in the plane.

#### Start/End dimension

- dimensionType: "fixed" for fixed dimension, "previous" for previous zone dimension, "relative" for previous zone dimension adjusted
- fixed: _(available when dimensionType is "fixed")_ Zone dimension, y direction is the height from the center point upwards
- previousZoneId: _(available when dimensionType is "previous" or "relative")_ The start and end entries use the zoneId to select the previous zone respectively
- progress: _(available when centerType is "previous" or "relative")_ The range is [0,1], the selected zone starts and ends at a certain moment in the progress
- relative: _(available when dimensionType is "relative")_ Add this vector after getting the previous zone dimension
- randomRange: After determining the dimension, randomly adjust the x and z directions [-randomRange, randomRange]
- scale: _(available when dimensionType is "previous" or "relative")_ After randomly adjusting the dimension, scale the x and z directions by _scale_ times

#### Start/End rotation
- rotationType: "fixed" for fixed angle, "previous" for previous zone angle, "relative" for previous zone angle and then rotate, "lockPlayer" to lock player's view direction
- fixed: _(available when rotationType is "fixed")_ The Y-axis rotation angle of the zone; positive values rotate clockwise
- previousZoneId: _(available when rotationType is "previous" or "relative")_ The start and end entries use the zoneId to select the previous zone respectively
- progress: _(available when rotationType is "previous" or "relative")_ The range is [0,1], the selected zone starts and ends at a certain moment in the progress
- relative: _(available when rotationType is "relative")_ Add this angle after getting the previous zone's rotation
- playerId: _(available when rotationType is "lockPlayer")_ Select the player with this id in the player list, 0 means random, if not found, the zone generation fails
- scale: _(available when rotationType is "previous" or "relative")_ After randomly adjusting the angle, scale the angle by _scale_ times
-  randomRange: After determining the angle, randomly rotate [-randomRange, randomRange]

#### Bad Shape
The value is _true_ or _false_, default it false
- During the pre-calculation phase of zone generation, if a negative dimension is found and _allowBadShape_ set to _false_, at least a positive correction will be performed

#### [Circle](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Circle)

#### [Square](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Square)

#### [Rectangle](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Rectangle)

#### [Flat top regular hexagon](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Flat-top-regular-hexagon)

#### [Spike regular polygon](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Spike-regular-polygon)

#### [Star](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Star)

#### [Ellipse](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Ellipse)

#### [Irregular polygon](https://github.com/XColorful/BattleRoyale/wiki/Zone-2D-shape#Irregular-polygon)

### 3D shape

#### Start/End center

- randomRange: After determining the coordinates, the x , y, z directions are offset in shorthand [-randomRange, randomRange]

#### Start/End dimension

- randomRange: After determining the dimension, randomly adjust the x, y, z directions [-randomRange, randomRange]

#### [Sphere](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#Sphere)

#### [Hemisphere](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#Hemisphere)

#### [Cube](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#Cube)

#### [Cuboid](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#Cuboid)

#### [Ellipsoid](https://github.com/XColorful/BattleRoyale/wiki/Zone-3D-shape#Ellipsoid)

## Zone special entry

```json
"zoneSpecial": {
	"zoneSpecialType": ZONE SPECIAL TYPE
	ZONE SPECIAL PROPERTY
}
```

### Special client function

#### [Additiona render](https://github.com/XColorful/BattleRoyale/wiki/Zone-special-client#Additional-render)