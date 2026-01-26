[English](#English)

# 配置文件说明

配置文件目录位于 _./minecraft/config/battleroyale_

模组会尝试读取每个配置目录下所有 .json 文件
一般配置文件都具有：
- id：配置唯一id，遇到重复id则覆盖，不小于0
- default：当配置文件里任意单个配置设置为true，则默认选用该文件/配置（该项可选，默认为 _false_ ）
- name：给配置起名，可重复
- color：用途不同的颜色，可使用 _\#RRGGBB_ 或 _\#RRGGBBAA_

一个配置文件内可包含多个配置，用方括号包围，内部每个花括号为一项配置

### 物资刷新配置

位于 _./minecraft/config/battleroyale/loot_ 下的子文件夹：
- airdrop：补给箱配置文件
- airdrop_special：特殊补给箱配置文件
- entity_spawner：实体生成器配置文件
- loot_spawner：物资刷新器配置文件
- secret_room：暂未使用

以上刷新配置均使用[通用刷新配置](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config)

### 大逃杀游戏配置

位于 _./minecraft/config/battleroyale/game_ 下的子文件夹：
- gamerule：[游戏规则配置](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config)
- spawn：[出生配置](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config)
- zone：[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config)
- bot：[人机配置](https://github.com/XColorful/BattleRoyale/wiki/Bot-config)

### 效果配置

位于 _./minecraft/config/battleroyale/effect_ 下的子文件夹：
- particle：[粒子配置](https://github.com/XColorful/BattleRoyale/wiki/Particle-config)

### 客户端配置

位于 _./minecraft/config/battleroyale/client_ 下的子文件夹：
- render：[渲染配置](https://github.com/XColorful/BattleRoyale/wiki/Render-config)
- display：[显示配置](https://github.com/XColorful/BattleRoyale/wiki/Display-config)

### 服务端配置

位于 _./minecraft/config/battleroyale/server_ 下的子文件夹：

- performance：[性能配置](https://github.com/XColorful/BattleRoyale/wiki/Performance-config)
- utility：[实用配置](https://github.com/XColorful/BattleRoyale/wiki/Utility-config)

# English

Configuration directory located in _./minecraft/config/battleroyale_

The mod will attempt to read every .json file in each config directory
Generally, the configuration will has:
- id: a unique id, overwrite if duplicate, not less than 0
- default: When any single configuration in the file is set to true, the file/config is selected by default (optional, defaults to _false_ )
- name: name the config, can be repeated
- color: color for different purposes, use _\#RRGGBB_ or _\#RRBBGGAA_

A config file can contain multiple configs, surrounded by square brackets, each curly brace inside is a single config

### Loot

Located in subfolders under _./minecraft/config/battleroyale/loot_
- airdrop
- airdrop_special
- entity_spawner: for battleroyale:entity_spawner
- loot_spawner: for battleroyale:loot_spawner
- secret_room: currently has no idea

All of them use the [General loot](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#English)

### BattleRoyale

Located in subfolders under _./minecraft/config/battleroyale/game_
- [gamerule](https://github.com/XColorful/BattleRoyale/wiki/Gamerule-config#English)
- [spawn](https://github.com/XColorful/BattleRoyale/wiki/Spawn-config#English)
- [zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English)
- [bot](https://github.com/XColorful/BattleRoyale/wiki/Bot-config#English)

### Effect

Located in subfolders under _./minecraft/config/battleroyale/effect_
- [particle](https://github.com/XColorful/BattleRoyale/wiki/Particle-config#English)

### Client

Located in subfolders under _./minecraft/config/battleroyale/client_
- [Render](https://github.com/XColorful/BattleRoyale/wiki/Render-config#English)
- [Display](https://github.com/XColorful/BattleRoyale/wiki/Display-config#English)

### Server

Located in subfolders under _./minecraft/config/battleroyale/server_

- [performance](https://github.com/XColorful/BattleRoyale/wiki/Performance-config#English)
- [utility](https://github.com/XColorful/BattleRoyale/wiki/Utility-config#English)