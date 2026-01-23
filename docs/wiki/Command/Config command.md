# 切换配置
_/battleroyale config [loot/game/effect/client/server] [subType] [id/switch] [fileName]_

需要权限等级2

## 切换配置文件
_/battleroyale config [loot/game/effect/client/server] [subType] switch [fileName]_

更改使用的配置文件
- 不带 _fileName_ 则按文件名顺序切换至下一个
- 带 _fileName_ 则直接跳转到指定位置
- 更改配置文件后自动应用第一个配置

### 物资刷新配置
_/battleroyale config loot [loot_spawner/entity_spawner/airdrop/airdrop_special/secret_room] switch [fileName]_

- 物资刷新器配置文件：_/battleroyale config loot loot_spawner switch [fileName]_
- 实体生成器配置文件：_/battleroyale config loot entity_spawner switch [fileName]_
- 补给箱配置文件：_/battleroyale config loot airdrop switch [fileName]_
- 特殊补给箱配置文件：_/battleroyale config loot airdrop_special switch [fileName]_
- 神秘点位配置文件：_/battleroyale config loot secret_room switch [fileName]_

### 大逃杀游戏配置
_/battleroyale config game [bot/gamerule/spawn/zone] switch [fileName]_

- 人机配置文件：_/battleroyale config game bot switch [fileName]_
- 游戏规则配置文件：_/battleroyale config game gamerule switch [fileName]_
- 出生配置文件：_/battleroyale config game spawn switch [fileName]_
- 区域配置文件：_/battleroyale config game zone switch [fileName]_

> 区域配置文件可以用 _区域控制器_ 切换，无需权限等级

### 效果配置
_/battleroyale config effect [particle] switch [fileName]_

- 粒子配置文件：_/battleroyale config effect particle switch [fileName]_

### 客户端配置
_/battleroyale config client [render/display] switch [fileName]_

**客户端指令不需要权限等级**

- 渲染配置文件：_/battleroyale config client render switch [fileName]_
- 显示配置文件：_/battleroyale config client display switch [fileName]_

### 服务端配置
_/battleroyale config server [performance/utility] switch [fileName]_

- 性能配置文件：_/battleroyale config server performance switch [fileName]_
- 实用配置文件：_/battleroyale config server utility switch [fileName]_

## 切换大逃杀选中配置
_/battleroyale config game [bot/gamerule/spawn] [id]_

更改大逃杀配置文件中选用的配置id
- 默认均使用配置0
- 如不存在相应id的配置则不切换

- 人机配置： _/battleroyale config game bot [id]_
- 游戏规则配置： _/battleroyale config game gamerule [id]_
- 出生配置： _/battleroyale config game spawn [id]_

## 应用选中的客户端配置
_/battleroyale config client [render/display] [id]_

应用特定配置
- 如不存在相应id的配置则不应用

- 渲染配置： _/battleroyale config render [id]_
- 显示配置： _/battleroyale config display [id]_

> 区域渲染将在下一次接收服务端区域消息时生效，通常有几秒的延迟

## 应用选中的服务端配置
_/battleroyale config server [performance/utility] [id]_

应用特定配置
- 如不存在相应id的配置则不应用

- 性能配置：_/battleroyale config performance [id]_
- 实用配置：_/battleroyale config utility [id]_

# English
_/battleroyale config [loot/game/effect/client/server] [subType] [id/switch] [fileName]_

Require permission level 2

## Switch config file
_/battleroyale config [loot/game/effect/client/server] [subType] switch [fileName]_

Change the config file used
- Without _fileName_, switch to the next one in the order of file names
- With _fileName_, jump directly to the specified location
- Automatically apply the first config after changing the config file

### Loot config
_/battleroyale config loot [loot_spawner/entity_spawner/airdrop/airdrop_special/secret_room] switch [fileName]_

- Loot spawner config file: _/battleroyale config loot loot_spawner switch [fileName]_
- Entity spawner config file: _/battleroyale config loot entity_spawner switch [fileName]_
- Airdrop config file: _/battleroyale config loot airdrop switch [fileName]_
- Airdrop (Special) config file: _/battleroyale config loot airdrop_special switch [fileName]_
- Secret room config file: _/battleroyale config loot secret_room switch [fileName]_

### BattleRoyale config
_/battleroyale config game [bot/gamerule/spawn/zone] switch [fileName]_

- Bot config file: _/battleroyale config game bot switch [fileName]_
- Gamerule config file: _/battleroyale config game gamerule switch [fileName]_
- Spawn config file: _/battleroyale config game spawn switch [fileName]_
- Zone config file: _/battleroyale config game zone switch [fileName]_

> Zone config file can be switched with _Zone Controller_ , no permission level requried

### Effect config
_/battleroyale config effect [particle] switch [fileName]_

- Particle config file: _/battleroyale config effect particle switch [fileName]_

### Client config
_/battleroyale config client [render/display] switch [fileName]_

**Client command doesn't require permission level**

- Render config file: _/battleroyale config client render switch [fileName]_
- Display config file: _/battleroyale config client display switch [fileName]_

### Server config
_/battleroyale config server [performance/utility] switch [fileName]_

- Performance config file: _/battleroyale config server performance switch [fileName]_
- Utility config file: _/battleroyale config server utility switch [fileName]_

## Switch BattleRoyale selected config
_/battleroyale config game [bot/gamerule/spawn] [id]_

Change the configuration id used in the BattleRoyale game
- By default, configuration 0 is used
- If there is no configuration with the corresponding id, it will not be switched

- Bot config: _/battleroyale config bot [id]_
- Gamerule config: _/battleroyale config gamerule [id]_
- Spawn config: _/battleroyale config spawn [id]_

## Apply selected Client config
_/battleroyale config client [render/display] [id]_

Apply specific configuration
- If there is no configuration with the corresponding id, it will not be applied

- Render config: _/battleroyale config render [id]_
- Display config: _/battleroyale config display [id]_

> Zone rendering will take effect the next time a zone message is received from the server, usually with a delay of several seconds

## Apply selected Server config
_/battleroyale config server [performance/utility] [id]_

Apply specific configuration
- If there is no configuration with the corresponding id, it will not be applied

- Performance config：_/battleroyale config performance [id]_
- Utility config: _/battleroyale config utility [id]_