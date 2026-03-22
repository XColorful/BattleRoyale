[English](#English)

# 性能配置

## 单个配置

- id：显示配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
- generator：物资刷新器词条
```json
{
	"id": 0,
	"name": "Default performance",
	"color": "#FFFFFF",
	"generator": {
		物资刷新器词条
	}
}
```

## 物资刷新器

- common：公共设置
- normal：指令刷新设置
- game：游戏刷新设置
```json
"generator": {
	"common": {
		公共设置
	},
	"normal": {
		指令刷新设置
	},
	"game": {
		游戏刷新设置
	}
}
```

### 公共设置

- lootAnyBlockEntity：是否对非容器方块实体也执行物资刷新
- lootVanillaChest：是否刷新原版箱子
- removeLootTable：是否移除原版战利品刷新
- clearPreviousContent：是否在刷新前清空物品
- removeNoGameidEntity：是否清除未添加游戏ID的掉落物/实体
- vanillaWhiteList：正则表达式匹配物品 _注册名称_ ，如不为空，则必须匹配其中一项才执行刷新
- vanillaBlackList：正则表达式匹配物品 _注册名称_ ，任意匹配一项则不执行刷新
> 注册名称由 _命名空间_ 和 _路径_ 组成
```json
"common": {
	"lootAnyBlockEntity": false,
	"lootVanillaChest": true,
	"removeLootTable": false,
	"clearPreviousContent": true,
	"removeNoGameidEntity": false,
	"vanillaWhiteList": [
		"minecraft:chest" // 箱子
		"minecraft:barrel" // 木桶
		"minecraft:(chest|barrel)" // 一次性匹配箱子和木桶
		"minecraft:.*" // 匹配所有原版方块
		".*example.*" // 匹配注册名称中包含"example"的所有
	],
	"vanillaBlackList": [
		"minecraft:furnace" // 熔炉
	]
}
```

### 指令刷新设置

- maxNormalTickLootChunk：每tick最多刷新的区块数
```json
"normal": {
	"maxNormalTickLootChunk": 1000
}
```

### 游戏刷新设置

- maxGameTickLootChunk：每tick最多刷新的区块数，范围[5, 100000]
- maxLootDistance：最远刷新区块的曼哈顿距离，范围[3, 128]
- bfsTolerantCenterDistance：中心容忍距离，使玩家在上次刷新位置一定范围内不立即完整刷新，范围[0, 10]
- maxCachedCenter：缓存记录的中心数量，达到上限自动清理30%，范围[0, 50000]
- maxQueuedChunk：最大待处理区块数，范围[100, 200000]
- bfsFrequency：完整刷新的频率，范围[100, 2147483647]
- instantNextBfs：在bfsFrequency内刷新完成，是否立即开始下一次刷新
- maxCachedLootChunk：缓存记录的刷新区块数量，范围[100, 300000]
- cleanCachedChunk：清理记录的刷新区块的数量，范围[10, 10000]
```json
"game": {
	"maxGameTickLootChunk": 1000,
	"maxLootDistance": 20,
	"bfsTolerantCenterDistance": 3,
	"maxCachedCenter": 1000,
	"maxQueuedChunk": 50000,
	"bfsFrequency": 100,
	"instantNextBfs": false,
	"maxCachedLootChunk": 100000,
	"cleanCachedChunk": 10000
}
```

# English

## Single Configuration

- id: unique performance id
- name: name the config, can be repeated
- color: no function for now
- generator: loot generator entry
```json
{
	"id": 0,
	"name": "Default performance",
	"color": "#FFFFFF",
	"generator": {
		LOOT GENERATOR ENTRY
	}
}
```

## Loot Generator

- common: general settings
- normal: settings for command-based refreshing
- game: settings for in-game refreshing
```json
"generator": {
	"common": {
		GENERAL SETTINGS
	},
	"normal": {
		COMMAND LOOT SETTINGS
	},
	"game": {
		IN-GAME LOOT SETTINGS
	}
}
```


### General Settings

- lootAnyBlockEntity: Whether to perform loot generation on non-container block entities.
- lootVanillaChest: Whether to loot vanilla chests.
- removeLootTable: Whether to remove vanilla loot table.
- clearPreviousContent: whether to clear items before loot.
- removeNoGameidEntity: Whether to clear dropped items or entities that do not have a game ID.
- vanillaWhiteList: A list of regular expressions for matching item _registry names_. If not empty, a block must match at least one entry to be looted.
- vanillaBlackList: A list of regular expressions for matching item _registry names_. If a block matches any entry, it will not be looted.
> A registry name consists of a _namespace_ and a _path_.
```json
"common": {
	"lootAnyBlockEntity": false,
	"lootVanillaChest": true,
	"removeLootTable": false,
	"clearPreviousContent": true,
	"removeNoGameidEntity": false,
	"vanillaWhiteList": [
		"minecraft:chest" // Chest
		"minecraft:barrel" // Barrel
		"minecraft:(chest|barrel)" // Matches both chests and barrels at once
		"minecraft:.*" // Matches all vanilla blocks
		".*example.*" // Matches all registry names containing "example"
	],
	"vanillaBlackList": [
		"minecraft:furnace" // Furnace
	]
}
```

### Command loot Settings

- maxNormalTickLootChunk: The maximum number of chunks to loot per tick.
```json
"normal": {
	"maxNormalTickLootChunk": 1000
}
```

### In-game loot Settings

- maxGameTickLootChunk: The maximum number of chunks to loot per tick, with a range of [5, 100000]
- maxLootDistance: The maximum Manhattan chunk distance for generation, with a range of [3, 128]
- bfsTolerantCenterDistance: The tolerant center distance for BFS, which prevents a full refresh for players within a certain range of their last refresh location, with a range of [0, 10]
- maxCachedCenter: The maximum number of cached center chunks. When the limit is reached, 30% are automatically cleared. The range is [0, 50000]
- maxQueuedChunk: The maximum number of chunks in the queue waiting to be processed, with a range of [100, 200000]
- bfsFrequency: The frequency of a full generation, with a range of [100, 2147483647]
- instantNextBfs: Determines whether to start the next loot immediately if the current one finishes within the bfsFrequency
- maxCachedLootChunk: The maximum number of processed chunks to cache, with a range of [100, 300000]
- cleanCachedChunk: The number of cached chunks to remove during cleanup, with a range of [10, 10000].
```json
"game": {
	"maxGameTickLootChunk": 1000,
	"maxLootDistance": 20,
	"bfsTolerantCenterDistance": 3,
	"maxCachedCenter": 1000,
	"maxQueuedChunk": 50000,
	"bfsFrequency": 100,
	"instantNextBfs": false,
	"maxCachedLootChunk": 100000,
	"cleanCachedChunk": 10000
}
```