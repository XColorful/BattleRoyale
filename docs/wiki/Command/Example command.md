[English](#English)

# 生成配置
_/battleroyale example [loot/game/effect/client/server] [subType]_

需要权限等级2

在 _./minecraft/config/battleroyale_ 的子文件夹中写入示例配置文件以及特殊预设
- 示例配置文件仅用于展示如何编写配置文件
- 特殊预设面向整合包/其他模组

## 写入配置文件
_/battleroyale example [loot/game/effect/client/server] [subType]_

- 不保留重名配置
- 写入配置后不自动重载配置

### 全部物资刷新配置
_/battleroyale example loot [loot_spawner/entity_spawner/airdrop/airdrop_special/secret_room]_

在 _./minecraft/config/battleroyale/loot_ 下写入配置文件

_/battleroyale example loot_
处理所有子命令

#### 物资刷新器
_/battleroyale example loot loot_spawner_

在 _./minecraft/config/battleroyale/loot/loot_spawner_ 下写入配置文件
- 该示例配置文件作为通用刷新配置示例
- 当任意版本[TaCZ](https://github.com/MCModderAnchor/TACZ/tree/1.20.1)加载时，额外生成以[TaCZ](https://github.com/MCModderAnchor/TACZ/tree/1.20.1)默认枪包为主的物资刷新配置，适用于[完整自定义大逃杀](https://github.com/XColorful/Custom-BattleRoyale-Complete)

#### 实体生成器
_/battleroyale example loot entity_spawner_

在 _./minecraft/config/battleroyale/loot/entity_spawner_ 下写入配置文件
- 额外生成以原版坐骑为主的载具配置

#### 补给箱
_/battleroyale example loot airdrop_

在 _./minecraft/config/battleroyale/loot/airdrop_ 下写入配置文件

#### 特殊补给箱
_/battleroyale example loot airdrop_special_

在 _./minecraft/config/battleroyale/loot/airdrop_special_ 下写入配置文件

#### 神秘点位
_/battleroyale example loot secret_room_

在 _./minecraft/config/battleroyale/loot/secret_room_ 下写入配置文件

### 全部大逃杀游戏配置
_/battleroyale example game [gamerule/spawn/zone/bot]_

在 _./minecraft/config/battleroyale/game_ 下写入配置文件

_/battleroyale example game_
处理所有子命令

#### 游戏规则配置
_/battleroyale example game gamerule_

在 _./minecraft/config/battleroyale/game/gamerule_ 下写入配置文件
- 额外生成经典大逃杀的规则配置（100/64玩家，队伍规模4人）

#### 出生配置
_/battleroyale example game spawn_

在 _./minecraft/config/battleroyale/game/spawn_ 下写入配置文件
- 额外生成经典大逃杀的出生配置（经典8000x8000、半径2670、半径881）

#### 区域配置

_/battleroyale example game zone_

在 _./minecraft/config/battleroyale/game/zone_ 下写入配置文件
- 额外生成两种经典大逃杀8000x8000区域配置
- 额外生成经典大逃杀5340x5340区域配置
- 额外生成经典大逃杀881x881区域配置

#### 人机配置

_/battleroyale example game bot_

在 _./minecraft/config/battleroyale/game/bot_ 下写入配置文件

### 全部效果配置
_/battleroyale example effect [particle]_

在 _./minecraft/config/battleroyale/effect_ 下写入配置文件

_/battleroyale example effect_
处理所有子命令

#### 粒子配置
_/battleroyale example effect particle_

在 _./minecraft/config/battleroyale/effect/particle_ 下写入配置文件

### 全部客户端配置
_/battleroyale example client [render/display]_

**客户端指令不需要权限等级**

在 _./minecraft/config/battleroyale/client_ 下写入配置文件

_/battleroyale example client_
处理所有子命令

#### 渲染配置
_/battleroyale example client render_

在 _./minecraft/config/battleroyale/client/render_ 下写入配置文件

#### 显示配置
_/battleroyale example example display_

在 _./minecraft/config/battleroyale/client/display_ 下写入配置文件

### 全部服务端配置
_/battleroyale example server [performance/profile/utility]_

在 _./minecraft/config/battleroyale/server_ 下写入配置文件

_/battleroyale example server_
处理所有子命令

#### 性能配置
_/battleroyale example server performance_

在 _./minecraft/config/battleroyale/server/performance_ 下写入配置文件

#### 预设配置
_/battleroyale example server profile_

在 _./minecraft/config/battleroyale/server/profile_ 下写入配置文件

#### 实用配置
_/battleroyale example server utility_

在 _./minecraft/config/battleroyale/server/utility_ 下写入配置文件

# English
_/battleroyale example [loot/game/effect/client/server] [subType]_

Requires permission level 2

Writes example configuration files and special presets into subfolders of _./minecraft/config/battleroyale_.
- Example configuration files are only for demonstrating how to write configuration files.
- Special presets are for modpacks/other mods.

## Write Configuration Files
_/battleroyale example [loot/game/effect/client/server] [subType]_

- Does not preserve configurations with duplicate names.
- Does not automatically reload configurations after writing.

### All Loot Config
_/battleroyale example loot [loot_spawner/entity_spawner/airdrop/airdrop_special/secret_room]_

Writes configuration files under _./minecraft/config/battleroyale/loot/_.

_/battleroyale example loot_
Processes all subcommands.

#### Loot Spawner
_/battleroyale example loot loot_spawner_

Writes configuration files under _./minecraft/config/battleroyale/loot/loot_spawner/_.
- This example configuration file serves as a general loot config example.
- When any version of [TaCZ](https://github.com/MCModderAnchor/TACZ/tree/1.20.1) is loaded, additionally generates loot configurations primarily based on the [TaCZ](https://github.com/MCModderAnchor/TACZ/tree/1.20.1) default gun pack, suitable for [Custom BattleRoyale Complete](https://github.com/XColorful/Custom-BattleRoyale-Complete).

#### Entity Spawner
_/battleroyale example loot entity_spawner_

Writes configuration files under _./minecraft/config/battleroyale/loot/entity_spawner/_.
- Additionallly generates vehicle configurations based on vanilla mounts

#### Airdrop
_/battleroyale example loot airdrop_

Writes configuration files under _./minecraft/config/battleroyale/loot/airdrop/_.

#### Special Airdrop
_/battleroyale example loot airdrop_special_

Writes configuration files under _./minecraft/config/battleroyale/loot/airdrop_special/_.

#### Secret Room
_/battleroyale example loot secret_room_

Writes configuration files under _./minecraft/config/battleroyale/loot/secret_room/_.

### All BattleRoyale game config
_/battleroyale example game [gamerule/spawn/zone/bot]_

Writes configuration files under _./minecraft/config/battleroyale/game/_.

_/battleroyale example game_
Processes all subcommands.

#### Gamerule config
_/battleroyale example game gamerule_

Writes configuration files under _./minecraft/config/battleroyale/game/gamerule/_.

- Additionally generates classic BattleRoyale rule configurations (100/64 players, team size 4).

#### Spawn config
_/battleroyale example game spawn_

Writes configuration files under _./minecraft/config/battleroyale/game/spawn/_.
- Additionally generates classic BattleRoyale spawn configurations (classic 8000x8000, radius 2670, radius 881).

#### Zone config
_/battleroyale example game zone_

Writes configuration files under _./minecraft/config/battleroyale/game/zone/_.
- Additionally generates two classic BattleRoyale 8000x8000 zone configurations.
- Additionally generates classic BattleRoyale 5340x5340 zone configuration.
- Additionally generates classic BattleRoyale 881x881 zone configuration.

#### Bot config
_/battleroyale example game bot_

Writes configuration files under _./minecraft/config/battleroyale/game/bot/_.

### All effect config
_/battleroyale example effect [particle]_

Writes configuration files under _./minecraft/config/battleroyale/effect/_.

_/battleroyale example effect_
Processes all subcommands.

#### Particle config
_/battleroyale example effect particle_

Writes configuration files under _./minecraft/config/battleroyale/effect/particle/_.

### All client config
_/battleroyale example client [render/display]_

**Client commands do not require permission level.**

Writes configuration files under _./minecraft/config/battleroyale/client/_.

_/battleroyale example client_
Processes all subcommands.

#### Render config
_/battleroyale example client render_

Writes configuration files under _./minecraft/config/battleroyale/client/render/_.

#### Display config
_/battleroyale example client display_

Writes configuration files under _./minecraft/config/battleroyale/client/display/_.

### All server config
_/battleroyale example server [performance/profile/utility]_

Writes configuration files under _./minecraft/config/battleroyale/server/_.

_/battleroyale example server_
Processes all subcommands.

#### Performance config
_/battleroyale example server performance_

Writes configuration files under _./minecraft/config/battleroyale/server/performance/_.

#### Profile config
_/battleroyale example server profile_

Writes configuration files under _./minecraft/config/battleroyale/server/profile/_.

#### Utility config
_/battleroyale example server utility_

Writes configuration files under
_./minecraft/config/battleroyale/server/utility_.