[English](#English)

# 实用配置

## 单个配置

- id：显示配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
- survival：生存模式功能词条
```json
{
	"id": 0,
	"name": "Overworld Survival",
	"color": "#FFFFFF",
	"survival": {
		生存模式功能
	}
}
```

## 生存模式功能

- survivalLobby：生存模式大厅
```json
"survival": {
	"survivalLobby": {
		生存模式大厅
	}
}
```

### 生存模式大厅

- levelKey：大厅所在的世界维度
- allowGamePlayerTeleport：是否允许未被淘汰的游戏玩家传送至大厅
- lobbyCenter：大厅坐标
- lobbyDimension：大厅的规模，以lobbyCenter为中心向x、y、z正负方向扩展，最终为长方体
- lobbyMuteki：大厅内是否无敌，对所有玩家生效
- lobbyHeal：传送至大厅是否自动补满血量
- lobbyChangeGamemode：传送至大厅是否修改成生存模式
- dropInventory：是否在传送至大厅时吐出玩家背包物品
- dropGameItemOnly：是否仅吐出物资刷新器刷新的物品
- clearInventory：是否在传送至大厅时清除玩家背包
- clearGameItemOnly：是否仅清除物资刷新器刷新的物品
```json
"survivalLobby": {
	"levelKey": "minecraft:overworld",
	"allowGamePlayerTeleport": false,
	"lobbyCenter": "0.0,70.0,0.0",
	"lobbyDimension": "8.0,160.0,8.0",
	"lobbyMuteki": false,
	"lobbyHeal": false,
	"lobbyChangeGamemode": true,
	"dropInventory": true,
	"dropGameItemOnly": true,
	"clearInventory": true,
	"clearGameItemOnly": true,
}
```

# English

## Single Configuration

- id: unique performance id
- name: name the config, can be repeated
- color: no function for now
- survival: survival utility entry
```json
{
	"id": 0,
	"name": "Overworld Survival",
	"color": "#FFFFFF",
	"survival": {
		SURVIVAL UTILITY
	}
}
```

## Survival Utility

- survivalLobby: Survival mode lobby.
```json
"survival": {
	"survivalLobby": {
		SURVIVAL MODE LOBBY
	}
}
```

### Survival mode lobby

- levelKey: the dimension where the lobby is located
- allowGamePlayerTeleport: Whether non-eliminated game players can teleport to the lobby
- lobbyCenter: lobby coordinates
- lobbyDimension: the size of the lobby, which expands in the positive and negative directions of x, y, and z with lobbyCenter as the center, and finally becomes a rectangular block
- lobbyMuteki: whether player is invulnerable in lobby, this applies to all players
- lobbyHeal: Whether to automatically replenish health when teleporting to the lobby
- lobbyChangeGamemode: Whether to change the gamemode to survival mode when teleporting to the lobby
- dropInventory: Whether to drop items in the player's inventory when teleporting to the lobby
- dropGameItemOnly: Whether to only drop generated loot item
- clearInventory: Whether to clear the player's inventory when teleporting to the lobby
- clearGameItemOnly: Whether to only clear generated loot item
```json
"survivalLobby": {
	"levelKey": "minecraft:overworld",
	"allowGamePlayerTeleport": false,
	"lobbyCenter": "0.0,70.0,0.0",
	"lobbyDimension": "8.0,160.0,8.0",
	"lobbyMuteki": false,
	"lobbyHeal": false,
	"lobbyChangeGamemode": true,
	"dropInventory": true,
	"dropGameItemOnly": true,
	"clearInventory": true,
	"clearGameItemOnly": true,
}
```