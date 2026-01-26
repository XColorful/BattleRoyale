[English](#English)

# 显示配置

## 单个配置

- id：显示配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
```json
{
	"id": 0,
	"name": "All display",
	"color": "#FFFFFFAA",
	"team": {
		队伍信息显示
	},
	"game": {
		游戏信息显示
	},
	"map": {
		地图信息显示
	}
}
```

## HUD显示

- display：是否显示该HUD
- xRatio：从屏幕中心往右的半屏长比例
- yRatio：从屏幕中心往上的半屏长比例
> 以屏幕中心为原点建平面直角坐标系，右上角为(xRatio=1, yRatio=1)，左下角为(xRatio=-1, yRatio=-1)

### 队伍信息显示

- offlineTime：经过多久没收到服务端消息时更改渲染颜色
```json
"team": {
	"display": true,
	"xRatio": -0.9,
	"yRatio": -0.9,
	"offlineTime": 100
}
```

### 游戏信息显示

- aliveColor：“生存”的显示颜色，格式#RRGGBBAA
- aliveCountColor：总生存玩家数的显示颜色，格式#RRGGBBAA
```json
"game": {
	"display": true,
	"xRatio": 0.85,
	"yRatio": 0.9,
	"aliveColor": "#FFFFFFFF",
	"aliveCountColor": "#00FFFFFF"
}
```

## 世界显示

### 地图信息显示

- enableJourneyMap：是否在[JourneyMap](https://teamjm.github.io/journeymap-docs/5.9.x/)上显示区域
```json
"map": {
	"enableJourneyMap": true
}
```

# English

## Single render config

- id: unique display id
- name: name the config, can be repeated
- color: no function for now
```json
{
	"id": 0,
	"name": "No limit",
	"color": "#FFFFFFAA",
	"team": {
		TEAM INFO DISPLAY
	},
	"game": {
		GAME INFO DISPLAY
	},
	"map": {
		MAP INFO DISPLAY
	}
}
```

## HUD display

- display: whether to display the HUD
- xRatio: the half-screen length ratio from the center of the screen to the right
- yRatio: the half-screen length ratio from the center of the screen to the top
> Use the center of the screen as the origin to build a plane rectangular coordinate system, with the upper right corner (xRatio=1, yRatio=1) and the lower left corner (xRatio=-1, ​​yRatio=-1)

### Team info display

- offlineTime: how long it takes to change the rendering color after not receiving a message from the server
```json
"team": {
	"display": true,
	"xRatio": -0.9,
	"yRatio": -0.9,
	"offlineTime": 100
}
```

### Game info display

- aliveColor: the display color of "Alive", format \#RRGGBBAA
- aliveCountColor: the display color of the total number of alive players, format \#RRGGBBAA
```json
"game": {
	"display": true,
	"xRatio": 0.85,
	"yRatio": 0.9,
	"aliveColor": "#FFFFFFFF",
	"aliveCountColor": "#00FFFFFF"
}
```

## World display

### Map info display

- enableJourneyMap: Whether to display zone on [JourneyMap](https://teamjm.github.io/journeymap-docs/5.9.x/)
```json
"map": {
	"enableJourneyMap": true
}
```