[English](#English)

# 游戏规则配置

## 单个配置

- gameId：大逃杀游戏配置唯一id
- gameName：为该配置命名，可重复
- color：暂时没有功能
- battleroyale：大逃杀规则配置
- minecraft：原版规则配置
- game：（可选）游戏配置
- extraRule：（可选）扩展规则配置
```json
{
	"gameId": 0,
	"gameName": "Custom BattleRoyale gamerule",
	"color": "#FFFFFFAA",
	"battleroyale": {
		大逃杀规则配置
	},
	"minecraft": {
		原版规则配置
	},
	"game": {
		游戏配置
	},
	"extraRule": {
		扩展规则配置
	}
}
```

### 大逃杀规则配置

- defaultLevelKey：未加载游戏时的初始游戏维度
- playerTotal：玩家+人机总数
- teamSize：队伍最大人数
- aiTeammate：是否允许人机队友
- aiEnemy：是否允许人机敌人
- requiredTeamToStart：开始大逃杀游戏需要的队伍数量
- maxGameTime：最大游戏时长
- winnerTeamTotal：获胜队伍总数，队伍数量小于等于该值时大逃杀游戏结束
- lobbyCenter：大厅坐标
- lobbyDimension：大厅的规模，以lobbyCenter为中心向x、y、z正负方向扩展，最终为长方体
- lobbyMuteki：大厅内是否无敌，对未被淘汰的玩家无效
- lobbyHeal：传送至大厅是否自动补满血量
- lobbyChangeGamemode：传送至大厅是否修改成默认游戏模式
- lobbyTeleportDropInventory：传送至大厅是否吐出背包物品（在 _lobbyTeleportClearInventory_ 之前）
- lobbyTeleportClearInventory：传送至大厅是否清空背包
- recordGameStats：是否记录大逃杀游戏统计数据
- autoJoinGame：是否在游戏初始化后自动加入游戏（加入任意队伍，如无法创建新队伍则改为发送邀请）
```json
"battleroyale": {
	"defaultLevelKey": "minecraft:overworld",
	"playerTotal": 100,
	"teamSize": 4,
	"aiTeammate": true,
	"aiEnemy": true,
	"requiredTeamToStart": 2,
	"maxGameTime": 12000,
	"winnerTeamTotal": 1,
	"lobbyCenter": "128.0,-60.0,128.0",
	"lobbyDimension": "10.0,10.0,10.0",
	"lobbyMuteki": true,
	"lobbyHeal": true,
	"lobbyChangeGamemode": true,
	"lobbyTeleportDropInventory": false,
	"lobbyTeleportClearInventory": false,
	"recordGameStats": true,
	"autoJoinGame": true
},
```

### 原版规则配置

- adventureMode：是否以冒险模式开始大逃杀游戏，false则使用生存模式
- autoSaturation：每10秒自动补充饱和度
- clearInventoryAtStart：是否在游戏开始时清除游戏玩家背包
- doTimeSet：是否使 _timeSet_ 生效
- timeSet：等价于游戏开始时执行一次 _/time set [int]_
- 其余选项等价于原版指令 _/gamerule 配置 [true/false]_
> 注：0.4.7及以前，"tntExplosionDropDecay"是"tntExplodes"
```json
"minecraft": {
	"adventureMode": true,
	"mobGriefing": false,
	"autoSaturation": true,
	"naturalRegeneration": false,
	"doMobSpawning": false,
	"doFireTick": false,
	"doDaylightCycle": false,
	"doWeatherCycle": false,
	"fallDamage": false,
	"tntExplosionDropDecay": false,
	"spectatorGenerateChunks": false,
	"clearInventoryAtStart": true,
	"keepInventory": false,
	"doImmediateRespawn": false,
	"doTimeSet": true,
	"timeSet": 5000
}
```

### 游戏配置

- teleportWhenInitGame：在[初始化游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#初始化游戏)时将所有游戏玩家传送至大厅
- teamMessageExpireSeconds：[申请入队](https://github.com/XColorful/BattleRoyale/wiki/Team-command#申请入队)/[邀请入队](https://github.com/XColorful/BattleRoyale/wiki/Team-command#邀请入队)消息的过期时长
- teamColors：队伍序号颜色列表，格式为 _\#RRGGBBAA_
- buildVanillaTeam：在[开始游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#开始游戏)时为所有游戏玩家创建原版队伍
- vanillaTeamFormat：创建原版队伍名称格式（底层为`String.format()`，接收队伍ID为参数）
- hideVanillaTeamName：创建原版队伍是否隐藏队伍成员名称
> - 通过任何方式（如指令）退出或修改原版队伍，都不会影响模组内部的队伍机制
> - 由于模组使用的原版队伍名称无法通过原版指令创建，因此无法利用原版功能修改队伍规则，且玩家离开原版队伍会使隐藏名称失效
> - 由于原版 API 限制，原版队伍颜色只能根据模组队伍的颜色，取16种颜色中的近似色
- maxPlayerInvalidTime：玩家离开维度或离线累计时间超时将强制淘汰
- maxBotInvalidTime：人机未加载累计时间超时将强制淘汰
- removeInvalidTeam：队伍成员离线是否算作倒地
> 自己倒地而队友离线则视为队伍被淘汰
- healAllAtStart：游戏开始时治疗所有玩家
- friendlyFire：是否开启友伤
- canHurtNonGamePlayer：未被淘汰的游戏玩家是否能对被非未被淘汰游戏玩家造成伤害
- downFire：倒地后是否允许造成伤害
- downDamage：倒地后承受的流血伤害，总数量为允许的倒地次数
- downDamageFrequency：倒地流血伤害的频率
> 倒地机制需要[玩家救援（PlayerRevive）](https://github.com/CreativeMD/PlayerRevive)模组
- downShoot：倒地后手持枪械是否能射击
- downReload：倒地后手持枪械是否能换弹
- downFireSelect：倒地后是否能切换手持枪械射击模式
- downMelee：倒地后手持枪械是否能近战
> 倒地枪械机制需要同时安装[玩家救援（PlayerRevive）](https://github.com/CreativeMD/PlayerRevive)和[永恒枪械工坊：零（TaCZ）](https://github.com/MCModderAnchor/TACZ)模组
> - 玩家在倒地前执行的换弹不会被取消
- onlyGamePlayerSpectate：是否仅游戏玩家可以观战
- spectateAfterTeamEliminated：游戏玩家是否等队伍淘汰才能观战
- spectatorSeeAllTeams：观战玩家是否可以看见全部队伍
- allowInterfererDamage：非未被淘汰的游戏玩家是否能对游戏玩家造成伤害
- teleportInterfererToLobby：是否在游戏玩家与非游戏玩家互相造成伤害后将非游戏玩家传送回大厅
- forceEliminationTeleportToLobby：是否在游戏玩家被强制淘汰后传送回大厅
- allowRemainingBot：玩家被淘汰后是否允许人机继续游戏
- keepTeamAfterGame：游戏结束后是否保留队伍分配
- teleportAfterGame：游戏结束后将所有非胜利玩家传送回大厅
- teleportWinnerAfterGame：游戏结束后将胜利玩家传送回大厅
- winnerFireworkId：（暂未生效）庆祝胜利玩家的烟花配置id
- winnerParticleId：庆祝胜利玩家的粒子配置id
- initGameAfterGame：大逃杀游戏以胜利队伍结束后自动[初始化游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#初始化游戏)
- restartAfterGame：大逃杀游戏以胜利队伍结束后自动[开始游戏](https://github.com/XColorful/BattleRoyale/wiki/Game-command#开始游戏)
- restartDelay：自动重开游戏的延迟（单位 tick），不小于 5
- maxRestartRound：自动开始游戏的次数，大逃杀未以胜利队伍结束则提前终止
- messageCleanFrequency：服务端清理过期消息的频率，不小于0
- messageExpireTime：服务端消息多久算过期，不小于清理频率
- messageSyncFrequency：服务端强制完整同步消息的频率，不小于过期频率
> - 客户端有不可修改的过期频率：区域消息、游戏信息消息过期频率为15秒，队伍信息消息过期频率为30秒
> - 在不修改模组代码的情况下，玩家掉线的15秒内仍然渲染区域，对于刻意频繁离线超过15秒的玩家应视为作弊者而不是网络不佳
```json
"game": {
	"teleportWhenInitGame": true,
	"teamMessageExpireSeconds": 300,
	"teamColors": [
		"#E9ECEC", "#F07613", "#BD44B3", "#3AAFD9", "#F8C627", "#70B919", "#ED8DAC", "#8E8E86",
		"#A0A0A0", "#158991", "#792AAC", "#35399D", "#724728", "#546D1B", "#A02722", "#141519"
	],
	"buildVanillaTeam": true,
	"vanillaTeamFormat": "CBR Team %s",
	"hideVanillaTeamName": true,
	"maxPlayerInvalidTime": 1200,
	"maxBotInvalidTime": 200,
	"removeInvalidTeam": false,
	"healAllAtStart": true,
	"friendlyFire": false,
	"canHurtNonGamePlayer": false,
	"downFire": false,
	"downDamage": [0.3333, 0.4444, 0.6667, 1.3333, 2, 4, 8, 16, 32],
	"downDamageFrequency": 20,
	"downShoot": false,
	"downReload": false,
	"downFireSelect": false,
	"downMelee": false,
	"onlyGamePlayerSpectate": false,
	"spectateAfterTeamEliminated": true,
	"spectatorSeeAllTeams": true,
	"allowInterfererDamage": false,
	"teleportInterfererToLobby": true,
	"forceEliminationTeleportToLobby": true,
	"allowRemainingBot": true,
	"keepTeamAfterGame": true,
	"teleportAfterGame": true,
	"teleportWinnerAfterGame": false,
	"winnerFireworkId": 0,
	"winnerParticleId": 0,
	"initGameAfterGame": false,
	"restartAfterGame": false,
	"restartDelay": 20,
	"maxRestartRound": 15,
	"messageCleanFreq": 140,
	"messageExpireTime": 100,
	"messageSyncFrequency": 100
}
```

### 扩展规则配置

- protocol：自定义协议名称
- jsonTag：自定义数据，用于扩展规则
```json
"extraRule": {
	"protocol": "namespace:name",
	"jsonTag": {
	}
}
```

#### 死斗模式扩展规则配置

- protocol："battleroyale:deathmatch"或"cbr:deathmatch"均可
- targetKill：目标淘汰数，为死斗模式胜利条件
- killFuncs：[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#单个配置)唯一id列表；当玩家获得有效击杀数后立即对玩家执行其[区域功能词条](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域功能词条)
- respawnTrackDelay：游戏玩家再出生的延迟，单位tick，不小于20
- retickZones：[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#单个配置)唯一id列表；再出生时立即对玩家执行其[区域功能词条](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域功能词条)
- sendProgressBar：是否更新进度条（Boss栏）
- progressBarColor：进度条颜色
- progressBarOverlay：进度条样式
- allowAllWin：是否允许全部队伍胜利；若为 false，则在 $\text{生存队伍数} < \text{winnerTeamTotal} + 1$ 时结束游戏
```json
"extraRule": {
	"protocol": "battleroyale:deathmatch",
	"jsonTag": {
		"targetKill": 50,
		"killFuncs": [],
		"respawnTrackDelay": 100,
		"retickZones": [],
		"sendProgressBar": false,
		"progressBarColor": "white",
		"progressBarOverlay": "progress",
		"allowAllWin": false
	}
}
```

# English

## Single gamerule config

- gameId: unique gamerule id
- gameName: name the config, can be repeated
- color: no function for now
- battleroyale: BattleRoyale gamerule config
- minecraft: Vanilla gamerule config
- game: (optional) game config
- extraRule: (optional) extra gamerule config
```json
{
	"gameId": 0,
	"gameName": "Custom BattleRoyale gamerule",
	"color": "#FFFFFFAA",
	"battleroyale": {
		BATTLEROYALE GAMERULE
	},
	"minecraft": {
		MINECRAFT GAMERULE
	},
	"game": {
		GAME CONFIG
	},
	"extraRule": {
		EXTRA GAMERULE CONFIG
	}
}
```

### BattleRoyale gamerule

- defaultLevelKey: the initial game dimension when the game is not loaded
- playerTotal: total number of players + AI
- teamSize: maximum number of team members
- aiTeammate: whether AI teammates are allowed
- aiEnemy: whether AI enemies are allowed
- requiredTeamToStart: the number of teams required to start BattleRoyale
- maxGameTime: maximum BattleRoyale game time
- winnerTeamTotal: total number of winner teams, the BattleRoyale ends when the number of teams is less than or equal to this value
- lobbyCenter: lobby coordinates
- lobbyDimension: the size of the lobby, which expands in the positive and negative directions of x, y, and z with lobbyCenter as the center, and finally becomes a rectangular block
- lobbyMuteki: whether player is invulnerable in lobby, has no effect on not eliminated game player
- lobbyHeal: Whether to automatically replenish health when teleporting to the lobby
- lobbyChangeGamemode: Whether to change the gamemode to the default mode when teleporting to the lobby
- lobbyTeleportDropInventory: Whether to drop the player's inventory when teleporting to the lobby (Before _lobbyTeleportClearInventory_)
- lobbyTeleportClearInventory: Whether to clear player's inventory when teleporting to the lobby
- recordGameStats: whether to record the statistics of the BattleRoyale game
- autoJoinGame: whether to automatically join the game after initializing game (join any team, if a new team cannot be created, send an invitation instead)
```json
"battleroyale": {
	"defaultLevelKey": "minecraft:overworld",
	"playerTotal": 100,
	"teamSize": 4,
	"aiTeammate": true,
	"aiEnemy": true,
	"requiredTeamToStart": 2,
	"maxGameTime": 12000,
	"winnerTeamTotal": 1,
	"lobbyCenter": "128.0,-60.0,128.0",
	"lobbyDimension": "10.0,10.0,10.0",
	"lobbyMuteki": true,
	"lobbyHeal": true,
	"lobbyChangeGamemode": true,
	"lobbyTeleportDropInventory": false,
	"lobbyTeleportClearInventory": false,
	"recordGameStats": true,
	"autoJoinGame": true
},
```

### Vanilla gamerule
- adventureMode: whether to start the BattleRoyale game in adventure mode, set _false_ to use survival mode
- autoSaturation: automatically replenish saturation every 10 seconds
- clearInventoryAtStart: Whether to clear the player's inventory at the start of the game
- doTimeSet: Whether to make _timeSet_ effective
- timeSet: equivalent to executing _/time set [int]_ once at the start of the game
- The rest of the options are equivalent to the vanilla command _/gamerule OPTION [true/false]_
> Note: In version 0.4.7 and earlier, "tntExplosionDropDecay" was named "tntExplodes".
```json
"minecraft": {
	"adventureMode": true,
	"mobGriefing": false,
	"autoSaturation": true,
	"naturalRegeneration": false,
	"doMobSpawning": false,
	"doFireTick": false,
	"doDaylightCycle": false,
	"doWeatherCycle": false,
	"fallDamage": false,
	"tntExplosionDropDecay": false,
	"spectatorGenerateChunks": false,
	"clearInventoryAtStart": true,
	"keepInventory": false,
	"doImmediateRespawn": false,
	"doTimeSet": true,
	"timeSet": 5000
}
```

### Game config

- teleportWhenInitGame: teleport all game players to lobby in [Initialize the game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#initialize-the-game)
- teamMessageExpireSeconds: [Team request](https://github.com/XColorful/BattleRoyale/wiki/Team-command#Team-request)/[Team invitation](https://github.com/XColorful/BattleRoyale/wiki/Team-command#Team-invitation) message expiration time
- teamColors: Team number color list, format _\#RRGGBBAA_
- buildVanillaTeam: create vanilla teams for all game players in [Start game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Start-game)
- vanillaTeamFormat: the format for creating vanilla team names (internally uses `String.format()` and accepts team ID as a parameter)
- hideVanillaTeamName: hide the names of vanilla team members
> - Manually leaving or modifying a vanilla team (e.g., via commands) will not affect the mod's internal team mechanism.
> - Because the mod uses vanilla team names that cannot be created through vanilla commands, it's impossible to use vanilla functions to modify team rules. However, a player leaving the vanilla team will disable the hidden name effect.
> - Due to a limitation in the vanilla API, vanilla team colors can only be approximated from the mod's team colors, using one of the 16 available Minecraft colors.
- maxPlayerInvalidTime: The time (in ticks) after which a player leaving the dimension or being offline will be forcibly eliminated
- maxBotInvalidTime: The time (in ticks) after which an unloaded bot will be forcibly eliminated
- removeInvalidTeam: Whether a team member being offline counts as being downed
> If a player is downed and their teammate is offline, the team is considered eliminated
- healAllAtStart: Heals all players at the start of the game
- friendlyFire: Whether to enable friendly damage
- canHurtNonGamePlayer: Whether non-eliminated game players can deal damage to non-eliminated non-game players (interferers)
- downFire: Whether to enable downed game player damage
- downDamage: The amount of bleeding damage incurred after being knocked down. The total amount of damage is equal to the allowed number of knockdowns
- downDamageFrequency: The frequency of bleeding damage
> The knockdown mechanism requires [PlayerRevive](https://github.com/CreativeMD/PlayerRevive) mod
- downShoot: Whether downed game player can shoot
- downReload: Whether downed game player can reload gun
- downFireSelect: Whether downed game player can select gun fire mode
- downMelee: Whether downed game player can do gun melee
> The downed gun mechanic requires both the [PlayerRevive](https://github.com/CreativeMD/PlayerRevive) and [TaCZ](https://github.com/MCModderAnchor/TACZ) mods to be installed.
> - Reloads performed by the player before being knocked down will not be canceled.
- onlyGamePlayerSpectate: Whether only game player can spectate the game
- spectateAfterTeamEliminated: Whether game players can only spectate the game after team is eliminated
- spectatorSeeAllTeams: Whether spectator can see all game teams
- allowInterfererDamage: Whether non-eliminated non-game players (interferers) can deal damage to non-eliminated game players
- teleportInterfererToLobby: Whether to teleport the non-game player back to the lobby after dealing damage with a game player
- forceEliminationTeleportToLobby: Whether to teleport game player back to the lobby after force elimination
- allowRemainingBot: Whether to allow bots to continue playing after their human teammates have been eliminated
- keepTeamAfterGame: Whether to keep the team assignments after the game ends
- teleportAfterGame: Whether to teleport all non-winning players back to the lobby after the game ends
- teleportWinnerAfterGame: Whether to teleport the winning players back to the lobby after the game ends
- winnerFireworkId: (Not yet effective) The firework config ID for celebrating the winning players
- winnerParticleId: The particle config ID for celebrating the winning players
- initGameAfterGame: Automatically [Initialize the game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#initialize-the-game) when finish BattleRoyale with winner game team(s)
- restartAfterGame: Automatically [Start game](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Start-game) when BattleRoyale ends with a winning team.
- restartDelay: Delay for automatic game restart (in ticks), minimum value is 5.
- maxRestartRound: The maximum number of automatic restarts; will terminate early if the game does not end with a winning team.
- messageCleanFrequency: How often the server clears expired messages (minimum 0)
- messageExpireTime: How long a message stays on the server before expiring (minimum: messageCleanFrequency)
- messageSyncFrequency: How often the server forces a full message resync (minimum: messageExpireTime)
> - The client's expiration frequency cannot be changed: Zone and game messages expire after 15 seconds; team messages expire after 30 seconds.
> - Without modifying the mod's code, the Zone will still be rendered within 15 seconds after the player is offline. Anyone disconnecting frequently for longer than 15 seconds should be flagged as a cheater, not someone with a bad internet connection.
```json
"game": {
	"teleportWhenInitGame": true,
	"teamMessageExpireSeconds": 300,
	"teamColors": [
		"#E9ECEC", "#F07613", "#BD44B3", "#3AAFD9", "#F8C627", "#70B919", "#ED8DAC", "#8E8E86",
		"#A0A0A0", "#158991", "#792AAC", "#35399D", "#724728", "#546D1B", "#A02722", "#141519"
	],
	"buildVanillaTeam": true,
	"vanillaTeamFormat": "CBR Team %s",
	"hideVanillaTeamName": true,
	"maxPlayerInvalidTime": 1200,
	"maxBotInvalidTime": 200,
	"removeInvalidTeam": false,
	"healAllAtStart": true,
	"friendlyFire": false,
	"canHurtNonGamePlayer": false,
	"downFire": false,
	"downDamage": [0.3333, 0.4444, 0.6667, 1.3333, 2, 4, 8, 16, 32],
	"downDamageFrequency": 20,
	"downShoot": false,
	"downReload": false,
	"downFireSelect": false,
	"downMelee": false,
	"onlyGamePlayerSpectate": false,
	"spectateAfterTeamEliminated": true,
	"spectatorSeeAllTeams": true,
	"allowInterfererDamage": false,
	"teleportInterfererToLobby": true,
	"forceEliminationTeleportToLobby": true,
	"allowRemainingBot": true,
	"keepTeamAfterGame": true,
	"teleportAfterGame": true,
	"teleportWinnerAfterGame": false,
	"winnerFireworkId": 0,
	"winnerParticleId": 0,
	"initGameAfterGame": false,
	"restartAfterGame": false,
	"restartDelay": 20,
	"maxRestartRound": 15,
	"messageCleanFreq": 140,
	"messageExpireTime": 100,
	"messageSyncFrequency": 100
```

### Extra gamerule config

- protocol: A custom protocol name
- jsonTag: Custom data, for extra gamerule
```json
"extraRule": {
	"protocol": "namespace:name",
	"jsonTag": {
	}
}
```

#### DeathMatch extra gamerule config

- protocol: "battleroyale:deathmatch" or "cbr:deathmatch"
- targetKill: Target number of eliminations required to win in DeathMatch mode
- killFuncs: A list of [Zone config](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Single-zone-config) IDs; the [Zone function entry](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-function-entry) is executed immediately after player gain a valid kill
- respawnTrackDelay: The delay before a game player respawns, in ticks, no less than 20
- retickZones: A list of [Zone config](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Single-zone-config) IDs; the [Zone function entry](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-function-entry) is executed immediately for players upon respawn
- sendProgressBar: Whether to display and update the game progress bar (Boss Bar)
- progressBarColor: The color of the progress bar
- progressBarOverlay: The visual style of the progress bar
- allowAllWin: Whether to allow all teams to win; otherwise, the game ends when $\text{Alive teams} < \text{winnerTeamTotal} + 1$
```json
"extraRule": {
	"protocol": "battleroyale:deathmatch",
	"jsonTag": {
		"targetKill": 50,
		"killFuncs": [],
		"respawnTrackDelay": 100,
		"retickZones": [],
		"sendProgressBar": false,
		"progressBarColor": "white",
		"progressBarOverlay": "progress",
		"allowAllWin": false
	}
}
```