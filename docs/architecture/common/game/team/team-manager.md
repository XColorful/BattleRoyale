[English](#English)

## 队伍管理器

- shouldAutoJoin：是否自动执行[加入新队](https://github.com/XColorful/BattleRoyale/wiki/Team-command#加入新队)
- hasEnoughPlayerTeamToStart：当前游戏队伍数量是否能开始游戏

[![ITeamManager](/docs/api/game/team/ITeamManager.md)](/docs/api/game/team/ITeamManager.md)

#### 队伍管理器启动流程

- initGameConfig：读取配置，清除无效游戏玩家
- initGame：可创建`游戏玩家`，可提示游戏玩家/队伍数量是否足够
- startGame：为所有`游戏玩家`创建`未被淘汰的游戏玩家`列表

### 游戏队伍查询

`游戏玩家`应使用GamePlayer，`游戏队伍`应使用GameTeam
- getGamePlayerByUUID：通过UUID查询`游戏玩家`
- getGamePlayerBySingleId：通过`队伍管理器`管理的**唯一游戏玩家ID**查询`游戏玩家`，不小于1
- hasStandingGamePlayer：通过UUID查询`未被淘汰的游戏玩家`
- getGameTeams：获取`游戏队伍`列表
- getGameTeamById：通过`队伍管理器`管理的**唯一队伍玩家ID**查询`游戏队伍`，不小于1
- getGamePlayers：获取`游戏玩家`列表
- getStandingGamePlayers：获取`未被淘汰的游戏玩家`列表
- getTotalMembers：获取所有游戏玩家总数
- getStandingPlayerTeamCount：统计未被淘汰的含非人机游戏玩家的游戏队伍总数
- getStandingTeamCount：统计`未被淘汰的游戏队伍`总数

[![IGameTeamReadApi](/docs/api/game/team/IGameTeamReadApi.md)](/docs/api/game/team/IGameTeamReadApi.md)

### 队伍管理

#### 队伍指令功能

- joinTeam：加入队伍
- joinTeamSpecific：加入指定队伍
- kickPlayer：将玩家踢出队伍
- invitePlayer：邀请玩家加入队伍
- requestPlayer：申请加入其他玩家队伍
- leaveTeam：离开队伍
- addToTeam：将生物加入指定队伍
- buildTeamForAll：为选定生物组建队伍
> 游戏中离开队伍通常视为被淘汰，但**不**应从`游戏玩家`列表清除`GamePlayer`

[![ITeamExternal](/docs/api/game/team/ITeamExternal.md)](/docs/api/game/team/ITeamExternal.md)

#### 原版队伍功能

（可选）按照`游戏队伍`构建Minecraft原版队伍，但原版队伍的变更**不**应影响游戏队伍

[![IVanillaTeam](/docs/api/game/team/IVanillaTeam.md)](/docs/api/game/team/IVanillaTeam.md)

# English

## Team Manager

- shouldAutoJoin: Whether to automatically execute [Join a new team](https://github.com/XColorful/BattleRoyale/wiki/Team-command#Join-a-new-team).
- hasEnoughPlayerTeamToStart: Whether the current number of game teams is sufficient to start the game.

[![ITeamManager](/docs/api/game/team/ITeamManager.md)](/docs/api/game/team/ITeamManager.md)

#### Team Manager Startup Flow

- initGameConfig: Reads configuration and clears invalid game players.
- initGame: Creates `GamePlayer` objects and may prompt if the number of game players/teams is sufficient.
- startGame: Creates the list of `Non-Eliminated Game Players` for all `GamePlayer` objects.

### Game Team Query

`GamePlayer` should be used for game players, and `GameTeam` should be used for game teams.
- getGamePlayerByUUID: Queries a `GamePlayer` by UUID.
- getGamePlayerBySingleId: Queries a `GamePlayer` by the **Unique Game Player ID** managed by the `Team Manager` (must be ≥ 1).
- hasStandingGamePlayer: Queries for a `Non-Eliminated Game Player` by UUID.
- getGameTeams: Gets the list of `GameTeam` objects.
- getGameTeamById: Queries a `GameTeam` by the **Unique Team Player ID** managed by the `Team Manager` (must be ≥ 1).
- getGamePlayers: Gets the list of `GamePlayer` objects.
- getStandingGamePlayers: Gets the list of `Non-Eliminated Game Players`.
- getTotalMembers: Gets the total count of all game players.
- getStandingPlayerTeamCount: Counts the total number of non-eliminated game teams containing non-bot game players.
- getStandingTeamCount: Counts the total number of `Non-Eliminated Game Teams`.

[![IGameTeamReadApi](/docs/api/game/team/IGameTeamReadApi.md)](/docs/api/game/team/IGameTeamReadApi.md)

### Team Management

#### Team Command Function

- joinTeam: Joins a team.
- joinTeamSpecific: Joins a specific team. 
- kickPlayer: Kicks a player from the team. 
- invitePlayer: Invites a player to join the team. 
- requestPlayer: Requests to join another player's team.  
- leaveTeam: Leaves the team.
- addToTeam: Add a living entity to the specified team
- buildTeamForAll: Build teams for selected living entities
> Leaving a team during the game is usually treated as elimination, but the `GamePlayer` object should **not** be cleared from the list of `Game Players`.

[![ITeamExternal](/docs/api/game/team/ITeamExternal.md)](/docs/api/game/team/ITeamExternal.md)

#### Vanilla Team Function

(Optional) Builds Minecraft vanilla teams according to the `Game Teams`, but changes to vanilla teams should **not** affect game teams.

[![IVanillaTeam](/docs/api/game/team/IVanillaTeam.md)](/docs/api/game/team/IVanillaTeam.md)