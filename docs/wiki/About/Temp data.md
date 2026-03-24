[English](#English)

# 临时数据

临时数据位于 _./minecraft/battleroyale/temp_ 下的所有json文件

- 模组的临时数据独立于Minecraft的存档数据，全存档统一
- 所有临时数据均可以删除，通常不需要手动修改

## 功能类

### 注册项
_./minecraft/battleroyale/temp/registry.json_

- entitySelector：是否[启用实体选择器](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#启用实体选择器)
- selector.\*：是否[启用实体选择器类型](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#启用实体选择器类型)
- pubgmcCommand：是否启用[PUBGMC兼容](https://github.com/XColorful/BattleRoyale/wiki/PUBGMC-compatibility)
```json
{
	"entitySelector": true,
	"selector.gameplayers": true,
	"selector.nongameplayers.player": true,
	"selector.gameplayers.player": true,
	"selector.gameplayers.bot": true,
	"selector.gameplayers.downed": true,
	"selector.standinggameplayers": true,
	"selector.nonstandinggameplayers.player": true,
	"selector.standinggameplayers.player": true,
	"selector.standinggameplayers.bot": true,
	"selector.eliminatedgameplayers": true,
	"selector.eliminatedgameplayers.player": true,
	"selector.eliminatedgameplayers.bot": true,
	"pubgmcCommand": true
}
```

### 机制项
_./minecraft/battleroyale/temp/feature.json_

> 该功能已于 0.5.1 移至[TaCZ护甲缩放](https://github.com/XColorful/TaCZ-Armor-Scaling)
```json
{
	"taczBulletHandler": true
}
```

## 配置类

### 预计算
_./minecraft/battleroyale/temp/preCalculate.json_

- spawnManager：预计算[双圆心网格分布传送](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config#双圆心网格分布传送)结果
> - 预计算[_CircleGridStartN_, _CircleGridEndN_]范围的输入N
> - 预计算 _CircleGridNList_ 的所有N
> - 计算[1, 100]的时间开销小于1 tick，计算[1, 1000]的时间开销小于3 ticks
> - showDebugResult为true时会在debug.log里显示所有预计算点位
```json
{
	"spawnManager": {
		"CircleGridStartN": 1,
		"CircleGridEndN": 51,
		"CircleGridNList": [100, 101, 1000, 1001],
		"showDebugResult": false
	}
}
```

## 读取类
该列别的临时数据相当于写入Minecraft存档数据并读取，但本模组并没有这么做，因此有了读取类临时数据，通常不需要手动修改

### 游戏管理器
_./minecraft/battleroyale/temp/gameManager.json_

- globalOffset：大逃杀游戏使用的[区域偏移](https://github.com/XColorful/BattleRoyale/wiki/Game-command#区域偏移)
- lastGameId：游戏管理器使用的游戏ID，物资刷新生成的物品及实体会附带该游戏ID
```json
{
	"globalOffset": "0.500000,0.000000,-8191.500000",
	"lastGameId": "7f2311a8-a277-4ab5-a59e-0341302375da"
}
```

# English

Temporary data directory located in all json files under _./minecraft/battleroyale/temp_

- The mods' temporary data is separate from Minecraft's save data, all saves are unified
- All temporary data is deletable and usually doesn't require manual editing

## Function category

### Registry entry
_./minecraft/battleroyale/temp/registry.json_

- entitySelector: whether to [Enable entity selector](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#Enable-entity-selector)
- selector.\*: whether to [Enable entity selector type](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#Enable-entity-selector-type)
- pubgmcCommand: Enables/disables [PUBGMC compatibility](https://github.com/XColorful/BattleRoyale/wiki/PUBGMC-compatibility#English)
```json
{
	"entitySelector": true,
	"selector.gameplayers": true,
	"selector.nongameplayers.player": true,
	"selector.gameplayers.player": true,
	"selector.gameplayers.bot": true,
	"selector.gameplayers.downed": true,
	"selector.standinggameplayers": true,
	"selector.nonstandinggameplayers.player": true,
	"selector.standinggameplayers.player": true,
	"selector.standinggameplayers.bot": true,
	"selector.eliminatedgameplayers": true,
	"selector.eliminatedgameplayers.player": true,
	"selector.eliminatedgameplayers.bot": true,
	"pubgmcCommand": true
}
```

### Feature entry
_./minecraft/battleroyale/temp/feature.json_

> This feature was moved to [TaCZ Armor Scaling](https://github.com/XColorful/TaCZ-Armor-Scaling) in version 0.5.1
```json
{
	"taczBulletHandler": true
}
```

## Configuration category

### Pre-Calculation
_./minecraft/battleroyale/temp/preCalculate.json_

- spawnManager: Configures the pre-calculation of the [Double center grid distribution teleport](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config#Double-center-grid-distribution-teleport) points.
> - Calculates the input N grid points within the range [_CircleGridStartN_, _CircleGridEndN_]
> - Pre-calculates all N values in _CircleGridNList_
> - Calculation time cost for [1, 100] is less than 1 tick, and for [1, 1000] is less than 3 ticks
> - If _showDebugResult_ is true, all pre-calculated points will be displayed in debug.log
```json
{
	"spawnManager": {
		"CircleGridStartN": 1,
		"CircleGridEndN": 51,
		"CircleGridNList": [100, 101, 1000, 1001],
		"showDebugResult": false,
	}
}
```

## Read-Only data
This category's temporary data is similar to writing to and reading from Minecraft's save data, but this mod doesn't do that.  Therefore, this read-only data usually doesn't need manual modification.

### Game Manager
_./minecraft/battleroyale/temp/gameManager.json_

- globalOffset: The [Zone offset](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Zone-offset) used by the BattleRoyale game.
- lastGameId: The game ID used by the Game Manager. Items and entities generated by loot generation will carry this game ID.
```json
{
	"globalOffset": "0.500000,0.000000,-8191.500000",
	"lastGameId": "7f2311a8-a277-4ab5-a59e-0341302375da"
}
```