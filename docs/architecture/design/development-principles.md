[English](#English)

# 开发准则

> 本文档列出了开发过程中必须遵守的核心规则

## 架构

- 扁平化管理：`GameManager`和`ModConfigManager`作为顶级门面，负责整合子模块
- 禁止越级调用：外部通过顶级易用门面获取子管理器（如`BattleRoyale.getGameManager().getSubManager()`），禁止通过具体类直接访问子管理器及内部实现
> 使用`TeamManager teamManager = TeamManager.get();`则丢失热插拔适配性（视为专用）
- 接口优先：所有跨模块交互必须通过 API 接口进行，禁止直接引用具体实现类（`GamePlayer`、`ZoneTickContext`等指定数据类除外）
- 同构分形：子管理器应模仿顶级门面的模式，通过接口暴露功能，将具体实现委托给内部辅助类，从而简化系统结构
- 易用门面：对外提供一站式服务，隐藏内部复杂性，降低外部认知负担

## 兼容性与隔离

- 存档隔离：目前模组没有额外数据写入 _Minecraft_ 原版存档文件（`Level.dat`）
- 平台抽象：核心逻辑必须与 Forge/NeoForge API 解耦；长期冲突的变化应封装在`forge-compat`/`neoforge-compat`层，断层的变化可执行一次性处理
- 非侵入式：模组能够不干扰原版游戏机制（如区域渲染对生存玩家不可见、避免非游戏玩家互相伤害）
- 低耦合：核心机制（如游戏队伍、物资刷新）应独立于原版或其他模组的特定实现
- 无感执行：耗时操作（如物资刷新计算）应采用异步/分时处理，避免阻塞主线程，导致干扰游戏玩家及生存玩家

## 配置与数据

- 配置解耦：增量更新的配置文件仍使用纯 JSON 格式，不依赖平台特定的配置系统
- 格式通用：NBT 数据以字符串形式存储，从而读写方式上实现跨版本兼容
- 热重载支持：增量更新的新配置，效仿已有代码实现，则应支持已有的游戏内通过指令重载/切换/用文件名直接选中
- 多配置共存：_./minecraft/config_ 下的配置以文件夹为单位，支持多套配置切换
- 唯一性数据：_./minecraft/battleroyale_ 目录仅用于存储运行时生成的唯一数据（如统计数据、备份），模组内没有支持配置切换的计划

## 扩展性与热插拔

- 热插拔：允许扩展模组通过`setManager`方法完全替换核心子管理器，自行注意状态转移
- 熵对等原则：模组设计者承担架构复杂性，为玩家/服主提供便利（熵上移），扩展开发者进一步开发构建上层应用后承担自己特定场景的维护责任（熵不下移）
- 增量更新：对非核心模组（无论是否只支持单个版本）的联动都应采用增量逻辑，若移除联动模块也不影响核心功能，则达到无害扩展效果

# English

> This document lists the core rules that must be followed during the development process.

## Architecture

- Flattened Management: `GameManager` and `ModConfigManager` serve as top-level facades to integrate sub-modules.
- No Direct Access: External calls must go through the top-level facades (e.g., `BattleRoyale.getGameManager().getSubManager()`). Direct access to implementation classes and internal logic is prohibited.
> Using `TeamManager teamManager = TeamManager.get();` loses hot-swapping adaptability (seen as dedicated).
- Interface-First: All cross-module interactions must use API interfaces. Direct reference to concrete implementation classes is prohibited (except for specific data classes like `GamePlayer` and `ZoneTickContext`).
- Isomorphic Fractals: Sub-managers should mimic the top-level facade pattern, exposing features through interfaces and delegating implementations to internal helper classes, thereby simplifying the system structure.
- Easy-to-Use Facades: Provide one-stop service to hide internal complexity and reduce external cognitive load.

## Compatibility & Isolation

- Archive Isolation: No additional data is currently written to the vanilla _Minecraft_ archive file (`Level.dat`).
- Platform Abstraction: Core logic must be decoupled from Forge/NeoForge APIs; long-term conflicting changes should be encapsulated in `forge-compat`/`neoforge-compat` layers.
- Non-Intrusive: The mod should not interfere with vanilla game mechanics (e.g., zone rendering invisible to survival players, avoiding mutual damage between non-game players.).
- Low Coupling: Core mechanisms like teams and loot must be independent of vanilla or other mod implementations.
- Unobtrusive Execution: Resource-intensive tasks (e.g., loot refresh calculation) should use asynchronous or time-sharing processing to avoid blocking the main thread.

## Configuration & Data

- Config Decoupling: Incrementally updated configuration files still use pure JSON format and do not depend on platform-specific config systems.
- Universal Formats: Store NBT data as strings to achieve cross-version compatibility in reading and writing.
- Hot-Reload Support: By following existing implementations, incrementally updated configurations should support in-game reloading, switching, or direct selection via filenames through commands.
- Multi-Config Coexistence: Configurations under `./minecraft/config` are folder-based, supporting multiple sets of config switching.
- Unique Data: The `./minecraft/battleroyale` directory is used only for runtime-generated unique data (e.g., stats, backups), there is no plan to support config switching within this directory.

## Extensibility & Hot-swapping

- Hot-swapping: Allow extension mods to completely replace core sub-managers via `setManager` methods while handling state transfer.
- Entropy Equality Principle: The mod designer assumes architectural complexity to provide convenience for players/owners (Entropy Upward), while extension developers assume responsibility for maintaining their specific scenarios (Entropy stays with the extension).
- Incremental Updates: Linkages with non-core mods should use incremental logic, ensuring that core functions remain unaffected even if the linkage module is removed, achieving a harmless expansion effect.