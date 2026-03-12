[English](#English)

# 实用指令
_/battleroyale utility [survivallobby/tosurvivallobby/lootconfig/profile/team] [id/save/load/remove/rebuild] [type/id/...] [.../overwrite]_

## 生存模式大厅
_/battleroyale utility [survivallobby/tosurvivallobby]_

### 生存模式大厅信息
_/battleroyale utility survivallobby_

- 查看大厅坐标
- 查看大厅规模
- 查看大厅是否启用无敌
- 查看大厅是否启用治疗

### 传送至生存模式大厅
_/battleroyale utility tosurvivallobby_

- 若无法通过[生存模式大厅](https://github.com/XColorful/BattleRoyale/wiki/Utility-config#生存模式大厅)的 _levelKey_ 获取到维度，则不传送
- 若[生存模式大厅](https://github.com/XColorful/BattleRoyale/wiki/Utility-config#生存模式大厅)禁用 _allowGamePlayerTeleport_ ，则需要先[离开队伍](https://github.com/XColorful/BattleRoyale/wiki/Team-command#离开队伍)

## 生成配置文件
_/battleroyale utility [lootconfig] [id] [type] [...]_

### 生成物资刷新配置文件
_/battleroyale utility lootconfig [id] [slot/block/chunk] [xyz] [repeat] [baseWeight] [chunkRadius] [autoReload]_

_/battleroyale utility lootconfig [xyz] [id] [slot/block/chunk] [xyz] [repeat] [baseWeight] [chunkRadius] [autoReload]_

需要权限等级3

新建[物资刷新器配置文件](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#物资刷新配置)：
- id：刷新配置唯一id
- type：以单个物品槽/方块/区块为刷新单位
- repeat：[重复刷新](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#重复刷新)次数
- baseWeight：[加权刷新](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#加权刷新)默认权重
- chunkRadius：区块扫描半径（0表示仅当前区块）
- autoReload：生成完配置后自动执行[重载物资刷新器配置](https://github.com/XColorful/BattleRoyale/wiki/Reload-command#物资刷新器)，并[切换物资刷新配置文件](https://github.com/XColorful/BattleRoyale/wiki/Config-command#物资刷新配置)

以单个物品槽为单位：
- _/battleroyale utility lootconfig ~ ~ ~ 0 slot 1 1 0 true_
```json
[
	{
		"lootId": 0,
		"name": "lootConfig-20260128_110543-slot",
		"color": "#FFFFFFAA",
		"entry": {
			"lootType": "repeat",
			"min": 1,
			"max": 1,
			"entry": {
				"lootType": "weight",
				"entries": [
					{
						"weight": 1.0,
						"entry": {
							"lootType": "item",
							"item": "minecraft:grass_block",
							"count": 64,
							"nbt": "{}"
						}
					},
					{
						"weight": 1.0,
						"entry": {
							// ...
						}
					},
					{
						"weight": 1.0,
						"entry": {
							// ...
						}
					}
					// ...
				]
			}
		}
	}
]
```

以单个方块/区块槽为单位：
- _/battleroyale utility lootconfig ~ ~ ~ 0 block 1 1 0 true_
- _/battleroyale utility lootconfig ~ ~ ~ 0 chunk 1 1 0 true_
```json
[
	{
		"lootId": 0,
		"name": "lootConfig-20260128_110743-block",
		"color": "#FFFFFFAA",
		"entry": {
			"lootType": "repeat",
			"min": 1,
			"max": 1,
			"entry": {
				"lootType": "weight",
				"entries": [
					{
						"weight": 1.0,
						"entry": {
							"lootType": "multi",
							"entries": [
								{
									"lootType": "item",
									"item": "minecraft:grass_block",
									"count": 64,
									"nbt": "{}"
								},
								{
									"lootType": "item",
									"item": "minecraft:enchanted_golden_apple",
									"count": 64,
									"nbt": "{}"
								},
								{
									// ...
								}
							]
						}
					},
					{
						"weight": 1.0,
						"entry": {
							"lootType": "multi",
							"entries": [
								{
									// ...
								}
							]
						}
					}
				]
			}
		}
	}
]
```

## 预设配置
_/battleroyale utility profile [save/load] [id] [overwrite]_

### 保存配置预设
_/battleroyale utility profile save [id] [overwrite]_

将当前配置保存至 _./minecraft/config/battleroyale/server/profile_ 下
- id：预设配置唯一id
- overwrite：是否保存在当前配置下

### 应用预设配置
_/battleroyale utility profile load [id]_

自动切换至预设的配置文件及选用配置
- id：预设配置唯一id

## 原版队伍
_/battleroyale utility team [remove/rebuild] [...]_

### 移除原版队伍
_/battleroyale utility team remove [gameTeamOnly]_

移除所有原版队伍，等价于批量执行 _/team remove_
- gameTeamOnly：是否仅移除当前游戏队伍对应的原版队伍
> _/team remove_ 无法选中带空格的队伍名

### 重建原版队伍
_/battleroyale utility team rebuild [formatString] [hideName] [forceRebuild]_

重新为游戏队伍组建原版队伍：
- formatString：同[游戏配置](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#游戏配置)`vanillaTeamFormat`
- hideName：是否隐藏名称
- forceRebuild：是否允许在游戏中重建
- 该操作会先清空对应的原版队伍，并过滤掉不在指令执行维度内的游戏玩家
> 原版队伍不影响游戏队伍判定

# English
_/battleroyale utility [survivallobby/tosurvivallobby/lootconfig/profile/team] [id/save/load/remove/rebuild] [type/id/...] [.../overwrite]_

## Survival mode lobby
_/battleroyale utility [survivallobby/tosurvivallobby]_

### Survival mode lobby info
_/battleroyale utility survivallobby_

- Check lobby coordinates
- Check lobby size
- Check whether invulnerable is enabled in the lobby
- Check whether heal is enabled in the lobby

### Teleport to survival mode lobby
_/battleroyale utility tosurvivallobby_

- If the dimension cannot be obtained via the _levelKey_ in [Survival mode lobby](https://github.com/XColorful/BattleRoyale/wiki/Utility-config#Survival-mode-lobby), teleportation will not occur
- If _allowGamePlayerTeleport_ is disabled in [Survival mode lobby](https://github.com/XColorful/BattleRoyale/wiki/Utility-config#Survival-mode-lobby), you must first [Leave the team](https://github.com/XColorful/BattleRoyale/wiki/Team-command#Leave-the-team)

## Generate configuration file
_/battleroyale utility [lootconfig] [id] [type] [...]_

### Generate loot spawner configuration
_/battleroyale utility lootconfig [id] [slot/block/chunk] [xyz] [repeat] [baseWeight] [chunkRadius] [autoReload]_

_/battleroyale utility lootconfig [xyz] [id] [slot/block/chunk] [xyz] [repeat] [baseWeight] [chunkRadius] [autoReload]_

Require permission level 3

Create a new [Loot spawner config](https://github.com/XColorful/BattleRoyale/wiki/Configuration-introduction#Loot):
- id: unique loot id
- type: Units of generation (Single Slot / Block / Chunk).
- repeat: Number of times for [Repeat loot](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#Repeat-loot).
- baseWeight: Default weight for [Weighted loot](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#Weighted-loot).
- chunkRadius: Radius for scanning chunks (0 means only the current chunk).
- autoReload: Automatically execute [Reload Loot spawner config](https://github.com/XColorful/BattleRoyale/wiki/Reload-command#Loot-spawner) and [Switch Loot spawner config](https://github.com/XColorful/BattleRoyale/wiki/Config-command#Loot-config) after generation.

By Single Item Slot:
- _/battleroyale utility lootconfig ~ ~ ~ 0 slot 1 1 0 true_
- _/battleroyale utility lootconfig ~ ~ ~ 0 slot 1 1 0 true_
```json
[
	{
		"lootId": 0,
		"name": "lootConfig-20260128_110543-slot",
		"color": "#FFFFFFAA",
		"entry": {
			"lootType": "repeat",
			"min": 1,
			"max": 1,
			"entry": {
				"lootType": "weight",
				"entries": [
					{
						"weight": 1.0,
						"entry": {
							"lootType": "item",
							"item": "minecraft:grass_block",
							"count": 64,
							"nbt": "{}"
						}
					},
					{
						"weight": 1.0,
						"entry": {
							// ...
						}
					},
					{
						"weight": 1.0,
						"entry": {
							// ...
						}
					}
					// ...
				]
			}
		}
	}
]
```

By Block or Chunk:
- _/battleroyale utility lootconfig ~ ~ ~ 0 block 1 1 0 true_
- _/battleroyale utility lootconfig ~ ~ ~ 0 chunk 1 1 0 true_
```json
[
	{
		"lootId": 0,
		"name": "lootConfig-20260128_110743-block",
		"color": "#FFFFFFAA",
		"entry": {
			"lootType": "repeat",
			"min": 1,
			"max": 1,
			"entry": {
				"lootType": "weight",
				"entries": [
					{
						"weight": 1.0,
						"entry": {
							"lootType": "multi",
							"entries": [
								{
									"lootType": "item",
									"item": "minecraft:grass_block",
									"count": 64,
									"nbt": "{}"
								},
								{
									"lootType": "item",
									"item": "minecraft:enchanted_golden_apple",
									"count": 64,
									"nbt": "{}"
								},
								{
									// ...
								}
							]
						}
					},
					{
						"weight": 1.0,
						"entry": {
							"lootType": "multi",
							"entries": [
								{
									// ...
								}
							]
						}
					}
				]
			}
		}
	}
]
```

## Profile config
_/battleroyale utility profile [save/load] [id] [overwrite]_

### Save config profile
_/battleroyale utility profile save [id] [overwrite]_

Save the current configuration to _./minecraft/config/battleroyale/server/profile_
- id: unique profile id
- overwrite: Whether to overwrite the current configuration

### Apply profile config
_/battleroyale utility profile load [id]_

Automatically switch to the preset configuration file and apply the selected configuration
- id: unique profile id

## Vanilla team
_/battleroyale utility team [remove/rebuild] [...]_

### Remove vanilla team
_/battleroyale utility team remove [gameTeamOnly]_

Remove all vanilla teams, equivalent to executing _/team remove_ in batches:
- gameTeamOnly: Whether to only remove the vanilla teams corresponding to the current game teams.
> _/team remove_ cannot select team names containing spaces.

### Rebuild vanilla team
_/battleroyale utility team rebuild [formatString] [hideName] [forceRebuild]_

Rebuild vanilla teams for game teams:
- formatString: Same as `vanillaTeamFormat` in [Game config](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#Game-config).
- hideName: Whether to hide names.
- forceRebuild: Whether to allow rebuilding during the game.
- This operation will first clear the corresponding vanilla teams and filter out game players who are not within the dimension where the command is executed.
> Vanilla teams do not affect the determination of game teams.