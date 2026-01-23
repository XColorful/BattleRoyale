# 渲染配置

## 单个配置

- id：渲染配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
```json
{
	"id": 0,
	"name": "No limit",
	"color": "#FFFFFFAA",
	"block": {
		方块渲染配置
	},
	"zone": {
		区域渲染配置
	},
	"team": {
		队伍渲染配置
	},
	"spectator": {
		观战渲染配置
	}
}
```

### 方块渲染配置

- itemRenderDistance：物资刷新方块渲染物品的最大距离
- renderItemBlockIfEmpty：当物资刷新方块为空时是否渲染方块本身
- entityRenderDistance：实体刷新方块最大渲染距离
> 计算距离时取玩家脚下位置，而不是摄像机位置
```json
"block": {
	"itemRenderDistance": 16.0,
	"renderItemBlockIfEmpty": true,
	"entityRenderDistance": 16.0
}
```

### 区域渲染配置

- useClientColor：所有区域使用固定颜色渲染，不影响透明度
- fixedColor：_(useClientColor为"true"时可用)_ 区域颜色，格式 _#RRGGBB_
- circleSegments：圆形渲染边数
- ellipseSegments：椭圆渲染边数
- sphereSegments：球体赤道的渲染边数
- ellipsoidSegments：椭球赤道的渲染边数
> - 边数均默认为64，不小于32
> - 边数越高越平滑，但设置为1024时已经有显著性能差异
> - 边数设置仅本地生效，不影响区域的判定范围
```json
"zone": {
	"useClientColor": true,
	"fixedColor": "#0000FF",
	"circleSegments": 64,
	"ellipseSegments": 64,
	"sphereSegments": 64,
	"ellispoidSegments": 64
}
```

### 队伍渲染配置

- enableTeamZone：是否启用队友区域渲染
- useClientColor：所有队伍区域使用固定颜色渲染，不影响透明度
- fixedColor：_(useClientColor为"true"时可用)_ 队伍区域颜色，格式 _#RRGGBB_
- beacon：渲染队友头顶信标
- boundingBox：渲染队友碰撞箱
- transparency：队伍区域的透明度，范围[0, 1]
```json
"team": {
	"enableTeamZone": true,
	"useClientColor": true,
	"fixedColor": "#00FFFF",
	"headBeacon": true,
	"boundingBox": true,
	"transparency": 0.5
}
```

### 观战渲染配置

- enableSpectateZone：是否启用观战渲染
- useClientColor：所有观战区域使用固定颜色渲染，不影响透明度
- fixedColor：_(useClientColor为"true"时可用)_ 观战区域颜色，格式 _#RRGGBB_
- beacon：渲染观战玩家头顶信标
- boundingBox：渲染观战玩家碰撞箱
- transparency：观战区域的透明度，范围[0, 1]
- scanFrequency：扫描周围观战玩家的频率，单位tick
```json
"spectate": {
	"enableSpectateZone": true,
	"useClientColor": true,
	"fixedColor": "#00FFFF",
	"headBeacon": true,
	"boundingBox": true,
	"transparency": 0.5,
	"scanFrequency": 60
}
```

# English

## Single render config

- id: unique render id
- name: name the config, can be repeated
- color: no function for now
```json
{
	"id": 0,
	"name": "No limit",
	"color": "#FFFFFFAA",
	"block": {
		BLOCK RENDER CONFIG
	},
	"zone": {
		ZONE RENDER CONFIG
	},
	"team": {
		TEAM RENDER CONFIG
	},
	"spectate": {
		SPECTATE RENDER CONFIG
	}
}
```

### Block render config

- itemRenderDistance: The furthest distance at which items within loot block are rendered
- renderItemBlockIfEmpty: Whether to render loot block itself if it's empty
- entityRenderDistance: Maximum render distance of entity spawner block
> When calculating distance, take the player's foot position instead of the camera position
```json
"block": {
	"itemRenderDistance": 16.0,
	"renderItemBlockIfEmpty": true,
	"entityRenderDistance": 16.0
}
```

### Zone render config

- useClientColor: All zones are rendered with fixed colors, has no impact on transparency
- fixedColor：_(available when useClientColor is "true")_ Zone color, format _\#RRGGBB_
- circleSegments: Number of segments for rendering circles
- ellipseSegments: Number of segments for rendering ellipses
- sphereSegments: Number of segments for rendering the equator of a sphere
- ellipsoidSegments: Number of segments for rendering the equator of an ellipsoid
> - All segment counts default to 64 and must be no less than 32
> - Higher segment counts result in a smoother appearance, but a noticeable performance difference can be seen at 1024
> - The segment count setting is client-side only and does not affect the zone's detection area
```json
"zone": {
	"useClientColor": true,
	"fixedColor": "#0000FF",
	"circleSegments": 64,
	"ellipseSegments": 64,
	"sphereSegments": 64,
	"ellispoidSegments": 64
}
```

### Team render config

- enableTeamZone: Whether to enable teammate zone rendering
- useClientColor: All team zones are rendered with fixed colors, has no impact on transparency
- fixedColor：_(available when useClientColor is "true")_ Team zone color, format _\#RRGGBB_
- beacon: Renders beacons above teammates' head
- boundingBox: Renders teammates' bounding box
- transparency: Team zone transparency, range [0, 1]
```json
"team": {
	"enableTeamZone": true,
	"useClientColor": true,
	"fixedColor": "#00FFFF",
	"headBeacon": true,
	"boundingBox": true,
	"transparency": 0.5
}
```

### Spectator render config

- enableSpectateZone: Whether to enable spectator rendering
- useClientColor: All spectator zones are rendered with fixed colors, has no impact on transparency
- fixedColor: _(available when useClientColor is "true")_ Spectator zone color, format _#RRGGBB_
- beacon: Renders beacons above spectator game players' head
- boundingBox: Renders spectator game players' bounding box
- transparency: Spectator zone transparency, range [0, 1]
- scanFrequency: Frequency of scanning for nearby spectator game players, in ticks
```json
"spectate": {
	"enableSpectateZone": true,
	"useClientColor": true,
	"fixedColor": "#00FFFF",
	"headBeacon": true,
	"boundingBox": true,
	"transparency": 0.5,
	"scanFrequency": 60
}
```