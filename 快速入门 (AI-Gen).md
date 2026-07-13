> XiaoColorful：以下内容100%由ClaudeCode+deepseek-v4-flash1M自动生成，由于模组本身写了较多docs（代码和中文文档几乎都是人工），因而Agent搜集内容还是比较容易的；作为快速入门的话，内容大差不差（反正详细内容还要去看docs，那里都是我人工写的文档）；提示词如下，为第一轮对话结果：
> ```
> 请你自行制定计划，调研（只读）这个模组，给出一个模组的介绍、如何浏览该模组的文档，项目架构等内容，写到根目录新建一个"调研.md"，不要执行git相关的commit操作
> ```

# 自定义大逃杀 (Custom BattleRoyale) — 模组调研报告

> 调研日期：2026-07-10 | 分支：core1.20.1 | 版本：0.5.7-dev2

---

## 一、简介

**自定义大逃杀 (Custom BattleRoyale)** 是一个 Minecraft Forge 模组（Mod），Mod ID 为 `battleroyale`，由开发者 **XiaoColorful** 创作。项目旨在打造一个**高度自定义且极致兼容**的大逃杀/竞技玩法系统，支持任意数量配置预设的热加载、复杂的区域系统、自定义物资刷新，以及与多种模组的联动兼容。

- **许可证**: GPL-3.0-or-later
- **游戏版本**: Minecraft 1.20.1  (通过多模块架构同时支持 Forge 和 NeoForge 平台)
- **Github**: [github.com/XColorful/BattleRoyale](https://github.com/XColorful/BattleRoyale)
- **Wiki**: [项目 Wiki](https://github.com/XColorful/BattleRoyale/wiki)

---

## 二、如何浏览该模组的文档

项目文档分为三个层次，适合不同角色的读者：

### 1. 架构文档（面向开发者 / 调试 Bug）

> 目录：`docs/architecture/`

- **[架构总览 `Home.md`](docs/architecture/Home.md)** — **推荐起点**。详细列出了所有模块的包路径、功能说明和文档链接，覆盖算法、API、客户端、指令、核心机制、配置等
- **[设计哲学](docs/architecture/design/design-philosophy.md)** — 阐述"线性熵增"等核心架构思想
- **[同构分形](docs/architecture/design/isomorphic-fractals.md)** — 理解模组"门面-子管理器"模式的演变和设计逻辑
- **[API 索引](docs/architecture/api/api-index.md)** — 面向扩展模组开发者，查询所有 API 接口

### 2. API 文档（面向扩展模组开发者）

> 目录：`docs/api/`

每个 API 接口的详细参数文档，按功能分类存放（algorithm / client / config / effect / event / game / loot / network / server 等）

### 3. Wiki 页面（面向玩家 / 服主）

> 目录：`docs/wiki/`

- 配置文件说明（区域、物资刷新、游戏规则、性能等）
- 指令使用指南（大逃杀指令、队伍管理、配置指令等）
- 游戏类型介绍（竞技、非对称、PVE、休闲等）
- 模组开发教程（跨版本开发、自定义事件处理器等）

### 4. 文档快速导航

- 想**了解模块划分** → 读 `docs/architecture/Home.md`
- 想**给本模组写扩展** → 读 `docs/architecture/api/api-index.md`
- 想**配置服务器** → 读 `docs/wiki/Home.md`
- 想**理解架构思想** → 读 `docs/architecture/design/design-philosophy.md`

---

## 三、项目架构

### 3.1 多模块构建结构

项目使用 Gradle 构建，采用"核心 + 平台适配"的分层架构：

```
BattleRoyale/
├── core/                    # ★ 核心模块 (~899 个 Java 文件)
│   └── src/main/java/xiao/battleroyale/
│       ├── BattleRoyale.java          # 模组主类（门面入口）
│       ├── algorithm/                 # 算法（BFS、分布计算）
│       ├── api/                       # ★ 所有公开 API 接口
│       ├── block/                     # 方块（物资刷新器、实体生成器、区域控制器）
│       ├── client/                    # 客户端（渲染、游戏数据、GUI）
│       ├── command/                   # 指令系统（~16 个指令）
│       ├── common/                    # 核心机制（游戏管理器、效果、物资、网络消息、服务端）
│       ├── compat/                    # 模组联动（JourneyMap、TaCZ、PlayerRevive、PUBGMC 等）
│       ├── config/                    # 配置框架（支持热加载的层级化配置系统）
│       ├── data/                      # 模组数据（IO、临时数据、开发者工具）
│       ├── developer/                 # 开发者工具（调试指令、GM 指令）
│       ├── event/                     # 事件系统（自定义事件、事件派发、事件注册）
│       ├── init/                      # 初始化（注册表、指令注册、通用设置）
│       ├── inventory/                 # 背包/GUI
│       ├── network/                   # 网络（消息定义、网络处理器）
│       ├── resource/                  # 资源加载
│       └── util/                      # 工具类（Chat、Json、NBT、Vec3 等）
├── forge-compat/            # Forge 平台适配层 (~65 个 Java 文件)
│   └── BattleRoyaleForge.java         # @Mod 入口，初始化核心
├── neoforge-compat/         # NeoForge 平台适配层（目录结构存在，具体实现待确认）
└── docs/                    # 文档目录（架构/API/Wiki 三层结构）
```

### 3.2 加载流程

1. **Forge 入口** (`forge-compat`)：`BattleRoyaleForge` 类带有 `@Mod("battleroyale")` 注解
2. 实例化各平台适配组件（注册表工厂、网络适配器、事件注册器等）
3. 调用 `BattleRoyale.init()` 初始化核心模块
4. 核心模块初始化顺序：
   - 事件系统 → 网络系统 → 配置管理器 → 游戏管理器 → 效果管理器 → 服务端管理器 → 物资管理器
5. 注册所有方块、物品、创造模式标签页等到 Forge 总线

### 3.3 游戏框架 — 主/子管理器模式（核心架构）

项目的核心设计模式是**主管理器（Facade）调度多个子管理器**：

```
                     ┌────────────────────────┐
                     │     IGameManager       │  ← 全能易用门面
                     │   (游戏主管理器)         │
                     └────────┬───────────────┘
                              │ 调度
         ┌────────────────────┼────────────────────┐
         │        │          │         │           │
    ┌────┴───┐ ┌──┴────┐ ┌──┴───┐ ┌───┴────┐ ┌───┴────┐
    │Gamerule│ │Lobby  │ │Loot  │ │Process │ │ Zone   │ ...
    │Manager │ │Manager│ │Manager│ │Manager │ │Manager │
    └────────┘ └───────┘ └───────┘ └────────┘ └────────┘
```

**游戏流程生命周期**：`initGameConfig` → `initGame` → `startGame` → `onGameTick`（循环）→ `stopGame`

这种设计使得扩展模组可以通过实现接口热插拔替换任意子管理器。

### 3.4 配置框架

同样采用"主/子管理器"结构，支持：
- **多预设热加载**：每个配置类型可以有多个预设，可在游戏内通过指令一键切换
- **JSON 驱动**：所有配置以 JSON 格式存储在 `config/battleroyale/` 目录下
- **配置类型**：游戏规则、出生、区域、统计数据、物资刷新、性能、函数、预设等

### 3.5 算法模块

- **分布算法**：黄金螺旋分布、双圆心网格分布、网格采样（用于物资刷新点生成）
- **BFS 预计算**：用于寻路/连通性分析
- 算法通过 `AlgorithmFacade` 门面统一对外提供

### 3.6 区域系统（特色功能）

支持复杂游戏区域的构建：
- **2D 形状**：圆形、方形、矩形、平顶正六边形、尖顶正多边形、星形、椭圆、十字形、环形
- **3D 形状**：球、正方体、长方体、椭球
- **区域功能**（Tick 时触发）：能量区、效果区、烟花区、背包区、消息区、无敌区、安全/不安全区、粒子区
- **特殊功能**：补给箱区、实体刷新区、通用事件区

### 3.7 事件系统

- **模组事件**：包装 Forge 原生事件（受击、伤害、死亡、登录/登出、渲染等）
- **自定义事件**：扩展模组可监听的事件体系
  - 游戏事件（玩家死亡、倒地、复活、观战、开始/结束等）
  - Tick 事件（物资刷新 BFS、区域更新、游戏 Tick 等）
  - 队伍事件（邀请、申请等）
- 支持事件优先级、取消、完成回调（FinishEvent）

### 3.8 模组联动（Compat）

| 模组 | 类型 | 说明 |
|------|------|------|
| **JourneyMap** | 客户端 | 在旅行地图上显示区域边界 |
| **TaCZ (Timeless & Classics Zero)** | 双端 | 倒地禁枪、枪械事件监听 |
| **PlayerRevive** | 双端 | 倒地/救援机制集成 |
| **PUBGMC** | 兼容层 | 老版本 PUBGMC 模组的兼容 |
| **CBR Addon** | 服务端 | 自定义大逃杀扩展模组 API |

### 3.9 跨平台设计

核心模块（`core`）完全**与平台无关**，通过接口抽象层解耦：

- `IRegistrarFactory` — 抽象对象注册
- `INetworkAdapter` / `INetworkHook` — 抽象网络通信
- `IEventRegister` — 抽象事件总线
- `IBlockModelRenderer` — 抽象方块渲染

Forge 平台通过 `forge-compat` 模块提供这些接口的具体实现，便于未来移植到其他 Minecraft 加载器（已预留 `neoforge-compat` 模块）。

---

## 四、总结

| 维度 | 详情 |
|------|------|
| **项目性质** | Minecraft Forge 模组，Java 17，1.20.1 |
| **代码规模** | ~900 Java 文件（core）+ ~65（forge-compat） |
| **架构风格** | 门面模式 + 子管理器 + 同构分形 + 事件驱动 |
| **扩展性** | API 接口完备，支持热插拔子管理器，支持添加自定义事件处理器 |
| **文档完整度** | 极高（架构/API/Wiki 三层、中英双语、含 Mermaid 图表） |
| **关键设计理念** | 线性熵增控制、扁平化架构、同构分形易用门面 |
