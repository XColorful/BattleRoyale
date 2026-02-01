[English](#English)

# 重载配置
_/battleroyale reload [loot/game/effect/client/server] [subType]_

需要权限等级2

从 _./minecraft/config/battleroyale_ 重新读取配置文件
- 不保存读取前的配置
- 跳过无效的词条
- 跳过无效的配置
- 跳过无效的文件
- 对于每个读取的配置类别，如最终没有已读取的配置则创建示例配置并再次读取

## 重载全部配置
_/battleroyale reload_

从 _./minecraft/config/battleroyale_ 重新读取配置文件

### 全部物资刷新配置
_/battleroyale reload loot [loot_spawner/entity_spawner/airdrop/airdrop_special_secret_room]_

从 _./minecraft/config/battleroyale/loot_ 重新读取配置文件

_/battleroyale reload loot_
处理所有子命令

#### 物资刷新器
_/battleroyale reload loot loot_spawner_

从 _./minecraft/config/battleroyale/loot/loot_spawner_ 重新读取配置文件
- 不更改世界中已有物资刷新器的lootId，不立即处理物资刷新

#### 实体生成器
_/battleroyale reload loot entity_spawner_

从  _./minecraft/config/battleroyale/loot/entity_spawner_ 重新读取配置文件
- 不更改世界中已有实体生成器的lootId，不立即处理实体生成

#### 补给箱
_/battleroyale reload loot airdrop_

从  _./minecraft/config/battleroyale/loot/airdrop_ 重新读取配置文件

#### 特殊补给箱
_/battleroyale reload loot airdrop_special_

从  _./minecraft/config/battleroyale/loot/airdrop_special_ 重新读取配置文件

#### 神秘点位
_/battleroyale reload loot secret_room_

从  _./minecraft/config/battleroyale/loot/secret_room_ 重新读取配置文件

### 全部大逃杀游戏配置
_/battleroyale reload game [gamerule/spawn/zone/bot]_

从 _./minecraft/config/battleroyale/game_ 重新读取配置文件

_/battleroyale reload game_
处理所有子命令

#### 游戏规则配置
_/battleroyale reload game gamerule_

从 _./minecraft/config/battleroyale/game/gamerule_ 重新读取配置文件


#### 出生配置
_/battleroyale reload game spawn_

从 _./minecraft/config/battleroyale/game/spawn_ 重新读取配置文件

#### 区域配置

_/battleroyale reload game zone_

从 _./minecraft/config/battleroyale/game/zone_ 重新读取配置文件

#### 人机配置

_/battleroyale reload game bot_

从 _./minecraft/config/battleroyale/game/bot_ 重新读取配置文件

### 全部效果配置
_/battleroyale reload effect [particle]_

从 _./minecraft/config/battleroyale/effect_ 重新读取配置文件

_/battleroyale reload effect_
处理所有子命令

#### 粒子配置
_/battleroyale reload effect particle_

从 _./minecraft/config/battleroyale/effect/particle_ 重新读取配置文件

### 全部客户端配置
_/battleroyale reload client [render/display]_

**客户端指令不需要权限等级**

从 _./minecraft/config/battleroyale/client_ 重新读取配置文件

_/battleroyale reload client_
处理所有子命令

#### 渲染配置
_/battleroyale reload client render_

从 _./minecraft/config/battleroyale/client/render_ 重新读取配置文件

#### 显示配置
_/battleroyale reload client display_

从 _./minecraft/config/battleroyale/client/display_ 重新读取配置文件

### 全部服务端配置
_/battleroyale reload server [performance/profile/utility]_

从 _./minecraft/config/battleroyale/server_ 重新读取配置文件

_/battleroyale reload server_
处理所有子命令

#### 性能配置
_/battleroyale reload server performance_

从 _./minecraft/config/battleroyale/server/performance_ 重新读取配置文件

#### 预设配置
_/battleroyale reload server profile_

从 _./minecraft/config/battleroyale/server/profile_ 重新读取配置文件

#### 实用配置
_/battleroyale reload server utility_

从 _./minecraft/config/battleroyale/server/utility_ 重新读取配置文件

# English
_/battleroyale reload [loot/game/effect/client/server] [subType]_

Require permission level 2

Re-read config files from _./minecraft/config/battleroyale_
- Do not save previous config
- Skip invalid entries
- Skip invalid configs
- Skip invalid config files
- For each config category read, if there is no config read in the end, create example configs and read again

## Reload all config
_/battleroyale reload_

Re-read config files from _./minecraft/config/battleroyale_

### All loot Config
_/battleroyale reload loot [loot_spawner/entity_spawner/airdrop/airdrop_special_secret_room]_

Re-read config files from _./minecraft/config/battleroyale/loot_

_/battleroyale reload loot_
Process all sub commands

#### Loot spawner
_/battleroyale reload loot loot_spawner_

Re-read config files from _./minecraft/config/battleroyale/loot/loot_spawner_
- Do not change the lootId of existing loot spawners in the world, and do not process loot refreshes immediately

#### Entity spawner
_/battleroyale reload loot entity_spawner_

Re-read config files from _./minecraft/config/battleroyale/loot/entity_spawner_

- Does not change lootId of existing entity spawners in the world, does not process entity spawns immediately

#### Airdrop
_/battleroyale reload loot airdrop_

Re-read config files from _./minecraft/config/battleroyale/loot/airdrop_

#### Special airdrop
_/battleroyale reload loot airdrop_special_

Re-read config files from _./minecraft/config/battleroyale/loot/airdrop_special_

#### Secret room
_/battleroyale reload loot secret_room_

Reread config files from _./minecraft/config/battleroyale/loot/secret_room_

### All BattleRoyale game config
_/battleroyale reload game [gamerule/spawn/zone/bot]_

Re-read config files from _./minecraft/config/battleroyale/game_

_/battleroyale reload game_
Process all sub commands

#### Gamerule config
_/battleroyale reload game gamerule_

Re-read config files from _./minecraft/config/battleroyale/game/gamerule_

#### Spawn config
_/battleroyale reload game spawn_

Re-read config files from _./minecraft/config/battleroyale/game/spawn_

#### Zone config

_/battleroyale reload game zone_

Re-read config files from _./minecraft/config/battleroyale/game/zone_

#### Bot config

_/battleroyale reload game bot_

Re-read config files from _./minecraft/config/battleroyale/game/bot_

### All effect config
_/battleroyale reload effect [particle]_

Re-read config files from _./minecraft/config/battleroyale/effect_

_/battleroyale reload effect_
Process all sub commands

#### Particle config
_/battleroyale reload effect particle_

Re-read config files from _./minecraft/config/battleroyale/effect/particle_

### All client config
_/battleroyale reload client [render/display]_

**Client command doesn't require permission level**

Re-read config files from _./minecraft/config/battleroyale/client_

_/battleroyale reload client_
Process all sub commands

#### Render config
_/battleroyale reload client render_

Re-read config files from _./minecraft/config/battleroyale/client/render_

#### Display config
_/battleroyale reload client display_

Re-read config files from _./minecraft/config/battleroyale/client/display_

### All server config
_/battleroyale reload server [performance/profile/utility]_

Re-read config files from _./minecraft/config/battleroyale/server_

_/battleroyale reload server_
Process all sub commands

#### Performance config
_/battleroyale reload server performance_

Re-read config files from _./minecraft/config/battleroyale/server/performance_

#### Profile config
_/battleroyale reload server profile_

Re-read configs files from _./minecraft/config/battleroyale/server/profile_

#### Utility config
_/battleroyale reload server utility_

Re-read config files from _./minecraft/config/battleroyale/server/utility_