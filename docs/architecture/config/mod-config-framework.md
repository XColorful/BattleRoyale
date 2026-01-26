[English](#English)

# 模组配置框架

> _这是我第一个自己做的我的世界模组（同时也是我第一个“像样的项目”），而我不知道怎么使用Forge提供的配置API😅，看其他模组（开源代码）的配置一下子也学不来，算了先把别的功能写了……_
>
> _在完成早期的[游戏框架](/docs/architecture/common/game/game-framework.md)后，模组配置管理器也使用类似的设计（不过还不支持子配置管理器热插拔），然后就自然地使用纯Java进行配置文件读写_
>
> _因此，没有游戏内GUI修改配置文件的方式，同时，本模组的配置文件实际只需要服主提前准备好预设即可，全部大逃杀配置均支持热加载，也有[设置大逃杀游戏步长](/docs/wiki/Command/Temp%20data%20command.md#设置大逃杀游戏步长)以快速测试复杂的[区域配置](/docs/wiki/Configuration/Zone%20config.md)config)并在[JourneyMap](https://teamjm.github.io/journeymap-docs/latest/)上绘制，何必再支持GUI修改……_
>
> _模组在多个版本更新（1.20.1forge，1.20.2f&n，1.20.4f&n，1.21.1f&n，1.21.4neoforge，1.21.6neoforge，1.21.10neoforge）几乎全部配置文件均通用（只依赖Java，从而通用语Forge/NeoForge/其他平台移植），仅有个别配置如NBT字符串需改为组件系统写法、生物实体NBT格式更改等MC因素而不通用_
>
> _本文档写于版本0.4.6-dev1_

模组配置框架设计为由`模组配置管理器`、`主配置管理器`、`子配置管理器`构成
- `模组配置管理器`：包含若干`主配置管理器`和`子配置管理器`
- `主配置管理器`：本身不包含配置文件读写，但包含若干`子配置管理器`，本质是进行分类
- `子配置管理器`：有且仅有**1种类型**的配置文件及其专属的文件夹，在文件夹下直接存放若干该类型配置文件，或者在文件夹下分不同文件夹但仍使用相同类型的配置文件（如[通用刷新配置](/docs/wiki/Configuration/General%20loot%20config.md)）
> 该结构实际体现为：重载所有游戏配置指令为 _/cbr reload game_（`模组配置管理器`→`主配置管理器`）；重载区域配置指令为 _/cbr reload game zone_（`模组配置管理器`→`主配置管理器`→`子配置管理器`）；重载物资刷新配置指令为 _/cbr reload loot_（`模组配置管理器`→`子配置管理器`）

### 配置管理器特殊机制

[![ISideOnly](/docs/api/common/ISideOnly.md)](/docs/api/common/ISideOnly.md)

- 在未读取到有效配置时（即使已有配置文件），生成默认配置（写入操作）并自动重新读取一次
- 读取配置后选取一个配置进行应用配置（执行一些额外操作），当`游戏管理器`正在进行游戏时应避免影响游戏
- 客户端配置管理器需要重载`ISideOnly`，避免在专用服务器上注册到`模组配置管理器`
- 各配置类型的Json读写均使用 _fromJson()_ 和 _toJson()_ 而未使用Gson装饰器，使得代码自文档并提供更精细的默认值替换和无效配置判定

[![IManagerName](/docs/api/config/IManagerName.md)](/docs/api/config/IManagerName.md)
- 将配置指令输入的字符串作为`getNameKey`，用于获取`主配置管理器`和`子配置管理器`
- 如需用自制配置管理器替换已有配置管理器，应具有相同的`getNameKey`

## 模组配置管理器
> 前往[模组配置管理器](./mod-config-manager.md)

## 主配置管理器
> 前往[主配置管理器](./config-manager.md)

## 子配置管理器
> 前往[子配置管理器](./config-sub-manager.md)

# English

> _This is my first self-made Minecraft mod (and my first "decent project"), and I didn't know how to use the configuration API provided by Forge 😅. Learning configuration from other mod's open-source code seemed too hard at the time, so I decided to implement other features first..._
>
> _After completing the early [Game Framework](/docs/architecture/common/game/game-framework.md#English), the Mod Configuration Manager used a similar design (though hot-swapping of Sub-Managers is not yet supported). Thus, reading and writing configuration files were naturally implemented using pure Java._
>
> _Consequently, there is no in-game GUI for modifying configuration files. Furthermore, the configuration files for this mod only require the server owner to prepare presets in advance, and all BattleRoyale configurations support hot-reloading. Since there is already a command to [Set the BattleRoyale game step](/docs/wiki/Command/Temp%20data%20command.md#Set-the-BattleRoyale-game-step) to quickly test complex [Zone config](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English) and draw them on [JourneyMap](https://teamjm.github.io/journeymap-docs/latest/), why bother supporting GUI modification..._
>
> _The configuration files across multiple mod updates (1.20.1forge, 1.20.2f&n, 1.20.4f&n, 1.21.1f&n, 1.21.4neoforge, 1.21.6neoforge, 1.21.10neoforge) are almost entirely compatible (only depending on Java, making them compatible with Forge/NeoForge/other platform ports). Only individual configurations, such as NBT strings needing to be changed to component system syntax or MC entity NBT format changes, are incompatible due to Minecraft factors._
>
> _This document is written as of version 0.4.6-dev1._

The Mod Configuration Framework is designed to consist of the `Mod Config Manager`, `Main Config Manager`, and `Sub Config Manager`:
- `Mod Config Manager`: Contains several `Main Config Manager` and `Sub Config Manager` instances.
- `Main Config Manager`: Does not handle file read/write operations itself but contains several `Sub Config Manager` instances. It primarily serves as a categorization mechanism.
- `Sub Config Manager`: Has one and only **one type** of configuration file and its exclusive folder. It directly stores several config files of that type within the folder, or organizes them into different sub-folders while still using the same config file type (e.g., [General loot config](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#English)).
> This structure is demonstrated in the command hierarchy: Reload all game configurations command is _/cbr reload game_ (`Mod Config Manager` -> `Main Config Manager`); Reload Zone configuration command is _/cbr reload game zone_ (`Mod Config Manager` -> `Main Config Manager` -> `Sub Config Manager`); Reload loot configuration command is _/cbr reload loot_ (`Mod Config Manager` -> `Sub Config Manager`).

### Configuration Manager Special Mechanism

[![ISideOnly](/docs/api/common/ISideOnly.md)](/docs/api/common/ISideOnly.md)

- If no valid configuration is read (even if config files exist), a default configuration is generated (write operation) and automatically re-read once.
- After reading the configuration, one configuration is selected and applied (performing some extra operations). This must avoid affecting an ongoing game if the `Game Manager` is active.
- Client config managers must override `ISideOnly` to prevent them from being registered with the `Mod Config Manager` on a dedicated server.
- JSON read/write for all configuration types uses _fromJson()_ and _toJson()_ methods instead of Gson decorators. This makes the code self-documenting and provides more granular control over default value substitution and invalid configuration detection.

[![IManagerName](/docs/api/config/IManagerName.md)](/docs/api/config/IManagerName.md)
- The string input in the configuration command is used as the `getNameKey` to retrieve the `Main Config Manager` and `Sub Config Manager`.
- If a custom config manager is intended to replace an existing one, it should have the same `getNameKey`.

## Mod Config Manager
> Go to [Mod Config Manager](./mod-config-manager.md#English)

## Main Config Manager
> Go to [Main Config Manager](./config-manager.md#English)

## Sub Config Manager
> Go to [Sub Config Manager](./config-sub-manager.md#English)