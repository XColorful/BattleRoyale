[English](#English)

# 保存配置
_/battleroyale save [loot/game/effect/client/server] [subType]_

需要权限等级3

> 目前没有游戏内修改配置的功能，若无拓展模组，请使用[备份配置](https://github.com/XColorful/BattleRoyale/wiki/Backup-command)

将已加载的配置文件保存至 _./minecraft/config/battleroyale_
- **覆盖**已有文件
- 不删除各路径下其他文件
- [重载配置](https://github.com/XColorful/BattleRoyale/wiki/Reload-command)时包含**舍弃部分无效词条**、**自动补充默认值**的操作，新写入的配置**不一定与原先相同**

## 保存全部配置
_/battleroyale save_

### 全部物资刷新配置
_/battleroyale save loot [loot_spawner/entity_spawner/airdrop/airdrop_special/secret_room]_

将已加载的物资刷新配置保存至 _./minecraft/config/battleroyale/loot_

_/battleroyale save loot_
处理所有子命令

#### 物资刷新器
_/battleroyale save loot loot_spawner_

将已加载的物资刷新器配置保存至 _./minecraft/config/battleroyale/loot/loot_spawner_

#### 实体生成器
_/battleroyale save loot entity_spawner_

将已加载的实体生成器配置保存至 _./minecraft/config/battleroyale/loot/entity_spawner_

#### 补给箱
_/battleroyale save loot airdrop_

将已加载的补给箱配置保存至 _./minecraft/config/battleroyale/loot/airdrop_

#### 特殊补给箱
_/battleroyale save loot airdrop_special_

将已加载的特殊补给箱配置保存至 _./minecraft/config/battleroyale/loot/airdrop_special_

#### 神秘点位
_/battleroyale save loot secret_room_

将已加载的神秘点位配置保存至 _./minecraft/config/battleroyale/loot/secret_room_

### 全部大逃杀游戏配置
_/battleroyale save game [gamerule/spawn/zone/bot]_

将已加载的大逃杀游戏配置保存至 _./minecraft/config/battleroyale/game_

_/battleroyale save game_
处理所有子命令

#### 游戏规则配置

_/battleroyale save game gamerule_

将已加载的游戏规则配置保存至 _./minecraft/config/battleroyale/game/gamerule_

#### 出生配置
_/battleroyale save game spawn_

将已加载的出生配置保存至 _./minecraft/config/battleroyale/game/spawn_

#### 区域配置
_/battleroyale save game zone_

将已加载的区域配置保存至 _./minecraft/config/battleroyale/game/zone_

#### 人机配置
_/battleroyale save game bot_

将已加载的人机配置保存至 _./minecraft/config/battleroyale/game/bot_

### 全部效果配置
_/battleroyale save effect [particle]_

将已加载的效果配置保存至 _./minecraft/config/battleroyale/effect_

_/battleroyale save effect_
处理所有子命令

#### 粒子配置
_/battleroyale save effect particle_

将已加载的粒子配置保存至 _./minecraft/config/battleroyale/effect/particle_

### 全部客户端配置
_/battleroyale save client [render/display]_

**客户端指令不需要权限等级**

将已加载的客户端配置保存至 _./minecraft/config/battleroyale/client_

_/battleroyale save client_
处理所有子命令

#### 渲染配置
_/battleroyale save client render_

将已加载的渲染配置保存至 _./minecraft/config/battleroyale/client/render_

#### 显示配置
_/battleroyale save client display_

将已加载的显示配置保存至 _./minecraft/config/battleroyale/client/display_

### 全部服务端配置
_/battleroyale save server [performance/utility]_

将已加载的服务端配置保存至 _./minecraft/config/battleroyale/server_

_/battleroyale save server_
处理所有子命令

#### 性能配置
_/battleroyale save server performance_

将已加载的性能配置保存至 _./minecraft/config/battleroyale/server/performance_

#### 实用配置
_/battleroyale save server utility_

将已加载的实用配置保存至 _./minecraft/config/battleroyale/server/utility_

# English
_/battleroyale save [loot/game/effect/client/server] [subType]_

Require permission level 3

> There is currently no in-game config modification feature. If there are no extension mods, please use the [Backup command](https://github.com/XColorful/BattleRoyale/wiki/Backup-command#English).

Save the currently loaded config files to _./minecraft/config/battleroyale_
- **Overwrite** existing files
- Do not delete other files in each path
- [Reload command](https://github.com/XColorful/BattleRoyale/wiki/Reload-command#English) includes operations like **discarding some invalid entries** and **automatically supplementing default values**, so the newly written configs **may not be exactly the same as the original ones**

## Save all config
_/battleroyale save_

Save all loaded config files to _./minecraft/config/battleroyale_

### All loot Config
_/battleroyale save loot [loot_spawner/entity_spawner/airdrop/airdrop_special/secret_room]_

Save loaded loot configs to _./minecraft/config/battleroyale/loot_

_/battleroyale save loot_
Process all sub commands

#### Loot spawner
_/battleroyale save loot loot_spawner_

Save loaded loot spawner config to _./minecraft/config/battleroyale/loot/loot_spawner_

#### Entity spawner
_/battleroyale save loot entity_spawner_

Save loaded entity spawner config to _./minecraft/config/battleroyale/loot/entity_spawner_

#### Airdrop
_/battleroyale save loot airdrop_

Save loaded airdrop config to _./minecraft/config/battleroyale/loot/airdrop_

#### Special airdrop
_/battleroyale save loot airdrop_special_

Save loaded special airdrop config to _./minecraft/config/battleroyale/loot/airdrop_special_

#### Secret room
_/battleroyale save loot secret_room_

Save loaded secret room config to _./minecraft/config/battleroyale/loot/secret_room_

### All BattleRoyale game config
_/battleroyale save game [gamerule/spawn/zone/bot]_

Save loaded BattleRoyale game configs to _./minecraft/config/battleroyale/game_

_/battleroyale save game_
Process all sub commands

#### Gamerule config
_/battleroyale save game gamerule_

Save loaded gamerule config to _./minecraft/config/battleroyale/game/gamerule_

#### Spawn config
_/battleroyale save game spawn_

Save loaded spawn config to _./minecraft/config/battleroyale/game/spawn_

#### Zone config
_/battleroyale save game zone_

Save loaded zone config to _./minecraft/config/battleroyale/game/zone_

#### Bot config
_/battleroyale save game bot_

Save loaded bot config to _./minecraft/config/battleroyale/game/bot_

### All effect config
_/battleroyale save effect [particle]_

Save loaded effect configs to _./minecraft/config/battleroyale/effect_

_/battleroyale save effect_

Process all sub commands

#### Particle config
_/battleroyale save effect particle_

Save loaded particle config to _./minecraft/config/battleroyale/effect/particle_

### All client config
_/battleroyale save client [render/display]_

**Client command doesn't require permission level**

Save loaded client configs to _./minecraft/config/battleroyale/client_

_/battleroyale save client_
Process all sub commands

#### Render config
_/battleroyale save client render_

Save loaded render config to _./minecraft/config/battleroyale/client/render_

#### Display config
_/battleroyale save client display_

Save loaded display config to _./minecraft/config/battleroyale/client/display_

### All server config
_/battleroyale save server [performance/utility]_

Save loaded server configs to _./minecraft/config/battleroyale/server_

_/battleroyale save server_
Process all sub commands

#### Performance config
_/battleroyale save server performance_

Save loaded performance config to _./minecraft/config/battleroyale/server/performance_

#### Utility config
_/battleroyale save server utility_

Save loaded utility config to _./minecraft/config/battleroyale/server/utility_