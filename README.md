# 自定义大逃杀 | Custom BattleRoyale

[中文](#自定义大逃杀) | [English](#custom-battleroyale)

[![1:1艾伦格](pic/Erangle%20Pochinki%2016：9.png)](https://space.bilibili.com/300364311)

# 自定义大逃杀

😎[wiki](https://github.com/XColorful/BattleRoyale/wiki) | 📄[docs](https://github.com/XColorful/BattleRoyale/tree/HEAD/docs)

本模组旨在打造拥有**高度自定义且极致兼容**的**区域、物资刷新和游戏规则**，与多种玩法混合打造数量丰富的游戏模式，包括但不限于大逃杀玩法。同时本模组也可用作 PUBGMC mod 的高版本增强版。

---

`该模组需要安装在服务端和客户端`

## 主要特色

### 高度定制及兼容性

- 所有配置文件均支持**任意数量的预设及热加载**，可在游戏内通过指令一键切换
- 游戏机制**独立于原版**队伍，与原版及其他模组的兼容性好
- 可与生存维度共存：游戏区域对生存玩家不可见，游戏玩家不受生存玩家干扰
- 可构建复杂的游戏区域，自由组合区域形状、位置及功能
- 可构建高度定制的物资刷新，刷新原版及其他模组的任意物品及实体

### 自定义区域

- 二维形状：圆形、方形、矩形、平顶正六边形、尖顶正多边形、星形、椭圆
- 三维形状：球、正方体、长方体、椭圆
- 区域功能：安全区、[刷新玩家背包](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#背包区)、无敌区、状态效果、生成粒子、烟花

### 定制物资刷新

- 支持**原版箱子刷新**，可用黑白名单过滤特定容器如熔炉
- 可嵌套的控制刷新类型、重复刷新、条件刷新
- 根据群系、建筑、NBT正则表达式精准刷新
- 自动清理旧游戏中刷新的物品及实体
- 支持读取容器内容并[自动生成](https://github.com/XColorful/BattleRoyale/wiki/Utility-command#生成物资刷新配置文件)物资刷新配置

### API与数据包扩展

重新定义数据包架构设计：
- **事件驱动型数据包**：支持[配置文件](https://github.com/XColorful/BattleRoyale/wiki/Function-config#事件注册)自动注册监听、[函数注册API](https://github.com/XColorful/BattleRoyale/wiki/Function-API-command#函数注册API)**动态注册监听**[模组事件](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventType.md)、[自定义事件](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/CustomEventType.md)
- 提供丰富的 [API指令](https://github.com/XColorful/BattleRoyale/wiki/API-command)，高效获取游戏信息，避免手动维护复杂状态
- 扩展[实体选择器](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#启用实体选择器)，一键筛选游戏玩家，无需手动维护标签

### 模组联动

- JourneyMap：在地图上绘制游戏区域
- PlayerRevive：对未被淘汰的游戏玩家设置倒地次数，添加倒地持续扣血
- 可开启PUBGMC指令兼容，使用PUBGMC指令的命令方块无需修改
- TaCZ：提供开箱即用的物资刷新配置示例，可在PlayerRevive倒地状态下禁用枪械

# Custom BattleRoyale

😎[wiki](https://github.com/XColorful/BattleRoyale/wiki#English) | 📄[docs](https://github.com/XColorful/BattleRoyale/tree/HEAD/docs)

This mod is designed to create game modes with **highly customizable and extremely compatible** **zones, loot generation, and game rules**, blending with various gameplay styles to offer a rich selection of game modes, including but not limited to Battle Royale. This mod can also serve as an enhanced, higher-version alternative to the PUBGMC mod.

---

`This mod needs to be installed on both the server and the client.`

## Main Features

### High Customizability & Compatibility

- All configuration files support **unlimited presets and hot-reloading**, allowing for a one-click switch via in-game commands.
- The game mechanics are **independent of vanilla** teams, ensuring excellent compatibility with both vanilla and other mods.
- Coexists with Survival Mode: Game zones are invisible to survival players, and game players are not affected by survival players.
- Create complex game zones by freely combining zone shapes, positions, and functions.
- Build highly customized loot generation, including any items and entities from both vanilla and other mods.

### Custom Zones

- 2D Shapes: Circle, Square, Rectangle, Flat-Top Hexagon, Pointy-Top Polygon, Star, Ellipse
- 3D Shapes: Sphere, Cube, Cuboid, Ellipsoid
- Zone Functions: Safe zone, [refresh player's inventory](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Inventory-zone), invulnerability zone, status effects particle generation, fireworks.

### Custom Loot Generation

- Supports **vanilla chest looting** and can filter specific containers like furnaces using whitelists and blacklists.
- Nested generation types, repeat generation, and conditional generation.
- Generating precisely based on biomes, structures, and NBT regular expressions.
- Automatically clear items and entities generated in previous games.
- Supports reading container contents to [automatically generate](https://github.com/XColorful/BattleRoyale/wiki/Utility-command#Generate-loot-spawner-configuration) loot configuration files.

### API and Datapack addon

Redefining datapack architecture design:
- **Event-Driven Datapacks**: Supports automatic listener registration via [configuration files](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Event-register) and **dynamic registration** of [Mod events](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventType.md) or [Custom events](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/CustomEventType.md) via the [Function Register API](https://github.com/XColorful/BattleRoyale/wiki/Function-API-command#Function-register-API).
- Provides a rich set of [API Commands](https://github.com/XColorful/BattleRoyale/wiki/API-command#English) for efficient retrieval of game information, eliminating the need for manual state maintenance.
- Enhanced [entity selectors](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#Enable-entity-selector): Filter game players instantly without the need for manual tag management.

### Mod Integrations

- JourneyMap: Draws game zones on the map.
- PlayerRevive: Sets the number of downs and adds consistent health loss for non-eliminated game players.
- Optional PUBGMC Command Compatibility: Allows command blocks using PUBGMC commands to work without modification.
- TaCZ: Provides out-of-the-box loot configuration example, and can disables guns in the PlayerRevive downed state
