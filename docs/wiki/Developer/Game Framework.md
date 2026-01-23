# 游戏框架

游戏框架设计为**主管理器**调度各**子管理器**，将各功能拆分至子管理器：

|游戏子管理器|对应接口名称|职责简述|
|:--|:--|:--|
|游戏管理器（主管理器）|`IGameManager`|调度其他游戏子管理器|
|游戏规则管理器|`IGameruleManager`|设置游戏规则，切换游戏模式|
|游戏大厅管理器|`IGameLobbyManager`|提供大厅传送、大厅无敌|
|游戏物资刷新管理器|`IGameLootManager`|持续执行物资刷新|
|游戏进程管理器|`IGameProcessManager`|更新游戏状态、实现特定游戏类型|
|出生管理器|`ISpawnManager`|游戏开始时传送玩家|
|统计管理器|`IStatsManager`|记录游戏统计数据|
|队伍管理器|`ITeamManager`|管理游戏队伍、游戏玩家|
|区域管理器|`IZoneManager`|持续更新游戏区域|

### 游戏子管理器

主管理器及子管理器都实现的接口
> 用指令调用主管理器执行某功能，主管理器应调度所有子管理器也执行相应功能
```java
package xiao.battleroyale.api.game;

public interface IGameSubManager {
	// 读取配置
    void initGameConfig(ServerLevel serverLevel);
    boolean isConfigPrepared();
	// 初始化
    void initGame(ServerLevel serverLevel);
    boolean isReady();
	// 开始游戏
    boolean startGame(ServerLevel serverLevel);
    // 开始游戏后
    void onGameTick(int gameTime);
    void stopGame(@Nullable ServerLevel serverLevel);
}
```

#### 游戏流程

读取配置 → 初始化游戏 → 开始游戏 → 持续运行/提前结束
- initGameConfig：从配置文件读取游戏配置，该阶段不应有多余操作
- initGame：初始化游戏，注册相关事件处理器、集中创建玩家队伍、预计算性能密集型事务等
- startGame：若成功开始游戏，则当前`游戏时间`为0，下一刻`游戏时间`为1并调度子管理器
- onGameTick：每`游戏时间`（游戏刻）调度子管理器，更新游戏状态
- stopGame：强制停止游戏，不进行结算

##### 特殊流程

“一键开始游戏”：执行`startGame`时，若有子管理器未通过`isReady`则应自动执行一次`initGame`；执行`initGame`时，若有子管理器未通过`isConfigPrepared`则应自动执行一次`initGameConfig`
> 在`initGame`和`startGame`刚执行时立即进行一次检查

连续初始化/配置时效性：主管理器在`initGame`成功后，会使自身通过`isReady`但不通过`isConfigPrepared`，使下次`initGame`时重新执行`initGameConfig`，以确保配置时效性

游戏人数检查：主管理器执行`initGame`后，队伍管理器（因玩家人数不够）未通过`isReady`应立即提示“人数不足”；若不希望在`initGame`时提示“人数不足”，可以将人数检查推迟至`startGame`以避免
> 模组默认的主管理器不检查队伍管理器的`isReady`，使得会提示“人数不足”但不影响`startGame`开头的检查


## 游戏管理器

- 外部只需要获取全局静态API`IGameManager`即可，`IGameManager`具体类再往下层层委派功能，而未将`IGameManager`拆分成不同类实现且未添加权限检查
- 外部调用视为拥有所有权限（即使只调用普通接口），不应调用的接口已经添加了`@Internal`或`@Deprecated`并说明
> _`IGameManager`继承的各接口只是对`游戏管理器`本身应有功能分类而进行拆分，如游戏管理的配置读写，游戏事件处理（本身就是`IGameManager`的职责），及其特殊状态的设置……_
> 
> _本模组各`游戏子管理器`的默认实现类会在代码较多时使用protected抽离函数体到同包下代理的“门面模式”，使得各`游戏子管理器`对应的接口都像`IGameManager`提供了**1个**该职责下通用且功能全面的API_
> 
> _`IGameManager`这个“部门”开放共享，访客只需要知道`IGameManager`而不需要知道其内部资源如何分层管理就能直接获取，让访客任意使用其功能并**自行调试**_
```java
package xiao.battleroyale.api.game;

public interface IGameManager extends IGameMainManager, IGameApiGetter, IGameConfigGetter,  
        IGameConfigSetter, IGameStatusSetter, IGameEventReceiver {
}
```

### 游戏主管理器

- 主管理器调度各子管理器
- 提供其下所有子管理器的获取接口
```java
package xiao.battleroyale.api.game;

public interface IGameMainManager extends IGameSubManager, IGameFunc, IGameInfoGetter {
	@NotNull IGameProcessManager getGameProcessManager();  
	@NotNull IGameruleManager getGameruleManager();  
	@NotNull IGameLootManager getGameLootManager();  
	@NotNull ISpawnManager getSpawnManager();  
	@NotNull IGameLobbyManager getGameLobbyManager();  
	@NotNull IStatsManager getStatsManager();  
	@NotNull ITeamManager getTeamManager();  
	@NotNull IZoneManager getZoneManager();
}
```

#### 游戏主管理器功能

- spectateGame：由`游戏进程管理器`接管，返回玩家是否能观战游戏，并使玩家观战游戏
- finishGame：区别于`stopGame`，仅允许`游戏进程管理器`调用该方法，并在符合条件时立即结束游戏并进行胜利结算
```java
package xiao.battleroyale.api.game;

public interface IGameFunc {
    boolean spectateGame(ServerPlayer player);
    void finishGame(boolean hasWinner);
}
```

#### 游戏信息

- getGameTime：获取当前`游戏时间`，单位为tick
- getGameId：获取当前`游戏ID`（UUID）
- getGlobalCenterOffset：获取`全局偏移`（用于切换地图）
- getWinnerTeamTotal：获取最大胜利队伍数
- getServerLevel：获取当前游戏维度
```java
package xiao.battleroyale.api.game;

public interface IGameInfoGetter {
    int getGameTime();  
    UUID getGameId();
    Vec3 getGlobalCenterOffset();
    int getWinnerTeamTotal();
    ServerLevel getServerLevel();
}
```

### 游戏ID

游戏管理器为每局游戏创建一个UUID作为`游戏ID`，同时还用于：
- `游戏物资刷新器`刷新物品/生成实体/更新方块实体时写入游戏ID（持久化保存）以辨别是否已刷新过/需要清理
- 让其他模组刷新物品/生成实体后手动写入游戏ID进行标记，以**防止**被`游戏物资刷新器`**自动清理**
```java
package xiao.battleroyale.api.game;

public interface IGameApiGetter {
	IGameIdReadApi getGameIdReadApi();
	IGameIdWriteApi getGameIdWriteApi();
}
```
```java
package xiao.battleroyale.api.game;

public interface IGameIdReadApi {
	@Nullable UUID getGameId(Entity entity);
	@Nullable UUID getGameId(BlockEntity blockEntity);
	@Nullable UUID getGameId(ItemStack itemStack);
}
```
```java
package xiao.battleroyale.api.game;

public interface IGameIdWriteApi {
    void addGameId(ItemStack itemStack, UUID gameId);
    void addGameId(Entity entity, UUID gameId);
    void addGameId(BlockEntity blockEntity, UUID gameId);
}
```

### 选用子管理器配置

本模组支持任意数量的配置预设，而每局游戏只能选中其一
```java
package xiao.battleroyale.api.game;

public interface IGameConfigGetter {
	int getGameruleConfigId(); // 查看选用的游戏规则配置ID
	int getSpawnConfigId(); // 查看选用的出生配置ID
	// ...其余配置
}
```
手动设置配置ID后需要再次`initGameConfig`以重新读取
```java
package xiao.battleroyale.api.game;

public interface IGameConfigSetter {
	boolean setGameruleConfigId(int gameId);
	boolean setSpawnConfigId(int id);
	// ...其余配置
}
```

### 设置游戏管理器属性

- 游戏步长用于**跳过**`游戏时间`，特别是用于**快速测试区域配置**，而**不**应用于加速游戏
- 游戏管理器缓存一个`全局偏移`，使`区域管理器`和`出生管理器`能方便地切换地图并调整出生地点
- 每局游戏在同一维度（_ServerLevel_）下更新游戏状态，玩家离开维度则无法获取，且无法接收游戏消息（服务端→客户端通信）
```java
package xiao.battleroyale.api.game;

public interface IGameStatusSetter {
	boolean setGameStep(int step);
	boolean setGlobalCenterOffset(Vec3 offset);
	void setDefaultLevel(String defaultLevelKey);
}
```

### 游戏事件处理

将游戏事件委派给`游戏进程管理器`处理，更改部分规则即可转化为其他游戏类型
> 该接口已经由模组事件处理器调用，游戏管理器应只在`游戏进程管理器`接管前后发送相应事件
> 更改事件处理应通过替换`游戏进程管理器`实现
```java
package xiao.battleroyale.api.game;

public interface IGameEventReceiver {
    @ApiStatus.Internal void onPlayerLoggedIn(ServerPlayer player);
    @ApiStatus.Internal void onPlayerLoggedOut(ServerPlayer player);
    @ApiStatus.Internal void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity);
    @ApiStatus.Internal void onPlayerRevived(@NotNull GamePlayer gamePlayer);
    @ApiStatus.Internal void onPlayerDeath(@Nullable ILivingDeathEvent event, @NotNull GamePlayer gamePlayer);
}
```

## 游戏规则管理器

#### 游戏规则管理器启动流程

- initGameConfig：读取游戏规则配置，包含MC原版规则（_/gamerule_）、大逃杀规则、其他规则设置
- initGame：应用部分游戏规则
- startGame：应用其余游戏规则，如设置玩家游戏模式

### 游戏规则属性

#### 默认游戏规则

> 如果需要替换`游戏规则管理器`并不需要所有玩家统一的游戏模式，可忽略
```java
package xiao.battleroyale.api.game.gamerule;
public interface IGameruleManager extends IGameSubManager {
	GameType getGameMode();
}
```

## 游戏大厅管理器

#### 游戏大厅管理器启动流程

- initGameConfig：读取配置，使[传送至大厅](https://github.com/XColorful/BattleRoyale/wiki/Game-command#传送至大厅)生效
- initGame：根据配置决定是否将玩家传送至大厅，模组默认的`游戏进程管理器`已经提前调度`队伍管理器`以保证获取到刚创建的游戏玩家
```java
package xiao.battleroyale.api.game.lobby;

public interface IGameLobbyManager extends IGameSubManager, IGameLobbyReadApi, ILobbyFuncApi {
}
```

#### 大厅功能

- healPlayer：恢复游戏玩家状态
- teleportToLobby：传送回大厅
```java
package xiao.battleroyale.api.utilitity;

public interface ILobbyFuncApi {
	void healPlayer(@NotNull LivingEntity livingEntity);
	boolean teleportToLobby(@NotNull LivingEntity livingEntity);
}
```

#### 大厅API

- lobbyLevelKey：大厅所在维度的 _ResourceKey_
- canMuteki：当前位置及状态是否可以受**大厅无敌**保护
```java
package xiao.battleroyale.api.game.lobby;

public interface IGameLobbyReadApi extends ILobbyReadApi {
	void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner);
}
```
```java
package xiao.battleroyale.api.utilitity;

public interface ILobbyReadApi {
    ResourceKey<Level> lobbyLevelKey();
    Vec3 lobbyPos();
    Vec3 lobbyDimension();
    
    boolean isInLobbyRange(Vec3 pos);  
    boolean canMuteki(@NotNull LivingEntity livingEntity);
}
```

## 游戏物资刷新管理器

```java
package xiao.battleroyale.api.game.loot;

public interface IGameLootManager extends IGameSubManager, IGameLootConfigGetter, IGameLootStatus, IGameLootTester, IGameLootOperator {
}
```

#### 物资刷新器配置

- 本模组默认物资刷新器使用异步BFS+单tick限制刷新区块数（均摊计算量）以提高性能
> - 以各玩家位置为BFS初始队列逐层遍历，使刷新尽可能公平
> - 需要在服务器关闭时安全处理额外线程
```java
package xiao.battleroyale.api.game.loot;

public interface IGameLootConfigGetter {
    int getMaxLootChunkPerTick();
    int getMaxLootDistance();
    int getTolerantCenterDistance();
    int getMaxCachedCenter();
    int getMaxQueuedChunk();
    int getBfsFrequency();
    boolean isInstantNextBfs();
    int getMaxCachedLootChunk();
    int getCleanCachedChunk();
    
    int getSimulationDistance();
}
```
```java
package xiao.battleroyale.api.game.loot;

public interface IGameLootOperator {
	void awaitTerminationOnShutdown();
}
```

## 游戏进程管理器

- checkIfGameShouldEnd：完整检查游戏状态，如符合条件则直接调用`游戏管理器`游戏主管理器功能结束游戏并进行胜利结算
```java
package xiao.battleroyale.api.game.process;

public interface IGameProcessManager extends IGameSubManager, IGameManagement, IGameNotification, IGameEventHandler {
    void checkIfGameShouldEnd();
}
```

#### 游戏进程管理器启动流程

- startGame：执行一次`checkAndUpdateInvalidGamePlayer`，供`游戏时间`为1时使用，并清除无效玩家

### 游戏进程管理

#### 游戏状态管理

- checkAndUpdateInvalidGamePlayer：检查所有玩家，更新不在线时长及最后有效位置，处理无效玩家
- teleportToLobbyInGame：玩家手动[传送至大厅](https://github.com/XColorful/BattleRoyale/wiki/Game-command#传送至大厅)，通常应视为离开游戏而直接淘汰
- teleportAfterGame：游戏结束后对胜利玩家与其余玩家进行一次传送
- spectateGame：只能由`游戏管理器`调用，返回玩家是否能观战游戏，并使玩家观战游戏
- healGamePlayers：开始游戏时由`游戏管理器`调用，恢复所有游戏玩家状态
- finishGameAddWinner：结算胜利玩家，调用`游戏管理器`添加胜利玩家及胜利队伍
```java
package xiao.battleroyale.api.game.process;

public interface IGameManagement {
    void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel);
    void teleportToLobbyInGame(ServerPlayer player);
    void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,  
                           boolean teleportWinnerAfterGame, boolean teleportAfterGame);
    boolean spectateGame(ServerPlayer player);
    void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers); 
    void finishGameAddWinner(boolean hasWinner);  
}
```

#### 游戏事件处理

由`游戏管理器`派发的事件，如大逃杀游戏在玩家死亡时即淘汰该玩家对应的游戏玩家
> - 该接口只能由`游戏管理器`调用
> - 如有特殊机制如大逃杀游戏可能将倒地事件判定应为淘汰事件，则应调用`游戏管理器`并由其发送相关事件
```java
package xiao.battleroyale.api.game.process;

public interface IGameEventHandler {
	void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate);
	void onPlayerLoggedOut(boolean isInGame, ServerPlayer player);
	void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, LivingEntity livingEntity, boolean removeInvalidTeam);
	void onPlayerDeath(@Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void onPlayerRevived(@NotNull GamePlayer gamePlayer);
}
```

#### 发送游戏消息

- sendWinnerResult：在聊天栏发送胜利结算信息
- notifyWinner：向胜利玩家发送消息
- sendGameSpectateMessage：提供观战游戏指令快捷方式
- sendDownMessage：游戏玩家倒地时聊天栏消息
- sendReviveMessage：游戏玩家复活时聊天栏消息
- sendEliminateMessage：游戏玩家被淘汰时聊天栏消息
```java
package xiao.battleroyale.api.game.process;

public interface IGameNotification {
	void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams, int gameTime);
	void notifyWinner(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, int winnerParticleId);
	void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate);
	void sendDownMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void sendReviveMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void sendEliminateMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
}
```

## 出生管理器

#### 出生管理器启动流程

- initGameConfig：读取游戏出生器配置
- initGame：初始化游戏出生器

#### 游戏玩家出生器

> `getGameSpawner()`应仅用于调试目的
```java
package xiao.battleroyale.api.game.spawn;

public interface ISpawnManager extends IGameSubManager {
    @Deprecated(forRemoval = false) IGameSpawner getGameSpawner();
}
```

## 统计管理器

#### 统计管理器启动流程

- initGameConfig：读取游戏统计数据配置
- initGame：清除旧统计数据
- startGame：开始记录统计数据
> 若开始游戏失败，正常情况下应会再次执行`initGame`以重置统计数据

### 统计游戏数据

```java
package xiao.battleroyale.api.game.stats;

public interface IStatsManager extends IGameSubManager, IGameEventStatsRecorder, IStatsQuery,
IZoneStatsRecorder, IGameruleStatsRecorder, ISpawnStatsRecorder {
    String getStatsFilePath();
    void saveStats(String filePath);  
}
```

#### 统计游戏事件数据

```java
package xiao.battleroyale.api.game.stats;

public interface IGameEventStatsRecorder {
    void onRecordDamage(GamePlayer gamePlayer, DamageSource damageSource, float damage);
    void onRecordDamage(GamePlayer gamePlayer, ILivingDamageEvent livingDamageEvent);
    void onRecordInstantRevive(GamePlayer gamePlayer, ILivingDeathEvent event);
    void onRecordDown(GamePlayer gamePlayer, ILivingDeathEvent event);
    void onRecordKill(GamePlayer gamePlayer, ILivingDeathEvent event);
}
```

#### 统计游戏子管理器数据

```java
package xiao.battleroyale.api.game.stats;

public interface IZoneStatsRecorder {
}
```
```java
package xiao.battleroyale.api.game.stats;

public interface IGameruleStatsRecorder {
}
```
```java
package xiao.battleroyale.api.game.stats;

public interface ISpawnStatsRecorder {
}
```

#### 查询游戏统计数据

```java
package xiao.battleroyale.api.game.stats;

public interface IStatsQuery {
    void getGamePlayerStats(int playerId);
    void getGamePlayerStats(UUID playerUUID);
    void getGamePlayerStats(String playerName);
    void getGameTeamStats(int teamId);
    void getGameruleStats(String gameruleName);
}
```

## 队伍管理器

- shouldAutoJoin：是否自动执行[加入新队](https://github.com/XColorful/BattleRoyale/wiki/Team-command#加入新队)
- hasEnoughPlayerTeamToStart：当前游戏队伍数量是否能开始游戏
```java
package xiao.battleroyale.api.game.team;

public interface ITeamManager extends IGameSubManager, IGameTeamReadApi, ITeamExternal, ITeamManagement, ITeamPreManagement, ITeamNotification, IVanillaTeam {
	boolean shouldAutoJoin();
	boolean hasEnoughPlayerTeamToStart();
}
```

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
```java
package xiao.battleroyale.api.game.team;

public interface IGameTeamReadApi {
	@Nullable GamePlayer getGamePlayerByUUID(UUID uuid);
	@Nullable GamePlayer getGamePlayerBySingleId(int playerId);
	boolean hasStandingGamePlayer(UUID uuid);
	List<GameTeam> getGameTeams();
	@Nullable GameTeam getGameTeamById(int teamId);
	List<GamePlayer> getGamePlayers();
	List<GamePlayer> getStandingGamePlayers();
	int getTotalMembers();
	int getStandingPlayerTeamCount();
	int getStandingTeamCount();
}
```

### 队伍管理

#### 队伍指令功能

- joinTeam：加入队伍
- joinTeamSpecific：加入指定队伍
- kickPlayer：将玩家踢出队伍
- invitePlayer：邀请玩家加入队伍
- requestPlayer：申请加入其他玩家队伍
- leaveTeam：离开队伍
> 游戏中离开队伍通常视为被淘汰，但**不**应从`游戏玩家`列表清除`GamePlayer`
```java
package xiao.battleroyale.api.game.team;

public interface ITeamExternal {
	void joinTeam(ServerPlayer player);
	void joinTeamSpecific(ServerPlayer player, int teamId);
	void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer);
	void declineInvite(ServerPlayer player, ServerPlayer senderPlayer);
	void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void acceptRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
	void declineRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
	boolean leaveTeam(ServerPlayer player);
}
```

#### 原版队伍功能

（可选）按照`游戏队伍`构建Minecraft原版队伍，但原版队伍的变更**不**应影响游戏队伍
```java
package xiao.battleroyale.api.game.team;

public interface IVanillaTeam {
	void buildVanillaTeam(@Nullable ServerLevel serverLevel, boolean hideName);
	void clearVanillaTeam(@Nullable ServerLevel serverLevel);
}
```

## 区域管理器

- setStackZoneConfig：加载配置时叠加区域配置，用于快速测试多个区域配置合并后的效果
- randomizeZoneTickOffset：随机偏移区域功能延迟，均摊区域功能计算时间
- getCommonZoneContext：获取游戏区域更新上下文，包含`游戏玩家`列表
- getZoneContextInGame：获取游戏中的游戏区域更新上下文，包含`未被淘汰的游戏玩家`列表
```java
package xiao.battleroyale.api.game.zone;

public interface IZoneManager extends IGameSubManager, IGameZoneReadApi {
    void setStackZoneConfig(boolean turn);
    void randomizeZoneTickOffset();
    ZoneManager.ZoneContext getCommonZoneContext();
    ZoneManager.ZoneContext getZoneContextInGame();
}
```

#### 区域管理器启动流程

- initGameConfig：读取区域配置
- startGame：执行`randomizeZoneTickOffset`

### 获取游戏区域

游戏区域默认实现为`GameZone`，若需要替换则应基于`GameZone`的实现方式
- getGameZones：获取`游戏区域`列表
- getCurrentGameZones：获取当前`游戏时间`更新的`游戏区域`列表
- getCurrentGameZones：获取指定`游戏时间`更新的`游戏区域`列表
- getGameZone：通过`区域管理器`管理的**唯一区域ID**查询`游戏区域`，不小于0
> 默认`区域管理器`维护的**唯一区域ID**对应`GameZone`的**固定区域ID**，不建议修改该机制
```java
package xiao.battleroyale.api.game.zone;

public interface IGameZoneReadApi {
    List<IGameZone> getGameZones();
    List<IGameZone> getCurrentGameZones();
    List<IGameZone> getCurrentGameZones(int gameTime);
    @Nullable IGameZone getGameZone(int zoneId);
}
```

### 游戏区域

游戏区域包含3个可排列组合的类型
- [区域功能类型](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域功能词条)
- [区域形状类型](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域形状词条)
- [区域特殊类型](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#区域特殊词条)
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface IGameZone extends ITickableZone, ISpatialZone, IAdditionalZone {
}
```
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface ITickableZone {
}
```
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface ISpatialZone {
}
```
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface IAdditionalZone extends IZoneSpecialClient {
}
```

# English

The game framework is designed with a **Main Manager** orchestrating various **Sub-Managers**, splitting functionalities into distinct components:

|Game Sub-Manager|Corresponding Interface|Responsibility Summary|
|---|---|---|
|Game Manager (Main)|`IGameManager`|Orchestrates other Game Sub-Managers.|
|Gamerule Manager|`IGameruleManager`|Sets game rules and switches game modes.|
|Game Lobby Manager|`IGameLobbyManager`|Provides lobby teleportation and lobby invulnerability.|
|Game Loot Manager|`IGameLootManager`|Continuously executes loot generation.|
|Game Process Manager|`IGameProcessManager`|Updates game state and implements specific game types/flows.|
|Spawn Manager|`ISpawnManager`|Teleports players at the start of the game.|
|Statistics Manager|`IStatsManager`|Records game statistics.|
|Team Manager|`ITeamManager`|Manages game teams and game players.|
|Zone Manager|`IZoneManager`|Continuously updates game zones.|

### Game Sub-Manager

The common interface implemented by both the Main Manager and all Sub-Managers.

> When a command invokes the Main Manager to execute a function, the Main Manager should delegate the execution of the corresponding function to all Sub-Managers.
```java
package xiao.battleroyale.api.game;

public interface IGameSubManager {
	// Reads configuration
    void initGameConfig(ServerLevel serverLevel);
    boolean isConfigPrepared();
	// Initializes the game
    void initGame(ServerLevel serverLevel);
    boolean isReady();
	// Starts the game
    boolean startGame(ServerLevel serverLevel);
    // After the game starts
    void onGameTick(int gameTime);
    void stopGame(@Nullable ServerLevel serverLevel);
}
```

#### Game Lifecycle

Read Config → Initialize Game → Start Game → Continuous Operation / Early Termination
- initGameConfig: Reads game configuration from config files; no excessive operations should occur at this stage.
- initGame: Initializes the game, including registering relevant event handlers, centralizing the creation of player teams, pre-calculating performance-intensive tasks, etc.
- startGame: If the game successfully starts, the current `game time` is 0, the next tick the `game time` will be 1, and Sub-Managers are dispatched.
- onGameTick: Dispatches Sub-Managers every `game time` (game tick) to update the game state.
- stopGame: Forcefully stops the game without performing victory settlement/cleanup.

##### Special Flow

"One-Click Start Game": When executing `startGame`, if any Sub-Manager is not `isReady`, `initGame` should be executed automatically once. When executing `initGame`, if any Sub-Manager is not `isConfigPrepared`, `initGameConfig` should be executed automatically once.
> A check is immediately performed right after `initGame` and `startGame` are executed.

Sequential Initialization / Config Freshness: After the Main Manager successfully executes `initGame`, it will set itself to be `isReady` but not `isConfigPrepared`. This ensures that the next time `initGame` is called, it will be forced to re-execute `initGameConfig`, guaranteeing config freshness.

Player Count Check: If the Team Manager fails `isReady` after the Main Manager executes `initGame` (e.g., due to insufficient player count), a "Not enough players" message should be immediately prompted. If you do not want to prompt "Not enough players" during `initGame`, the player count check can be deferred until `startGame`.
> The mod's default Main Manager does not check the Team Manager's `isReady`, which will cause the "Not enough players" prompt but does not affect the check at the start of `startGame`.

## Game Manager

- Externally, only the static API `IGameManager` needs to be accessed. The concrete `IGameManager` class then delegates functionality layer by layer, without splitting `IGameManager` into different classes or adding permission checks._
- External calls are treated as having full permissions (even when only calling ordinary interfaces). Interfaces that should not be called externally have been annotated with `@Internal` or `@Deprecated`._
> _The interfaces inherited by `IGameManager` are simply functionalities categorized and broken down by the `Game Manager` itself, such as game management configuration reading and writing, game event handling (which is inherently the responsibility of `IGameManager`), and the setting of special states, etc._
> 
> _The default implementation classes of each `Game Sub-Manager` in this mod will use the `protected` keyword and extract function bodies to a proxy within the same package—a "facade pattern"—when the code is extensive. This ensures that each `Game Sub-Manager`'s corresponding interface provides a single, comprehensive API for its specific responsibility, just like `IGameManager`._
> 
> _The `IGameManager` "department" is open and shared. Visitors only need to know `IGameManager`, without needing to know how its internal resources are hierarchically managed, to directly access it, allow visitors to freely use its functions, and **debug on their own**._
```java
package xiao.battleroyale.api.game;

public interface IGameManager extends IGameMainManager, IGameApiGetter, IGameConfigGetter,  
        IGameConfigSetter, IGameStatusSetter, IGameEventReceiver {
}
```

### Game Main Manager

- The Main Manager orchestrates all Sub-Managers.
- Provides getter interfaces for all its Sub-Managers.
```java
package xiao.battleroyale.api.game;

public interface IGameMainManager extends IGameSubManager, IGameFunc, IGameInfoGetter {
	@NotNull IGameProcessManager getGameProcessManager();  
	@NotNull IGameruleManager getGameruleManager();  
	@NotNull IGameLootManager getGameLootManager();  
	@NotNull ISpawnManager getSpawnManager();  
	@NotNull IGameLobbyManager getGameLobbyManager();  
	@NotNull IStatsManager getStatsManager();  
	@NotNull ITeamManager getTeamManager();  
	@NotNull IZoneManager getZoneManager();
}
```

#### Game Main Manager Function

- spectateGame: Handled by the `Game Process Manager`. Returns whether the player can spectate the game and places the player into spectator mode.
- finishGame: Distinct from `stopGame`. Only the `Game Process Manager` is allowed to call this method, which immediately ends the game and settles the victor(s) if conditions are met.
```java
package xiao.battleroyale.api.game;

public interface IGameFunc {
    boolean spectateGame(ServerPlayer player);
    void finishGame(boolean hasWinner);
}
```

#### Game Information

- getGameTime: Gets the current `game time` in ticks.
- getGameId: Gets the current `Game ID` (UUID).
- getGlobalCenterOffset: Gets the `Global Offset` (for map switching/relocation).
- getWinnerTeamTotal: Gets the maximum number of winning teams allowed.
- getServerLevel: Gets the current game dimension/level.
```java
package xiao.battleroyale.api.game;

public interface IGameInfoGetter {
    int getGameTime();  
    UUID getGameId();
    Vec3 getGlobalCenterOffset();
    int getWinnerTeamTotal();
    ServerLevel getServerLevel();
}
```

### Game ID

The Game Manager creates a UUID as the `Game ID` for each game session, which is also used for:
- The `Game Loot Manager` writes the Game ID when refreshing items/spawning entities/updating block entities (for persistent storage) to determine if they have been refreshed/need cleanup.
- Allowing other mods to manually write the Game ID after refreshing items/spawning entities for marking, to **prevent** being **automatically cleaned up** by the `Game Loot Manager`.
```java
package xiao.battleroyale.api.game;

public interface IGameApiGetter {
	IGameIdReadApi getGameIdReadApi();
	IGameIdWriteApi getGameIdWriteApi();
}
```
```java
package xiao.battleroyale.api.game;

public interface IGameIdReadApi {
	@Nullable UUID getGameId(Entity entity);
	@Nullable UUID getGameId(BlockEntity blockEntity);
	@Nullable UUID getGameId(ItemStack itemStack);
}
```
```java
package xiao.battleroyale.api.game;

public interface IGameIdWriteApi {
    void addGameId(ItemStack itemStack, UUID gameId);
    void addGameId(Entity entity, UUID gameId);
    void addGameId(BlockEntity blockEntity, UUID gameId);
}
```

### Select Sub-Manager Configuration

This mod supports an arbitrary number of configuration presets, but only one can be selected per game session.
```java
package xiao.battleroyale.api.game;

public interface IGameConfigGetter {
	int getGameruleConfigId(); // Views the selected Gamerule Configuration ID
	int getSpawnConfigId(); // Views the selected Spawn Configuration ID
	// ...other configurations
}
```
After manually setting the config ID, `initGameConfig` must be called again to re-read the configuration.
```java
package xiao.battleroyale.api.game;

public interface IGameConfigSetter {
	boolean setGameruleConfigId(int gameId);
	boolean setSpawnConfigId(int id);
	// ...other configurations
}
```

### Set Game Manager Property

- The Game Step is used to **skip** `game time`, particularly for **quickly testing zone configurations**, and should **not** be used to speed up the game.
- The Game Manager caches a `Global Offset`, allowing the `Zone Manager` and `Spawn Manager` to easily switch maps and adjust spawn locations.
- Each game session updates its state within the same dimension (_ServerLevel_). Players who leave this dimension cannot retrieve the state or receive game messages (server → client communication).
```java
package xiao.battleroyale.api.game;

public interface IGameStatusSetter {
	boolean setGameStep(int step);
	boolean setGlobalCenterOffset(Vec3 offset);
	void setDefaultLevel(String defaultLevelKey);
}
```

### Game Event Handling

Game events are delegated to the `Game Process Manager` for handling; changing some rules can transform the game into a different game type.
> This interface is already called by the mod's event handler. The Game Manager should only send corresponding events before and after the Game Process Manager takes over.
> Changing event handling should be achieved by replacing the implementation of the Game Process Manager.
```java
package xiao.battleroyale.api.game;

public interface IGameEventReceiver {
    @ApiStatus.Internal void onPlayerLoggedIn(ServerPlayer player);
    @ApiStatus.Internal void onPlayerLoggedOut(ServerPlayer player);
    @ApiStatus.Internal void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity);
    @ApiStatus.Internal void onPlayerRevived(@NotNull GamePlayer gamePlayer);
    @ApiStatus.Internal void onPlayerDeath(@Nullable ILivingDeathEvent event, @NotNull GamePlayer gamePlayer);
}
```

## Gamerule Manager

#### Gamerule Manager Startup Flow

- initGameConfig: Reads game rule configurations, including MC vanilla rules (_/gamerule_), BattleRoyale rules, and other rule settings.
- initGame: Applies some game rules.
- startGame: Applies the remaining game rules, such as setting player game modes.

### Game Rule Property

#### Default Game Rule

> Can be ignored if replacing the `Gamerule Manager` and not requiring a uniform game mode for all players.
```java
package xiao.battleroyale.api.game.gamerule;
public interface IGameruleManager extends IGameSubManager {
	GameType getGameMode();
}
```

## Game Lobby Manager

#### Game Lobby Manager Startup Flow

- initGameConfig: Reads configuration to enable [Teleport to lobby](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Teleport-to-lobby).
- initGame: Decides whether to teleport players to the lobby based on configuration. The mod's default `Game Process Manager` has pre-scheduled the `Team Manager` to ensure that newly created players are acquired.
```java
package xiao.battleroyale.api.game.lobby;

public interface IGameLobbyManager extends IGameSubManager, IGameLobbyReadApi, ILobbyFuncApi {
}
```

#### Lobby Function

- healPlayer: Restores the game player's state.
- teleportToLobby: Teleports back to the lobby.
```java
package xiao.battleroyale.api.utilitity;

public interface ILobbyFuncApi {
	void healPlayer(@NotNull LivingEntity livingEntity);
	boolean teleportToLobby(@NotNull LivingEntity livingEntity);
}
```

#### Lobby API

- lobbyLevelKey: The _ResourceKey_ of the dimension where the lobby is located.
- canMuteki: Whether the current position and state allow the entity to be protected by **Lobby Invulnerability**.
```java
package xiao.battleroyale.api.game.lobby;

public interface IGameLobbyReadApi extends ILobbyReadApi {
	void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner);
}
```
```java
package xiao.battleroyale.api.utilitity;

public interface ILobbyReadApi {
    ResourceKey<Level> lobbyLevelKey();
    Vec3 lobbyPos();
    Vec3 lobbyDimension();
    
    boolean isInLobbyRange(Vec3 pos);  
    boolean canMuteki(@NotNull LivingEntity livingEntity);
}
```

## Game Loot Manager

```java
package xiao.battleroyale.api.game.loot;

public interface IGameLootManager extends IGameSubManager, IGameLootConfigGetter, IGameLootStatus, IGameLootTester, IGameLootOperator {
}
```

#### Loot Generator Configuration

- The mod's default loot generator uses asynchronous BFS + a single-tick chunk generation limit (distributing calculation load) to improve performance.
> - Iterates layer by layer, starting from each player's position as the BFS initial queue, to ensure the refresh is as fair as possible.
> - Requires safe handling of extra threads upon server shutdown
```java
package xiao.battleroyale.api.game.loot;

public interface IGameLootConfigGetter {
    int getMaxLootChunkPerTick();
    int getMaxLootDistance();
    int getTolerantCenterDistance();
    int getMaxCachedCenter();
    int getMaxQueuedChunk();
    int getBfsFrequency();
    boolean isInstantNextBfs();
    int getMaxCachedLootChunk();
    int getCleanCachedChunk();
    
    int getSimulationDistance();
}
```
```java
package xiao.battleroyale.api.game.loot;

public interface IGameLootOperator {
	void awaitTerminationOnShutdown();
}
```

## Game Process Manager

- checkIfGameShouldEnd: Performs a complete check of the game status; if conditions are met, it directly calls the `Game Manager`'s main functions to end the game and proceed with victory settlement.
```java
package xiao.battleroyale.api.game.process;

public interface IGameProcessManager extends IGameSubManager, IGameManagement, IGameNotification, IGameEventHandler {
    void checkIfGameShouldEnd();
}
```

#### Game Process Manager Startup Flow

- startGame: Executes `checkAndUpdateInvalidGamePlayer` once for use when `game time` is 1, and clears invalid players.

### Game Process Management

#### Game Status Management

- checkAndUpdateInvalidGamePlayer: Checks all players, updates offline duration and last valid position, and handles invalid players. 
- teleportToLobbyInGame: Player manually [Teleport to lobby](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Teleport-to-lobby), which should typically be treated as leaving the game and resulting in elimination.
- teleportAfterGame: Performs a single teleportation for winning players and remaining players after the game ends.
- spectateGame: Can only be called by the `Game Manager`. Returns whether the player can spectate the game and enables spectator mode for the player.
- healGamePlayers: Called by the `Game Manager` at the start of the game to restore the state of all game players.
- finishGameAddWinner: Settles the winning players, calling the `Game Manager` to add winning players and teams.
```java
package xiao.battleroyale.api.game.process;

public interface IGameManagement {
    void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel);
    void teleportToLobbyInGame(ServerPlayer player);
    void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,  
                           boolean teleportWinnerAfterGame, boolean teleportAfterGame);
    boolean spectateGame(ServerPlayer player);
    void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers); 
    void finishGameAddWinner(boolean hasWinner);  
}
```

#### Game Event Handling

Events dispatched by the `Game Manager`. For example, in a BattleRoyale game, when a player dies, the corresponding game player is eliminated.
> - This interface can only be called by the `Game Manager`.
> - If there are special mechanisms (e.g., a Battle Royale game might treat a downed event as an elimination event), it should call the Game Manager which then dispatches the relevant events.
```java
package xiao.battleroyale.api.game.process;

public interface IGameEventHandler {
	void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate);
	void onPlayerLoggedOut(boolean isInGame, ServerPlayer player);
	void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, LivingEntity livingEntity, boolean removeInvalidTeam);
	void onPlayerDeath(@Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void onPlayerRevived(@NotNull GamePlayer gamePlayer);
}
```

#### Sending Game Message

- sendWinnerResult: Sends the victory settlement information in the chat.
- notifyWinner: Sends a message to the winning players.
- sendGameSpectateMessage: Provides a shortcut for the spectate game command.
- sendDownMessage: Chat message when a game player is downed.
- sendReviveMessage: Chat message when a game player is revived.
- sendEliminateMessage: Chat message when a game player is eliminated.
```java
package xiao.battleroyale.api.game.process;

public interface IGameNotification {
	void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams, int gameTime);
	void notifyWinner(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, int winnerParticleId);
	void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate);
	void sendDownMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void sendReviveMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void sendEliminateMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
}
```

## Spawn Manager

#### Spawn Manager Startup Flow

- initGameConfig: Reads game spawner configuration.
- initGame: Initializes the game spawner.

#### Game Player Spawner

> `getGameSpawner()` should only be used for debugging purposes.
```java
package xiao.battleroyale.api.game.spawn;

public interface ISpawnManager extends IGameSubManager {
    @Deprecated(forRemoval = false) IGameSpawner getGameSpawner();
}
```

## Statistics Manager

#### Statistics Manager Startup Flow

- initGameConfig: Reads game statistics configuration.
- initGame: Clears old statistics data.
- startGame: Starts recording statistics data.
> If the game fails to start, `initGame` should normally be executed again to reset the statistics data.

### Statistical Game Data

```java
package xiao.battleroyale.api.game.stats;

public interface IStatsManager extends IGameSubManager, IGameEventStatsRecorder, IStatsQuery,
IZoneStatsRecorder, IGameruleStatsRecorder, ISpawnStatsRecorder {
    String getStatsFilePath();
    void saveStats(String filePath);  
}
```

#### Statistical Game Event Data

```java
package xiao.battleroyale.api.game.stats;

public interface IGameEventStatsRecorder {
    void onRecordDamage(GamePlayer gamePlayer, DamageSource damageSource, float damage);
    void onRecordDamage(GamePlayer gamePlayer, ILivingDamageEvent livingDamageEvent);
    void onRecordInstantRevive(GamePlayer gamePlayer, ILivingDeathEvent event);
    void onRecordDown(GamePlayer gamePlayer, ILivingDeathEvent event);
    void onRecordKill(GamePlayer gamePlayer, ILivingDeathEvent event);
}
```

#### Statistical Game Sub-Manager Data

```java
package xiao.battleroyale.api.game.stats;

public interface IZoneStatsRecorder {
}
```
```java
package xiao.battleroyale.api.game.stats;

public interface IGameruleStatsRecorder {
}
```
```java
package xiao.battleroyale.api.game.stats;

public interface ISpawnStatsRecorder {
}
```

#### Query Game Statistics Data

```java
package xiao.battleroyale.api.game.stats;

public interface IStatsQuery {
    void getGamePlayerStats(int playerId);
    void getGamePlayerStats(UUID playerUUID);
    void getGamePlayerStats(String playerName);
    void getGameTeamStats(int teamId);
    void getGameruleStats(String gameruleName);
}
```

## Team Manager

- shouldAutoJoin: Whether to automatically execute [Join a new team](https://github.com/XColorful/BattleRoyale/wiki/Team-command#Join-a-new-team).
- hasEnoughPlayerTeamToStart: Whether the current number of game teams is sufficient to start the game.
```java
package xiao.battleroyale.api.game.team;

public interface ITeamManager extends IGameSubManager, IGameTeamReadApi, ITeamExternal, ITeamManagement, ITeamPreManagement, ITeamNotification, IVanillaTeam {
	boolean shouldAutoJoin();
	boolean hasEnoughPlayerTeamToStart();
}
```

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
```java
package xiao.battleroyale.api.game.team;

public interface IGameTeamReadApi {
	@Nullable GamePlayer getGamePlayerByUUID(UUID uuid);
	@Nullable GamePlayer getGamePlayerBySingleId(int playerId);
	boolean hasStandingGamePlayer(UUID uuid);
	List<GameTeam> getGameTeams();
	@Nullable GameTeam getGameTeamById(int teamId);
	List<GamePlayer> getGamePlayers();
	List<GamePlayer> getStandingGamePlayers();
	int getTotalMembers();
	int getStandingPlayerTeamCount();
	int getStandingTeamCount();
}
```

### Team Management

#### Team Command Function

- joinTeam: Joins a team.
- joinTeamSpecific: Joins a specific team. 
- kickPlayer: Kicks a player from the team. 
- invitePlayer: Invites a player to join the team. 
- requestPlayer: Requests to join another player's team.  
- leaveTeam: Leaves the team.
> Leaving a team during the game is usually treated as elimination, but the `GamePlayer` object should **not** be cleared from the list of `Game Players`.
```java
package xiao.battleroyale.api.game.team;

public interface ITeamExternal {
	void joinTeam(ServerPlayer player);
	void joinTeamSpecific(ServerPlayer player, int teamId);
	void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer);
	void declineInvite(ServerPlayer player, ServerPlayer senderPlayer);
	void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void acceptRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
	void declineRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
	boolean leaveTeam(ServerPlayer player);
}
```

#### Vanilla Team Function

(Optional) Builds Minecraft vanilla teams according to the `Game Teams`, but changes to vanilla teams should **not** affect game teams.
```java
package xiao.battleroyale.api.game.team;

public interface IVanillaTeam {
	void buildVanillaTeam(@Nullable ServerLevel serverLevel, boolean hideName);
	void clearVanillaTeam(@Nullable ServerLevel serverLevel);
}
```

## Zone Manager

- setStackZoneConfig: Stacks zone configurations when loading to quickly test the combined effect of multiple zone configurations.
- randomizeZoneTickOffset: Randomly offsets the zone function delay to distribute the zone function calculation time (load balancing).  
- getCommonZoneContext: Gets the game zone update context, including the list of `Game Players`.  
- getZoneContextInGame: Gets the in-game zone update context, including the list of `Non-Eliminated Game Players`.  
```java
package xiao.battleroyale.api.game.zone;

public interface IZoneManager extends IGameSubManager, IGameZoneReadApi {
    void setStackZoneConfig(boolean turn);
    void randomizeZoneTickOffset();
    ZoneManager.ZoneContext getCommonZoneContext();
    ZoneManager.ZoneContext getZoneContextInGame();
}
```

#### Zone Manager Startup Flow

- initGameConfig: Reads zone configuration.
- startGame: Executes `randomizeZoneTickOffset`.

### Get Game Zone

The default implementation of a game zone is `GameZone`. If replacement is needed, it should be based on the implementation of `GameZone`.
- getGameZones: Gets the list of `Game Zones`. 
- getCurrentGameZones: Gets the list of `Game Zones` that update at the current `game time`.
- getCurrentGameZones: Gets the list of `Game Zones` that update at the specified `game time`.
- getGameZone: Queries a `Game Zone` by the **Unique Zone ID** managed by the `Zone Manager` (must be ≥ 0).
> By default, the **Unique Zone ID** maintained by the `Zone Manager` corresponds to the **Fixed Zone ID** of the `GameZone`. Modifying this mechanism is not recommended.
```java
package xiao.battleroyale.api.game.zone;

public interface IGameZoneReadApi {
    List<IGameZone> getGameZones();
    List<IGameZone> getCurrentGameZones();
    List<IGameZone> getCurrentGameZones(int gameTime);
    @Nullable IGameZone getGameZone(int zoneId);
}
```

### Game Zone

A Game Zone includes 3 combinable types:
- [Zone Function type](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-function-entry)
- [Zone Shape type](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-shape-entry)
- [Zone Special type](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#Zone-special-entry)
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface IGameZone extends ITickableZone, ISpatialZone, IAdditionalZone {
}
```
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface ITickableZone {
}
```
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface ISpatialZone {
}
```
```java
package xiao.battleroyale.api.game.zone.gamezone;

public interface IAdditionalZone extends IZoneSpecialClient {
}
```