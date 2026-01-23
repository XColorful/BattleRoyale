### 简单功能词条

#### 安全区

形状范围外周期毒伤，默认1秒1次
- zoneFuncType："safe"
- damage：单次造成的区域伤害量
```json
"zoneFunc": {
	"zoneFuncType": "safe",
	"damage": 1.0,
	"moveDelay": 600,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1
}
```

#### 不安全区

与安全区的区别仅为形状范围内周期毒伤
- zoneFuncType："unsafe"
- damage：单次造成的区域伤害量
```json
"zoneFunc": {
	"zoneFuncType": "unsafe",
	"damage": 1.0,
	"moveDelay": 600,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1
}
```

#### 烟花区

等价于[烟花指令](https://github.com/XColorful/BattleRoyale/wiki/Firework-command)
- zoneFuncType："firework"
- trackPlayer：烟花生成是否持续追踪玩家，设置为false则在玩家触发烟花区域的位置继续生成
- outside：若设置为true，则将对区域内的玩家生成烟花
```json
"zoneFunc": {
	"zoneFuncType": "firework",
	"moveDelay": 600,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1,
	"trackPlayer": true,
	"amount": 1,
	"interval": 20,
	"verticalRange": 5,
	"horizontalRange": 3,
	"outside": false
}
```

#### 无敌区

等价于[无敌指令](https://github.com/XColorful/BattleRoyale/wiki/Muteki-command)
大厅无敌效果优先于无敌区
- zoneFuncType："muteki"
```json
"zoneFunc": {
	"zoneFuncType": "muteki",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"mutekiTime": 200
}
```

#### 能量区

等价于[能量指令](https://github.com/XColorful/BattleRoyale/wiki/Boost-command)
- zoneFuncType："boost"
```json
"zoneFunc": {
	"zoneFuncType": "boost",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"boost": 80
}
```

#### 粒子区

等价于[粒子指令](https://github.com/XColorful/BattleRoyale/wiki/Particle-command)，但用游戏专用通道并追踪玩家生成粒子
- zoneFuncType："particle"
- particles：粒子配置唯一id列表，见[粒子配置](https://github.com/XColorful/BattleRoyale/wiki/Particle-config#单一配置)
- selectCount：从粒子列表中重复随机选取的次数
- channel：粒子生成任务的通道名称，相同通道共享通道冷却
- cooldown：通道冷却
> 第一个粒子生成失败将导致后续粒子不生成，即使可能是缺失配置文件导致的
```json
"zoneFunc": {
	"zoneFuncType": "particle",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"particles": [
		1,
		1,
		2
	],
	"selectCount": 1,
	"channel": "zone11",
	"cooldown": 50
}
```
```java
// 第一个粒子检测是否在冷却  
int selected = (int) (size * random.get());
if (!EffectManager.get().addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0)) {
    continue;
}
for (int i = 1; i < this.select - 1; i++) {
    selected = (int) (size * random.get());
    EffectManager.get().addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0);
}
// 最后一个粒子添加冷却
selected = (int) (size * random.get());
EffectManager.get().addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, cooldown);
```

#### 效果区

- zoneFuncType："effect"
- effects：效果列表
- type：效果类型
- duration：效果持续时长
- level：效果等级
```json
"zoneFunc": {
	"zoneFuncType": "effect",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"effects": [
		{
			"type": "minecraft:speed",
			"duration": 20,
			"level": 1
		},
		{
			"type": "minecraft:jump_boost",
			"duration": 20,
			"level": 1
		}
	]
}
```

#### 消息区

- zoneFuncType："message"
- setTitleAnimation：是否发送设置标题动画包
- fadeInTicks：文本淡入时长
- stayTicks：文本停留时长
- fadeOutTicks：文本淡出时长
- sendTitles：是否发送标题
- title：标题Json字符串
- subtitle：副标题Json字符串
- sendActionBar：是否发送状态条
- actionBar：状态条Json字符串
```json
"zoneFunc": {
	"zoneFuncType": "message",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"setTitleAnimation": true,
	"fadeInTicks": 10,
	"stayTicks": 80,
	"fadeOutTicks": 20,
	"sendTitle": true,
	"title": "{\"bold\":true,\"text\":\"§6Game Start\"}",
	"subTitle": "{\"text\":\"\"}",,
	"sendActionBar": true,
	"actionBar": "{\"color\":\"blue\",\"text\":\"Good luck\"}"
}
```

#### 背包区

- zoneFuncType："inventory"
- skipNonEmptySlot：是否跳过非空槽位
- dropBeforeReplace：如果即将填充的槽位非空，是否先吐出原物品
- firstSlotIndex：第一个放置的槽位
- lastSlotIndex：最后一个允许放置的槽位
> - 快捷键槽（从左往右）：[0, 8]
> - 物品存放槽（从上往下，从左往右）：[9, 17]，[18, 26]，[27, 35]
> - 盔甲槽（从靴子到头盔）：[36, 39]
> - 副手槽：[40, 40]
- lootEntry：[通用刷新配置](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config)的刷新词条
- lootSpawnerLootId：物资刷新器[物资刷新配置](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#物资刷新配置)唯一id，额外刷新物品
```json
"zoneFunc": {
	"zoneFuncType": "inventory",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"skipNonEmptySlot": false,
	"dropBeforeReplace": false,
	"firstSlotIndex": 36,
	"lastSlotIndex": 39,
	"lootEntry": {
		"lootType": "multi",
		"entries": [
			{
				"lootType": "empty",
				"type": "item"
			},
			{
				"lootType": "empty",
				"type": "item"
			},
			{
				"lootType": "item",
				"item": "minecraft:elytra",
				"count": 1,
				"nbt": "{Damage:332}"
			},
			{
				"lootType": "item",
				"item": "minecraft:iron_hemlet",
				"count": 1,
				"nbt": ""
			}
		]
	},
	"lootSpawnerLootId": -1
}
```

#### 无功能区

- zoneFuncType："noFunc"
```json
"zoneFunc": {
	"zoneFuncType": "noFunc",
	"moveDelay": 0,
	"moveTime": 0
}
```

# English

### Simple function entry

- moveDelay: delay in area movement, the movement path is a straight line segment between two points
- moveTime: duration of area movement, 0 means immediate movement to the end position
- tickFrequency: zone function trigger frequency
- tickOffset: zone function trigger delay, set to -1 to randomly select [0, tickFrequency)

#### Safe zone

Periodic zone damage outside the shape range, default is once per second
- zoneFuncType："safe"
- damage: the amount of zone damage caused in a single time
```json
"zoneFunc": {
	"zoneFuncType": "safe",
	"damage": 1.0,
	"moveDelay": 600,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1
}
```

#### Unsafe zone

The only difference from the safe zone is the periodic zone damage within the shape range
- zoneFuncType："unsafe"
- damage: the amount of zone damage caused in a single time
```json
"zoneFunc": {
	"zoneFuncType": "unsafe",
	"damage": 1.0,
	"moveDelay": 600,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1
}
```

#### Firework zone

Equivalent to [firework command](https://github.com/XColorful/BattleRoyale/wiki/Firework-command#English)
- zoneFuncType: "firework"
- trackPlayer: Whether the fireworks will continue to track the player. If set to false, the fireworks will continue to be generated at the location where the player triggers the fireworks zone
- outside: If set to true, fireworks will be generated for players in the zone
```json
"zoneFunc": {
	"zoneFuncType": "firework",
	"moveDelay": 600,
	"moveTime": 1200,
	"tickFrequency": 20,
	"tickOffset": -1,
	"trackPlayer": true,
	"amount": 1,
	"interval": 20,
	"verticalRange": 5,
	"horizontalRange": 3,
	"outside": false
}
```

#### Muteki zone

Equivalent to [Muteki time](https://github.com/XColorful/BattleRoyale/wiki/Muteki-command#English)
The lobby muteki effect takes precedence over the muteki zone
- zoneFuncType: "muteki"
```json
"zoneFunc": {
	"zoneFuncType": "muteki",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"mutekiTime": 200
}
```

#### Boost zone

Equivalent to [Boost effect](https://github.com/XColorful/BattleRoyale/wiki/Boost-command#English)
- zoneFuncType: "boost"
```json
"zoneFunc": {
	"zoneFuncType": "boost",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"boost": 80
}
```

#### Particle zone

Equivalent to [Particle command](https://github.com/XColorful/BattleRoyale/wiki/Particle-command#English), but use game special channel and track game player to generate particles
- zoneFuncType: "particle"
- particles: A list of particle configuration IDs. See [Particle config](https://github.com/XColorful/BattleRoyale/wiki/Particle-config#Single-particle-config).
- selectCount: The number of times particles are randomly selected from the _particles_ list.
- channel: The name of the channel for particle generation tasks. Channels with the same name share a cooldown.
- cooldown: The cooldown duration for the _channel_
> If the first particle fails to generate, subsequent particles will not be generated, even if the failure is due to a missing configuration file
```json
"zoneFunc": {
	"zoneFuncType": "particle",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"particles": [
		1,
		1,
		2
	],
	"selectCount": 1,
	"channel": "zone11",
	"cooldown": 50
}
```
```java
// 第一个粒子检测是否在冷却  
int selected = (int) (size * random.get());
if (!EffectManager.get().addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0)) {
    continue;
}
for (int i = 1; i < this.select - 1; i++) {
    selected = (int) (size * random.get());
    EffectManager.get().addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0);
}
// 最后一个粒子添加冷却
selected = (int) (size * random.get());
EffectManager.get().addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, cooldown);
```

#### Effect zone

- zoneFuncType: "effect"
- effects: a list of effect configurations
- type: the type of the effect
- duration: the duration of the effect
- level: the level of the effect
```json
"zoneFunc": {
	"zoneFuncType": "effect",
	"moveDelay": 200,
	"moveTime": 400,
	"tickFrequency": 20,
	"tickOffset": -1,
	"effects": [
		{
			"type": "minecraft:speed",
			"duration": 20,
			"level": 1
		},
		{
			"type": "minecraft:jump_boost",
			"duration": 20,
			"level": 1
		}
	]
}
```

#### Message zone

- zoneFuncType: "message"
- setTitleAnimation: Whether to send the title animation packet
- fadeInTicks: text fade-in duration
- stayTicks: text stay duration
- fadeOutTicks: text fade-out duration
- sendTitles: Whether to send the title packet
- title: title Json string
- subtitle：subtitle Json string
- sendActionBar: Whether to send the action bar
- actionBar: action bar Json string
```json
"zoneFunc": {
	"zoneFuncType": "message",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"setTitleAnimation": true,
	"fadeInTicks": 10,
	"stayTicks": 80,
	"fadeOutTicks": 20,
	"sendTitles": true,
	"title": "{\"bold\":true,\"text\":\"§6Game Start\"}",
	"subTitle": "{\"text\":\"\"}",,
	"sendActionBar": true,
	"actionBar": "{\"color\":\"blue\",\"text\":\"Good luck\"}"
}
```

#### Inventory zone

- zoneFuncType: "inventory"
- skipNonEmptySlot: Whether to skip non-empty slots.
- dropBeforeReplace: If the slot to be filled is not empty, whether to drop the original item first.
- firstSlotIndex: The index of the first slot where items can be placed.
- lastSlotIndex: The index of the last slot where items are allowed to be placed.
> - Hotbar slots (left to right): [0, 8]
> - Storage slots (top to bottom, left to right) : [9, 17]，[18, 26]，[27, 35]
> - Armor slots (boots to helmet) : [36, 39]
> - Off-hand slot: [40, 40]
- lootEntry: Loot entry from the [General loot config](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#English)
- lootSpawnerLootId: The unique id of the loot spawner [Loot config](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#Loot)，for additional loot generation
```json
"zoneFunc": {
	"zoneFuncType": "inventory",
	"moveDelay": 0,
	"moveTime": 20,
	"tickFrequency": 20,
	"tickOffset": -1,
	"skipNonEmptySlot": false,
	"dropBeforeReplace": false,
	"firstSlotIndex": 36,
	"lastSlotIndex": 39,
	"lootEntry": {
		"lootType": "multi",
		"entries": [
			{
				"lootType": "empty",
				"type": "item"
			},
			{
				"lootType": "empty",
				"type": "item"
			},
			{
				"lootType": "item",
				"item": "minecraft:elytra",
				"count": 1,
				"nbt": "{Damage:332}"
			},
			{
				"lootType": "item",
				"item": "minecraft:iron_hemlet",
				"count": 1,
				"nbt": ""
			}
		]
	},
	"lootSpawnerLootId": -1
}
```

#### No function zone

- zoneFuncType: "noFunc"
```json
"zoneFunc": {
	"zoneFuncType": "noFunc",
	"moveDelay": 0,
	"moveTime": 0
}
```