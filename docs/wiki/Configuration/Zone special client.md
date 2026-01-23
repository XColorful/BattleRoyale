### 特殊客户端功能

#### 附加渲染

该特殊功能仅为发送SpecialZoneRenderEvent，供其他模组开发者监听事件
- zoneSpecialType："render"
- protocol：自定义协议名称，建议优先通过该字符串筛选要处理的事件
- jsonTag：自定义数据
```json
"zoneSpecial": {
	"zoneSpecialType": "render",
	"protocol": "cbr:0.4.6",
	"jsonTag": {
	}
}
```

##### 方向渲染

本模组内置的区域特殊词条扩展，从玩家位置指向区域中心渲染一个长方体
- hideInSpectate：是否在旁观模式下隐藏
- heightOffset：竖直方向偏移距离
- side：长方体的宽/高
> 长方体中心与玩家脚底在同一平面，_heightOffset_ 为 _side / 2_ 时长方体底面与玩家脚底共面
- 3dDistance：是否计算玩家位置到区域中心的三维距离，若为false则取在XZ平面上的投影
- distDim：取区域维度的x/y/z分量为基础距离，若不为"x"、"y"、"z"则为0
- distMul：基础距离缩放比例
- distAdd：使固定加上固定距离
> 若 _距离_ ≤ _distDim_ * _distMul_ + _distAdd_，则在范围内
- innerColor：在范围内时的颜色，留空则取区域颜色，格式 _\#RRGGBBAA_
- outsideColor：在范围外时的颜色，留空则取区域颜色，格式为 _\#RRGGBBAA_
```json
"zoneSpecial": {
	"zoneSpecialType": "render",
	"protocol": "cbr:direct",
	"jsonTag": {
		"hideInSpectate": true,
		"heightOffset": 0.25,
		"side": 0.5,
		"3dDistance": false,
		"distDim": "x",
		"distMul": 1.0,
		"distAdd": 0.0,
		"innerColor": "#00FF0011"
		// outsideColor留空，自动取区域颜色及其透明度
	}
}
```

# English

### Special client function

#### Additional render

This special function is only for sending SpecialZoneRenderEvent, allowing other mod developers to listen for them.
- zoneSpecialType: "render"
- protocol: A custom protocol name. It's recommended to use this string to filter events to be processed.
- jsonTag: Custom data
```json
"zoneSpecial": {
	"zoneSpecialType": "render",
	"protocol": "cbr:0.4.6",
	"jsonTag": {
	}
}
```

##### Direction render

This is an extension to the zone special entry built into this mod, which renders a cuboid from the player's position pointing towards the center of the zone.
- hideInSpectate: Whether to hide in spectator mode
- heightOffset: Vertical offset distance
- side: The width/height of the cuboid
> The center of the cuboid is on the same plane as the player's foot position. When _heightOffset_ is _side / 2_, the bottom surface of the cuboid is coplanar with the player's foot position.
- 3dDistance: Whether to calculate the three-dimensional distance from the player's position to the center of the zone. If false, the projection on the XZ plane is used.
- distDim: Takes the x/y/z component of the zone dimension as the base distance. If it is not "x", "y", or "z", it is 0.
- distMul: Scaling ratio for the base distance
- distAdd: Adds a fixed distance
> If _distance_ ≤ _distDim_ * _distMul_ + _distAdd_, then it is within the range.
- innerColor: Color when within the range. If left blank, the zone color will be used. Format: _\#RRGGBBAA_
- outsideColor: Color when outside the range. If left blank, the zone color will be used. Format: _\#RRGGBBAA_
```json
"zoneSpecial": {
	"zoneSpecialType": "render",
	"protocol": "cbr:direct",
	"jsonTag": {
		"hideInSpectate": true,
		"heightOffset": 0.25,
		"side": 0.5,
		"3dDistance": false,
		"distDim": "x",
		"distMul": 1.0,
		"distAdd": 0.0,
		"innerColor": "#00FF0011"
		// outsideColor left blank, automatically takes the zone color and its transparency
	}
}
```