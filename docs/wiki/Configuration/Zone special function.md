[English](#English)

### 特殊功能词条

特殊功能区域均有相关的事件，供其他模组开发者监听事件
- protocol：自定义协议名称，建议优先通过该字符串筛选要处理的事件
- jsonTag：自定义数据
> UUID类型请**不要**使用 CompoundTag#putUUID()。应使用 **CompoundTag#putString(key, UUID#toString())** 写入，并用 **UUID.fromString(CompoundTag#getString(key))** 读取。

#### 通用事件区

该区域功能仅为发送CustomZoneEvent，供其他模组开发者监听事件
- zoneFuncType："event"
> 默认情况下，CustomZoneEvent.getTag()和CustomZoneEvent.getNbt()均返回tag
```json
"zoneFunc": {
	"zoneFuncType": "event",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"protocol": "cbr:0.3.8",
	"jsonTag": {
		"description": "Create event for other mod to subscribe",
		"boolTrue": true,
		"boolFalse": false,
		"float": 0.333,
		"double": 0.88888888,
		"long": 4294967294,
		"int": 666666,
		"short": 25565,
		"randomUUID": "c46a3681-5057-4173-acb5-7156b12e7b00"
	}
}
```

#### 补给箱区

- zoneFuncType："airdrop"
- lootId：使用的补给箱物资刷新配置
- nbt：为单个实体提供的NBT数据
> 请手动获取并写入GameId以避免在刷新物资时被清除
> - 获取GameId：AirdropEvent.getGameManager().getGameId()
> - 写入GameId：AirdropEvent.getGameManager().getGameIdWriteApi()#addGameId
```json
"zoneFunc": {
	"zoneFuncType": "airdrop",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"protocol": "cbr:airdrop",
	"jsonTag": {
	},
	"lootId": 0,
	"nbt": "{}"
}
```

#### 实体刷新区

- zoneFuncType："entity"
- lootId：使用的实体刷新配置
- nbt：为单个实体提供的NBT数据
> 事件结束后会统一写入GameId并生成实体，提前生z成实体需手动获取并写入GameId以避免在刷新物资时被清除
> - 获取GameId：AirdropEvent.getGameManager().getGameId()
> - 写入GameId：EntityEvent.getGameManager().getGameIdWriteApi()#addGameId
```json
"zoneFunc": {
	"zoneFuncType": "entity",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"protocol": "cbr:entity",
	"jsonTag": {
	},
	"lootId": 0,
	"nbt": "{}"
}
```

#### 强制刷新区
暂未实现

强制在区域内再次处理物资刷新

#### 撤离区
暂未实现

提供特殊胜利条件

#### 占领区
暂未实现

提供特殊胜利条件

# English

### Zone special function

Special function zones all have related events for other mod developers to listen for them.
- protocol: A custom protocol name. It's recommended to use this string to filter events to be processed.
- jsonTag: Custom data
> For UUID types, **do not** use CompoundTag#putUUID(). Instead, use **CompoundTag#putString(key, UUID#toString())** to write and **UUID.fromString(CompoundTag#getString(key))** to read.

#### Common event zone

This zone's function is only for sending CustomZoneEvent, allowing other mod developers to listen for them.
- zoneFuncType: "event"
> By default, both CustomZoneEvent.getTag() and CustomZoneEvent.getNbt() return tag
```json
"zoneFunc": {
	"zoneFuncType": "event",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"protocol": "cbr:0.3.8",
	"jsonTag": {
		"description": "Create event for other mod to subscribe",
		"boolTrue": true,
		"boolFalse": false,
		"float": 0.333,
		"double": 0.88888888,
		"long": 4294967294,
		"int": 666666,
		"short": 25565,
		"randomUUID": "c46a3681-5057-4173-acb5-7156b12e7b00"
	}
}
```

#### Airdrop zone

- zoneFuncType: "airdrop"
- lootId: The loot configuration used for airdrop
- nbt: NBT data provided for a single entity
> Manually get and write the GameId to avoid it being cleared during a loot generation:
> - Get the GameId: AirdropEvent.getGameManager().getGameId()
> - Write the GameId: AirdropEvent.getGameManager().getGameIdWriteApi()#addGameId
```json
"zoneFunc": {
	"zoneFuncType": "airdrop",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"protocol": "cbr:airdrop",
	"jsonTag": {
	},
	"lootId": 0,
	"nbt": "{}"
}
```

#### Entity loot zone

- zoneFuncType: "entity"
- lootId: The loot configuration used for entity spawner
- nbt: NBT data provided for a single entity
> The GameId will be uniformly written and the entity generated after the event ends. To generate the entity in advance, you need to manually get and write the GameId to avoid it being cleared during loot refresh:
> - Get the GameId: AirdropEvent.getGameManager().getGameId()
> - Write the GameId: AirdropEvent.getGameManager().getGameIdWriteApi()#addGameId
```json
"zoneFunc": {
	"zoneFuncType": "entity",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"protocol": "cbr:entity",
	"jsonTag": {
	},
	"lootId": 0,
	"nbt": "{}"
}
```

#### Forced loot zone
Not yet implemented

Forced to process loot generation again in the area

#### Extraction zone
Not yet implemented

Provide special victory conditions

#### Capture zone
Not yet implemented

Provide special victory conditions