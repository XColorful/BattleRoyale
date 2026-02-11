[English](#English)

# 统计数据配置

## 单个配置

- id：统计数据配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
```json
{
	"id": 0,
	"name": "",
	"color": "#FFFFFF",
	"scoreboard": {
		记分板配置
	}
}
```

### 记分板配置

- recordScoreboard：是否写入记分板
- resetScoreboardAtStart：是否在游戏开始时重置记分板
- mcMaxHealth：玩家最大生命值
- damageMultiplier：伤害分数倍率
- ratioBase：100%倍率对应的分数
> 伤害分数： $\text{mcMaxHealth} \times \text{damageMultiplier} \times \text{ratioBase}$
- syncGameInfoToObjective：是否同步游戏信息至记分项
- gameInfoObjectiveName：游戏信息记分项名称
	- playerTotal：玩家总数名称
	- alive：存活人数名称
	- gameTime：游戏时间名称，值以秒为单位
- scoreboardCycleInterval：侧边栏记分板循环间隔，单位 tick
- cycleObjectiveName：循环切换的记分板列表
- mainObjective：主记分项
	记分项名称：
	- player_to_player_damage：玩家对玩家造成的伤害
	- other_to_player_damage：非玩家对玩家造成的伤害
	- player_damage_by_player：玩家承受玩家伤害
	- player_damage_by_other：玩家承受非玩家伤害
	- player_knock_player：玩家击倒玩家次数
	- other_knock_player：非玩家击倒玩家次数
	- player_down_by_player：玩家被玩家击倒次数
	- player_down_by_other：玩家被非玩家击倒次数
	- player_revive：玩家复活次数
	- player_kill_player：玩家淘汰玩家次数
	- other_kill_player：非玩家淘汰玩家次数
	- player_death_by_player：玩家被玩家淘汰次数
	- player_death_by_other：玩家被非玩家淘汰次数
	- player_win：玩家获胜次数
	- player_lose：玩家失败次数
- secondObjective：二次计算记分项
	记分项名称：
	- player_attack_rate：玩家对玩家造成的伤害 / 玩家承受玩家伤害
	- player_kd：玩家击杀玩家次数 / 玩家被玩家击杀次数
	- player_game_total：玩家获胜次数 + 玩家失败次数
	- player_win_rate：玩家获胜次数 / （玩家获胜次数 + 玩家失败次数）
- specialObjective：特殊记分项
	- enableJourneyStats：是否启用玩家移动距离记分项
	- journeyStatsDelay：开始记录玩家移动距离记分项的延迟
	记分项名称：
	- player_journey：玩家移动距离记分项名称
- listObjectiveAfterGame：游戏结束后显示在列表的记分项
- sidebarObjectiveAfterGame：游戏结束后显示在侧边栏的记分项
```json
"scoreboard": {
	"recordScoreboard": true,
	"resetScoreboardAtStart": false,
	"mcMaxHealth": 20,
	"damageMultiplier": 5,
	"ratioBase": 1000,
	"syncGameInfoToObjective": true,
	"gameInfoObjectiveName": "cbr_gameInfo",
	"gameInfoObjective": {
		"playerTotal": "Total",
		"alive": "Alive",
		"gameTime": "GameTime",
	},
	"scoreboardCycleInterval": 60,
	"cycleObjectiveName": [
		"cbr_gameInfo"
	],
	"mainObjective": {
		"player_to_player_damage": "cbr_hurt",
		"other_to_player_damage": "cbr_otherHurt",
		"player_damage_by_player": "cbr_damage",
		"player_damage_by_other": "cbr_otherDamage",
		"player_knock_player": "cbr_knock",
		"other_knock_player": "cbr_otherKnock",
		"player_down_by_player": "cbr_down",
		"player_down_by_other": "cbr_otherDown",
		"player_revive": "cbr_revive",
		"player_kill_player": "cbr_kill",
		"other_kill_player": "cbr_otherKill",
		"player_death_by_player": "cbr_death",
		"player_death_by_other": "cbr_otherDeath",
		"player_win": "cbr_win",
		"player_lose": "cbr_lose"
	},
	"secondObjective": {
		"player_attack_rate": "cbr_attackRate",
		"player_kd": "cbr_kd",
		"player_game_total": "cbr_gameTotal",
		"player_win_rate": "cbr_winRate"
	},
	"specialObjective": {
		"enableJourneyStats": true,
		"journeyStatsDelay": 100,
		"player_journey": "cbr_journey"
	},
	"listObjectiveAfterGame": "cbr_winRate",
	"sidebarObjectiveAfterGame": "cbr_winRate"
}
```

# English

## Single stats config

- id: unique stats id
- name: name the config, can be repeated
- color: no function for now
```json
{
	"id": 0,
	"name": "",
	"color": "#FFFFFF"
}
```

### Scoreboard config

- recordScoreboard: Whether to write data to the scoreboard
- resetScoreboardAtStart: Whether to reset the scoreboard when the game starts
- mcMaxHealth: Maximum health of players
- damageMultiplier: Multiplier for damage-related scores
- ratioBase: The score corresponding to a 100% ratio
> Damage Score Formula: $\text{mcMaxHealth} \times \text{damageMultiplier} \times \text{ratioBase}$
- syncGameInfoToObjective: Whether to sync game information to a scoreboard objective
- gameInfoObjectiveName: Name of the objective used for game information
	- playerTotal: Name for total player count
	- alive: Name for alive player count
	- gameTime: Name for game time (value in seconds)
- scoreboardCycleInterval: The interval for cycling sidebar objectives, measured in ticks
- cycleObjectiveName: List of objectives to be displayed in the sidebar rotation
- mainObjective: Primary objectives
    Objective Names:
    - player_to_player_damage: Damage dealt by players to other players
    - other_to_player_damage: Damage dealt by non-players (e.g., mobs, environment) to players
    - player_damage_by_player: Damage taken by players from other players
    - player_damage_by_other: Damage taken by players from non-player sources
    - player_knock_player: Number of times a player knocked down another player
    - other_knock_player: Number of times a non-player knocked down a player
    - player_down_by_player: Number of times a player was knocked down by another player
    - player_down_by_other: Number of times a player was knocked down by a non-player source
    - player_revive: Number of times a player was revived
    - player_kill_player: Number of times a player eliminated another player
    - other_kill_player: Number of times a non-player eliminated a player
    - player_death_by_player: Number of times a player was eliminated by another player
    - player_death_by_other: Number of times a player was eliminated by a non-player source
    - player_win: Total number of player victories
    - player_lose: Total number of player losses
- secondObjective: Secondary calculated objectives
    Objective Names:
    - player_attack_rate: Player damage dealt to players / Player damage taken from players
    - player_kd: Player kills / Player deaths
	- player_game_total: Player wins + Player losses
    - player_win_rate: Player wins / (Player wins + Player losses)
- specialObjective: Special objectives
    - enableJourneyStats: Whether to enable the tracking of player movement distance
    - journeyStatsDelay: Delay before starting to record player movement distance
    Objective Names:
    - player_journey: Name for the movement distance objective
- listObjectiveAfterGame: The objective displayed in the player list after the game ends
- sidebarObjectiveAfterGame: The objective displayed in the sidebar after the game ends
```json
"scoreboard": {
	"recordScoreboard": true,
	"resetScoreboardAtStart": false,
	"mcMaxHealth": 20,
	"damageMultiplier": 5,
	"ratioBase": 1000,
	"syncGameInfoToObjective": true,
	"gameInfoObjectiveName": "cbr_gameInfo",
	"gameInfoObjective": {
		"playerTotal": "Total",
		"alive": "Alive",
		"gameTime": "GameTime",
	},
	"scoreboardCycleInterval": 60,
	"cycleObjectiveName": [
		"cbr_gameInfo"
	],
	"MainObjective": {
		"player_to_player_damage": "cbr_hurt",
		"other_to_player_damage": "cbr_otherHurt",
		"player_damage_by_player": "cbr_damage",
		"player_damage_by_other": "cbr_otherDamage",
		"player_knock_player": "cbr_knock",
		"other_knock_player": "cbr_otherKnock",
		"player_down_by_player": "cbr_down",
		"player_down_by_other": "cbr_otherDown",
		"player_revive": "cbr_revive",
		"player_kill_player": "cbr_kill",
		"other_kill_player": "cbr_otherKill",
		"player_death_by_player": "cbr_death",
		"player_death_by_other": "cbr_otherDeath",
		"player_win": "cbr_win",
		"player_lose": "cbr_lose"
	},
	"secondObjective": {
		"player_attack_rate": "cbr_attackRate",
		"player_kd": "cbr_kd",
		"player_game_total": "cbr_gameTotal",
		"player_win_rate": "cbr_winRate"
	},
	"specialObjective": {
		"enableJourneyStats": true,
		"journeyStatsDelay": 100,
		"player_journey": "cbr_journey"
	},
	"listObjectiveAfterGame": "cbr_winRate",
	"sidebarObjectiveAfterGame": "cbr_winRate"
}
```