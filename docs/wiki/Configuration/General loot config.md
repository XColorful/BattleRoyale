[English](#English)

# 通用刷新配置

lootId：
- 同一文件夹下的配置不应该用相同的lootId，重复则覆盖配置
- 通常默认lootId为0
- 原版箱子使用默认lootId

## 单个配置

- lootId：刷新配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
- entry：刷新词条

```json
{
	"lootId": 0,
	"name": "Custom loot config",
	"color": "#FFFFFFAA",
	"entry": {
		刷新词条
	}
}
```

## 刷新词条类型

根据是否处于最内层可分为**控制刷新词条**和**实际刷新词条**

### 控制刷新词条

模组默认生成的配置文件 _./minecraft/config/battleroyale/loot/loot_spawner/example.json_ 列举了所有控制刷新词条用法

不参与实际物品/实体刷新

#### 多个刷新

依次处理每一个刷新词条
```json
{
	"lootType": "multi",
	"entries": [
		{
			刷新词条
		},
		{
			刷新词条
		},
		{
			...
		}
	]
}
```

#### 随机刷新

随机判定是否处理刷新词条，概率范围使用[0,1]
```json
{
	"lootType": "random",
	"chance": 0.5,
	"entry": {
		刷新词条
	}
}
```

#### 重复刷新

在[min, max]范围内随机取一个整数n，重复处理n次刷新词条
```json
{
	"lootType": "repeat",
	"min": 0,
	"max": 5,
	"entry": {
		刷新词条
	}
}
```

#### 时间刷新

处理刷新前，大逃杀游戏时间在[start, end]范围内时，允许处理刷新词条
```json
{
	"lootType": "time",
	"start": 200,
	"end": 12000,
	"entry": {
		刷新词条
	}
}
```

#### 加权刷新

加权选取单个刷新词条处理，每个词条被选中的概率为：_该刷新词条权重 / 全部权重之和_
```json
{
	"lootType": "weight",
	"entries": [
		{
			"weight": 20.0,
			"entry": {
				刷新词条
			},
			"weight": 80.0,
			"entry": {
				刷新词条
			},
			{
				...
			}
		}
	]
}
```

#### 限量刷新

依次处理每一个刷新词条，刷新数在[min, max]范围内时刷新，否则无刷新
- countEmpty：空刷新是否加入计数
- countLootTime：计数标准使用刷新次数，否则使用实际刷新数量
- keepEmpty：是否保留空刷新
```json
{
	"lootType": "bound",
	"countEmpty": false,
	"countLootTime": true,
	"min": 1,
	"max": 2,
	"keepEmpty": false,
	"entries": [
		{
			刷新词条
		},
		{
			刷新词条
		},
		{
			...
		}
	]
}
```

#### 额外刷新

检查的刷新不为无刷新则额外刷新
- countEmpty：空刷新是否算作刷新
- keepCheck：是否保留检查的刷新
- check：检查的刷新
- extra：额外刷新
```json
{
	"lootType": "extra",
	"countEmpty": false,
	"keepCheck": false,
	"check": {
		刷新词条
	},
	"extra": {
		刷新词条
	}
}
```

#### 打乱刷新

随机打乱内部刷新后随机选取[min,max]个刷新
- keepEmpty：是否选取空刷新
```json
{
	"lootType": "shuffle",
	"keepEmpty": false,
	"min": 0,
	"max": 2,
	"entry": {
		刷新词条
	}
}
```

#### 清理刷新

移除所有空刷新
```json
{
	"lootType": "clean",
	"entry": {
		刷新词条
	}
}
```

#### 群系刷新

刷新方块位于指定群系时不刷新
- invert：是否将列表作为白名单
- filter：群系列表
```json
{
	"lootType": "biome",
	"invert": false,
	"filter": [
		"minecraft:plains"
	],
	"entry": {
		刷新词条
	}
}
```

#### 建筑刷新

刷新方块处在/不在特定建筑内则刷新
- invert：是否将列表作为白名单
- filter：建筑列表
> 注意：村庄范围为村庄房子内部
```json
{
	"lootType": "structure",
	"invert": true,
	"filter": [
		"minecraft:village_plains"
	],
	"entry": {
		刷新词条
	}
}
```

#### 正则刷新

刷新方块的NBT符合正则表达式则刷新
- invert：反转判定逻辑
- regex：正则表达式字符串
```json
{
	"lootType": "regex",
	"invert": false,
	"regex": "id:\"minecraft:chest\"",
	"entry": {
		刷新词条
	}
}
```
> 方块的NBT字符串形如：
> ```
> "{ForgeData:{},Items:[],id:"minecraft:chest",x:0,y:0,z:0}"
> ```
> 如果需要精准匹配方块，避免容器内有容器方块，考虑使用 _"id:\\"minecraft:chest\\",x:"_

#### 形状刷新

刷新方块在区域范围内则刷新
- invert：是否反转判定区域
- shapeEntry：同[区域形状词条](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域形状词条)
> - 使用游戏管理器的维度，区域进度为 _当前游戏时长_ / _最大游戏时长_，若无法创建区域则判定为在区域外
> - 若不在大逃杀游戏中，则选取全体游戏玩家，反之选取未被淘汰的游戏玩家
```json
{
	"lootType": "shape",
	"invert": false,
	"shapeEntry": {
		"zoneShapeType": 区域形状类型
		区域形状属性
	},
	"entry": {
		刷新词条
	}
}
```

### 实际刷新词条（最内层）

#### 物品刷新

- item：物品ID
- count：物品堆叠数
- nbt：物品NBT数据
```json
{
	"lootType": "item",
	"item": "minecraft:leather_helmet",
	"count": 1,
	"nbt": "{Damage:27}"
}
```

#### 空刷新

在刷新容器内留下一个空位，对实体刷新无实际效果
> 实际是生成一个无名称的物品/实体，配合控制刷新词条使用
- type：指定空刷新类型为物品"item"或实体"entity"
```json
{
	"lootType": "empty",
	"type": "item"
}
```

#### 实体刷新

- entity：实体ID
- count：刷新数量，每次刷新位置独立
- nbt：实体NBT数据
- range：允许偏离初始刷新点的范围
- attempts：寻找随机点位的次数
> 在0.4.3以前，模组会尝试寻找随机点位4次，往x、z方向随机偏移[-range, range]，当4次尝试均失败时生成在初始刷新点处
```json
{
	"lootType": "entity",
	"entity": "minecraft:zombie",
	"count": 5,
	"nbt": "",
	"range": 20,
	"attempts": 4
}
```

#### 无刷新

什么都不做
> 通常用于配合加权刷新词条在配置文件中占位
```json
{
	"lootType": "none"
}
```

### 功能刷新词条（最内层）

该类别刷新词条会立即执行其功能

#### 消息刷新

发送消息至聊天栏
- onlyGamePlayer：是否仅向游戏玩家发送
- sendPosition：是否发送坐标（作为前缀）
- message：发送的字面消息
- messageColor：发送消息的颜色，格式为 _\#RRGGBB_
```json
{
	"lootType": "message",
	"onlyGamePlayer": false,
	"sendPosition": true,
	"message": "稀有物资刷新！",
	"messageColor": "#FF0000"
}
```

#### 傀儡刷新

立即执行的[实体刷新](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#实体刷新)，可用于任意方块实体
> 该刷新词条设计为用在箱子（或任意方块实体）周围刷新实体，使绝大多数原版建筑（不含实体刷新方块）也能刷新实体
- entity：实体ID
- count：刷新数量，每次刷新位置独立
- nbt：实体NBT数据
- range：允许偏离初始刷新点的范围
- attempts：寻找随机点位的次数
```json
{
	"lootType": "golem",
	"entity": "minecraft:copper_golem",
	"count": 5,
	"nbt": "",
	"range": 20,
	"attempts": 4
}
```

#### 通用事件刷新

立即发送CustomGenerateEvent，供其他模组开发者监听事件
- protocol：自定义协议名称，建议优先通过该字符串筛选要处理的事件
- jsonTag：自定义数据
> UUID类型请**不要**使用 CompoundTag#putUUID()。应使用 **CompoundTag#putString(key, UUID#toString())** 写入，并用 **UUID.fromString(CompoundTag#getString(key))** 读取。
```json
{
	"lootType": "event",
	"protocol": "cbr:0.4.3",
	"jsonTag": {
		"description": "Create event for other mod to subscribe",
		"boolTrue": true,
		"boolFalse": false,
		"float": 0.333,
		"double": 0.88888888,
		"long": 4294967294,
		"int": 666666,
		"short": 25565,
		"randomUUID": "399f2f82-ea9a-46d4-8737-41d5fb405559",
		"tagInTag": {
			"additional data": "some structured data"
		}
	}
}
```

#### 标签刷新
暂未实现

修改方块的NBT标签
> LootTable标签只能写入**字符串**，否则无法覆盖
```json
{
	"lootType": "nbt",
	"overwrite": false,
	"nbt": {
		NBT 标签
	},
	"keyDelete": [
		"GameId",
		"ConfigId"
	]
}
```

#### 区域生成
暂未实现

生成指定区域
- offset：临时偏移
- addGlobalOffset：是否将全局偏移加至临时偏移
- alterOffset：是否将临时偏移保留至全局偏移
- zone：同[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config)
```json
{
	"lootType": "zone",
	"offset": "",
	"addGlobalOffset": true,
	"alterOffset": false,
	"zone": {
		"zoneId": 1000,
		"zoneName": "",
		...
	}
}
```

# English

lootId：
- Configurations in the same folder shouldn't use the same lootId, overrite if duplicate
- Usually the default lootId is 0
- Vanilla chest uses the default lootId

## Single loot config

- lootId: unique loot id
- name: name the config, can be repeated
- color: no function for now
- entry: loot entry

```json
{
	"lootId": 0,
	"name": "Custom loot config",
	"color": "#FFFFFFAA",
	"entry": {
		LOOT ENTRY
	}
}
```

## Loot entry type

According to whether it is in the innermost nesting, it can be classified into **control loot entry** and **actual loot entry**.

### Control loot entry

Configuration file generated by the mod by default _./minecraft/config/battleroyale/loot/loot_spawner/example.json_ lists all the usages of control loot entries.

Does not participate in the actual item/entity loot.

#### Multiple loot

Process each loot entry in turn.
```json
{
	"lootType": "multi",
	"entries": [
		{
			LOOT ENTRY
		},
		{
			LOOT ENTRY
		},
		{
			...
		}
	]
}
```

#### Random loot

Randomly determine whether to process the loot entry, the probability range uses [0,1].
```json
{
	"lootType": "random",
	"chance": 0.5,
	"entry": {
		LOOT ENTRY
	}
}
```

#### Repeat loot

Randomly select an integer N in the range [min, max], and repeatedly process the refresh entry N times.
```json
{
	"lootType": "repeat",
	"min": 0,
	"max": 5,
	"entry": {
		LOOT ENTRY
	}
}
```

#### Time loot

Before processing loot, when the BattleRoyale game time is within the range of [start, end], it is allowed to process the loot entry.
```json
{
	"lootType": "time",
	"start": 200,
	"end": 12000,
	"entry": {
		LOOT ENTRY
	}
}
```
#### Weighted loot

Weighted selection of a single loot entry for processing, the probability of each entry being selected is: _The weight of the loot entry / the sum of all weights_.
```json
{
	"lootType": "weight",
	"entries": [
		{
			"weight": 20.0,
			"entry": {
				LOOT ENTRY
			},
			"weight": 80.0,
			"entry": {
				LOOT ENTRY
			},
			{
				...
			}
		}
	]
}
```

#### Bound loot

Process each loot entry in turn, loot when the loot count is within the range of [min, max], otherwise result in None loot.
- countEmpty: whether Empty loot should add to count
- countLootTime: Use the loot times as the counting standard, otherwise use the actual loot quantity
- keepEmpty: Whether to keep Empty loot
```json
{
	"lootType": "bound",
	"countEmpty": false,
	"countLootTime": true,
	"min": 1,
	"max": 2,
	"keepEmpty": false,
	"entries": [
		{
			LOOT ENTRY
		},
		{
			LOOT ENTRY
		},
		{
			...
		}
	]
}
```

#### Extra loot

If the checked loot is not None loot, generate additional loot.
- countEmpty: whether empty loot is counted as loot
- keepCheck: whether to keep the checked loot
- check: loot to be checked
- extra: additional loot
```json
{
	"lootType": "extra",
	"countEmpty": false,
	"keepCheck": false,
	"check": {
		LOOT ENTRY
	},
	"extra": {
		LOOT ENTRY
	}
}
```

#### Shuffle loot

Randomly shuffle the internal loot and select [min, max] loots.
- keepEmpty: Whether to select Empty loot
```json
{
	"lootType": "shuffle",
	"keepEmpty": false,
	"min": 0,
	"max": 2,
	"entry": {
		LOOT ENTRY
	}
}
```

#### Clean loot

Remove all Empty loot
```json
{
	"lootType": "clean",
	"entry": {
		LOOT ENTRY
	}
}
```

#### Biome loot

Generate loot only if the block is/isn't in specified biomes.
- invert: Whether to treat the list as a whitelist.
- filter: A list of biome
```json
{
	"lootType": "biome",
	"invert": false,
	"filter": [
		"minecraft:plains"
	],
	"entry": {
		LOOT ENTRY
	}
}
```

#### Structure loot

Generate loot if the block is inside/outside a specified structure.
- invert: Whether to treat the list as a whitelist.
- filter: A list of structures.
> Note: The village range is the interior of the village house
```json
{
	"lootType": "structure",
	"invert": true,
	"filter": [
		"minecraft:village"
	],
	"entry": {
		LOOT ENTRY
	}
}
```

#### Regex loot

Generate loot if the block's NBT data matches a given regular expression.
- invert: Negate the result
- regex: The regular expression string to match against the block's NBT data.
```json
{
	"lootType": "regex",
	"invert": false,
	"regex": "id:\"minecraft:chest\"",
	"entry": {
		LOOT ENTRY
	}
}
```
> The NBT string of a block looks like:
> ```
> "{ForgeData:{},Items:[],id:"minecraft:chest",x:0,y:0,z:0}"
> ```
> If you need precise block matching and want to avoid container blocks within containers, consider using _"id:\\"minecraft:chest\\",x:"_

#### Shape loot

Generate loot if the block is inside/outside a specified area.
- invert: Whether to reverse the judgment area
- shapeEntry: Same as [Zone shape entry](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-shape-entry)
```json
{
	"lootType": "shape",
	"invert": false,
	"shapeEntry": {
		"zoneShapeType": ZONE SHAPE TYPE
		ZONE SHAPE PROPERTY
	},
	"entry": {
		LOOT ENTRY
	}
}
```

### Actual loot entry (innermost nesting)

#### Item loot

- item: item ID
- count: number of item stacks
- nbt: item NBT data

```json
{
	"lootType": "item",
	"item": "minecraft:leather_helmet",
	"count": 1,
	"nbt": "{Damage:27}"
}
```

#### Empty item loot

Leave an empty slot in the loot container, and have no actual effect on entity loot
> Actually generates an unnamed item/entity, used with the control loot entry
- type: specifies the empty loot type as "item" or "entity"
```json
{
	"lootType": "empty",
	"type": "item"
}
```

#### Entity loot

- entity: entity ID
- count: loot count, each loot position is independent
- nbt: entity NBT data
- range: the range allowed to deviate from the initial loot point
- attempts: number of attempts to find a random position
> Before 0.4.3, the mod would try to find a random position 4 times, with a random offset of [-range, range] in the x and z directions. If all 4 attempts failed, the entity was spawned at the initial loot point.
```json
{
	"lootType": "entity",
	"entity": "minecraft:zombie",
	"count": 5,
	"nbt": "",
	"range": 20,
	"attempts": 4
}
```

#### None loot

Do nothing.
> Usually used to take up space in the config file with weighted loot entry.
```json
{
"lootType": "none"
}
```

### Function loot entry (innermost nesting)

#### Message loot

Sends message to chat
- onlyGamePlayer: Whether to send only to game players
- sendPosition: Whether to send coordinates (as prefix)
- message: the literal message to send
- messageColor: the color of the message, format _\#RRGGBB_
```json
{
	"lootType": "message",
	"onlyGamePlayer": false,
	"sendPosition": true,
	"message": "Rare loot generated!",
	"messageColor": "#FF0000"
}
```

#### Golem loot

An immediately executed [Entity loot](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#Entity-loot), applicable to any block entity.
> This loot entry is designed to spawn entities around chests (or any block entity), allowing most vanilla structures (that don't contain entity spawner block) to also spawn entities.
- entity: entity ID
- count: loot count, each loot position is independent
- nbt: entity NBT data
- range: the range allowed to deviate from the initial loot point
- attempts: number of attempts to find a random position
```json
{
	"lootType": "golem",
	"entity": "minecraft:copper_golem",
	"count": 5,
	"nbt": "",
	"range": 20,
	"attempts": 4
}
```

#### Common event loot

Immediately send CustomGenerateEvent, for other mod developers to listen for them.
- protocol: A custom protocol name. It's recommended to use this string to filter events to be processed.
- jsonTag: Custom data
> For UUID types, **do not** use CompoundTag#putUUID(). Instead, use **CompoundTag#putString(key, UUID#toString())** to write and **UUID.fromString(CompoundTag#getString(key))** to read.
```json
{
	"lootType": "event",
	"protocol": "cbr:0.4.3",
	"jsonTag": {
		"description": "Create event for other mod to subscribe",
		"boolTrue": true,
		"boolFalse": false,
		"float": 0.333,
		"double": 0.88888888,
		"long": 4294967294,
		"int": 666666,
		"short": 25565,
		"randomUUID": "399f2f82-ea9a-46d4-8737-41d5fb405559",
		"tagInTag": {
			"additional data": "some structured data"
		}
	}
}
```

#### NBT loot
Not implemented yet.

Modify the NBT tag of a block
> The LootTable tag can only be written with a **string**, otherwise it cannot be overwritten
```json
{
	"lootType": "nbt",
	"overwrite": false,
	"nbt": {
		NBT tag
	},
	"keyDelete": [
		"GameId",
		"ConfigId"
	]
}
```

#### Zone loot
Not implemented yet.

Generate a specified zone
- offset: Temporary offset
- addGlobalOffset: Whether to add the current global offset to the temporary offset
- alterOffset: Whether to apply the temporary offset to the global offset
- zone: Same as [Zone ocnfig](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English)
```json
{
	"lootType": "zone",
	"offset": "",
	"addGlobalOffset": true,
	"alterOffset": false,
	"zone": {
		"zoneId": 1000,
		"zoneName": "",
		...
	}
}
```