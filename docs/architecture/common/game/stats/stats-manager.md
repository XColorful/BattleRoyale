[English](#English)

## 统计管理器

#### 统计管理器启动流程

- initGameConfig：读取游戏统计数据配置
- initGame：清除旧统计数据
- startGame：开始记录统计数据
> 若开始游戏失败，正常情况下应会再次执行`initGame`以重置统计数据

### 统计游戏数据

[![IStatsManager](/docs/api/game/stats/IStatsManager.md)](/docs/api/game/stats/IStatsManager.md)

#### 统计游戏事件数据

[![IGameEventStatsRecorder](/docs/api/game/stats/IGameEventStatsRecorder.md)](/docs/api/game/stats/IGameEventStatsRecorder.md)

#### 统计游戏子管理器数据

[![IZoneStatsRecorder](/docs/api/game/stats/IZoneStatsRecorder.md)](/docs/api/game/stats/IZoneStatsRecorder.md)

[![IGameruleStatsRecorder](/docs/api/game/stats/IGameruleStatsRecorder.md)](/docs/api/game/stats/IGameruleStatsRecorder.md)

[![ISpawnStatsRecorder](/docs/api/game/stats/ISpawnStatsRecorder.md)](/docs/api/game/stats/ISpawnStatsRecorder.md)

#### 查询游戏统计数据

[![IStatsQuery](/docs/api/game/stats/IStatsQuery.md)](/docs/api/game/stats/IStatsQuery.md)

# English

## Statistics Manager

#### Statistics Manager Startup Flow

- initGameConfig: Reads game statistics configuration.
- initGame: Clears old statistics data.
- startGame: Starts recording statistics data.
> If the game fails to start, `initGame` should normally be executed again to reset the statistics data.

### Statistical Game Data

[![IStatsManager](/docs/api/game/stats/IStatsManager.md)](/docs/api/game/stats/IStatsManager.md)

#### Statistical Game Event Data

[![IGameEventStatsRecorder](/docs/api/game/stats/IGameEventStatsRecorder.md)](/docs/api/game/stats/IGameEventStatsRecorder.md)

#### Statistical Game Sub-Manager Data

[![IZoneStatsRecorder](/docs/api/game/stats/IZoneStatsRecorder.md)](/docs/api/game/stats/IZoneStatsRecorder.md)

[![IGameruleStatsRecorder](/docs/api/game/stats/IGameruleStatsRecorder.md)](/docs/api/game/stats/IGameruleStatsRecorder.md)

[![ISpawnStatsRecorder](/docs/api/game/stats/ISpawnStatsRecorder.md)](/docs/api/game/stats/ISpawnStatsRecorder.md)

#### Query Game Statistics Data

[![IStatsQuery](/docs/api/game/stats/IStatsQuery.md)](/docs/api/game/stats/IStatsQuery.md)