[English](#English)

# 设计哲学

> 本文档阐述了开发准则背后的核心思想与设计初衷

## 线性熵增
> 核心哲学

用一次函数 $y = ax + b$ 来表示项目随更新而变化的整体复杂度，则复杂度增速为常数 $a$：
- 追求步调一致：以统一性为主体，将主要熵增用“共享的同一结构”实现叠加态，避免熵增坍缩至各功能的独立特性中
	- 从而将各独特性的熵增最小可观测化
- 常数结构提取：针对 $O(n)$ 持续增长的复杂度，从中集中提取全通用的 $O(1)$ 效果的结构
	- 使新功能的添加过程仅为对该结构的线性复用
	- 即便常数结构本身极度复杂，复用时也不会因架构产生额外复杂度，工程量仅取决于功能自身的增量内容
- 抑制耦合爆炸：传统架构中每增加一层级，层级下的个体间最大存在 $2^{n-1}$ 的耦合关系，这是 NP 问题
> 例如本模组通过`IGameSubManager`穿透`IGameManager`的扁平化设计：
> - 既保留了各层级的独特性
> - 又规避了深层嵌套带来的熵增累加效应
- 抑制观测复杂度：从观测者效应的视角看，即便实际复杂度为 $\text{常数结构} \times \text{独特性}$：
	- 当开发者学习或效仿已有功能时，若复用结构成为其“主要印象”，则常数项即成为逻辑主体
	- 此时独特性带来的可感知复杂度被有效抑制（熵上移至开发者最初的设计支出），甚至在系统演化中趋向于“高阶无穷小”

### 存在方式

#### 当加入新功能时

- 实现“局部必要代码”是短期内最快速的路径（如直接 AI 生成记分板命令实现简易游戏机制）：
	- 对于长期维护而言，这属于高熵的技术债
	- 能够以最小熵增而长久保持系统活力的，必然是“死”的结构约束
- 实现已有结构的完整扁平融入：
	- 在视模组已有内容为 $O(n)$ 量级的视角下，唯一未扁平化处理的新功能是可忽略的 $O(1)$ 量级
	- 在该 $O(1)$ 的视角下，所有新增独特性都必须捆绑已有架构，这会拖慢开发速度并增加复杂度
	- 即在思考功能实现的同时，必须强制思考如何将其解耦融入
	> - 例如`IConfigSubManager`需要配套完成`BackupCommand`、`ConfigCommand`等诸多适配
	> - 微观视角下，$O(n)$ 的扁平融入会拖慢功能本身 $O(1)$ 的增量开发过程
- 并行开发策略：
	- 快速开发 Demo，在未完成扁平化适配前不合并至主分支
	- 扁平化后并入主分支，增量逻辑在合并时不占用相同的行修改，从而实现行隔离

#### 扁平化以无后效性
> “线性”的核心

在当前仅有一个新增功能时，该功能在微积分上（相对于已有规模）是可忽略的“新的一点”：
- 及时平摊以力求避免“多个新功能”累加造成的技术债

#### 存在后存在

- 隔离性：新功能开发初期必须遵循已有的相互隔离，从而实现可选择性添加或及时删除
- Demo 迭代：可先快速实现 Demo，此时独特性占主体并引入大量额外熵增
- 及时坍缩：在同一开发线程下，Demo 完成后必须及时清理以抑制独特性熵增（如将配置项统一至模组框架），消灭其特殊性以回归常数状态

## 同构分形与扁平化

### 易用门面

将`IGameManager`和`IModConfigManager`这两个顶级门面想象成两个巨大的圆盘盖子，而`ITeamManager`、`IGameProcessManager`等子管理器则是支撑盖子的柱子：
- 外部视角：只能俯视盖子（门面）和侧视柱子暴露在外的一侧（API），外部调用者不应关心柱子内部的钢筋水泥（具体实现），除非遇到 Bug 或文档缺失
- 内部视角：柱子内部可以极其复杂，甚至包含自己的辅助类和逻辑闭环，但这一切对盖子之上的观察者是透明的

如此一来，模组外部只需要知道从模组主类`BattleRoyale.getGameManager()`获取`IGameManager`，而不需要知道每个游戏机制到底从哪获取

#### 分形结构

这种“门面-实现”的结构是分形的：
- `IGameManager`是所有游戏子部分的门面
- `ITeamManager`是队伍系统的门面，尽管臃肿地继承了`ITeamExternal`、`ITeamManagement`等多个接口，但这是相较于`IGameManager`的进一步“一站式服务”
> 作为观察者视角，臃肿的`ITeamManager`实则只是一个中间人门面（接口承载器）：对外服务`IGameManager`，对内接收`ITeamManagement`的生产，其复杂度在于继承的接口而自身只是整合
- 实现细节：`TeamManager`的“增量信息量”多（扁平化摊开，易穿透），“层级结构信息量”少（低负担），将具体实现逻辑委托给同包下的`TeamExternal`、`TeamManagement`等受保护类
> 从重构视角看，这是将臃肿的具体实现“抽离”到了同包下的其他 protected 类，从而拯救了`TeamManager`这一“头文件”

#### 同构分形

假如扩展模组替换（热插拔）了一个很复杂的`ITeamManager`，在`ITeamManager`的视角：
- `ITeamManager`本身提供的接口作为终极任务（类似`IGameManager`），在满足这些接口的基础上，内部怎样复杂都与外部无关
- 这种设计遵循同构逻辑，避免了模块间耦合和穿透访问：外部只需获取“`IGameManager`聚合（易用接口）→`ITeamManager`实现”这两层深度后即可查询“队伍相关”的方法列表
> manager = ModId.get().get();
> manager.func();

在开发`ITeamManager`内部复杂分工时，也无需关心外部调用：
- 只需满足接口契约即可随意发挥，极大减轻了开发负担
- 同时，扁平化也更易于看透模组框架，顶级门面只是增量地、可选地去认识，而非复杂的层级结构

#### 熵上移

- 降低认知负担：
  - 开发者只需知道`BattleRoyale.getGameManager()`、`BattleRoyale.getModConfigManager()`就能快速锁定大范围
  - 通过`ITeamManager teamManager = IGameManager.getTeamManager()`直接锁定所有队伍功能（`ITeamManager`本身也是中间人全能易用门面）
  - 无需查阅分散的类，输入`teamManager.`后 IDE 即弹出所有可用项，接口名称列表即文档
- 解耦：只要接口签名不变，内部实现可以随意重构（甚至完全替换/热插拔），而不影响上层调用

## 独立自主

### 存档隔离

模组目前没有任何数据写入 _Minecraft_ 的存档文件：
- 避免污染：大逃杀游戏中断通常无需继续，模组应尽力避免崩溃，节约复杂度和资源
- 数据安全：即使需要断电恢复，数据也应保存在存档外（_./minecraft/battleroyale_ 下）
- 方便管理：数据在`Level.dat`外部，方便玩家/服主查看和清理，无需 NBTExplorer
- 解耦：不放入`Level.dat`，进一步与 _Minecraft_ 解耦

除非有极强理由，否则严禁写入`Level.dat`

### 平台抽象

截至本文档编写时模组已同步支持 1.20.1-1.21.11 中多个重要版本并易于移植，根本原因在于核心逻辑不依赖 Forge/NeoForge 特定类，也基本不依赖 _Minecraft_ 限制
- 策略：平台专属 API（如事件注册、网络发包）集中在`forge-compat`/`neoforge-compat`执行，`core`里用接口抽象
- 取舍：长期存在且频繁变更的 API 差异进行封装；一次性的版本分割（如 1.21 注册表变更）直接在分支处理，不强求向下兼容

### 配置解耦

目前模组配置文件全部为纯 Java 的 Json 读写：
- 理由：配置本就不适合做游戏内设置（如区域、物资），且纯 Json 读写仅依赖 Gson，实现了 1.20.1-1.21.11 的通用性
- 兼容性：不适用的 NBT 字符串也遵循 Gson 读写形式，一次 1.20.1 的配置更新即可全版本同步

## 兼容性与一致性

### 低耦合

`GamePlayer`和`GameTeam`独立于 _Minecraft_ 原版队伍：
- 不受限制：不受原版队伍机制限制（如中途强制退队的干扰）
- 功能丰富：模组端可实现比原版队伍更丰富的功能
- 隔离性：可在生存服开大逃杀，与生存玩家互不干扰（区域渲染不可见、伤害判定互不干扰）

物资刷新机制只依赖 _Minecraft_ 为每个区块维护的方块实体列表：
- 广泛兼容：不依赖特定方块实体功能，直接获取全集进行刷新，无论是否实现`Container`接口
- 独立运行：即使不添加模组方块，也能刷新原版箱子（甚至用熔炉作为物资刷新方块）
- 宏观调控：可精确控制刷新速率和区块范围，适应不同 TPS 环境
- 缺陷：暂未添加“非游戏自行刷新”配置，但模组定位为小游戏而非探险（如 Lootr），且无自然刷新设计
> 自然生成的战利品箱子（Lootr）本身已有一套完整的数据包/配置流程，无需重复造轮子

```java
package xiao.battleroyale.common.loot;

public class LootGenerator { 
    public static int refreshLootInChunk(LootContext lootContext) {
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (refreshLootObject(lootContext, blockEntity)) {
                refreshedCount++;
            }
        }
    }
}
```

#### 无感执行

物资刷新机制与 PUBGMC 的区别：

| |PUBGMC|自定义大逃杀|
|---|---|---|
|刷新方式|方块实体自更新，固定频率检查|手动计算待刷新区块，每`ServerTickEvent`分批刷新|
|性能|周期性波动|稳定限制每 tick 最大刷新数量|
|计算量|区块加载即检查，刷新全图|只刷新玩家周围区块，另开线程消除预计算开销|

- PUBGMC 缺乏宏观调控，导致周期性卡顿；`GameLootManager`通过预计算和队列维护避免了盲目性

区域功能更新有类似问题：
- 理想情况下 N 个区域的功能在 N tick 依次更新的单 tick 复杂度应为 $O(1)$
- 而在区域配置中将更新频率“取整”则在写配置时方便计算区域时间，但会导致集中 tick 区域功能造成卡顿
- 模组引入`tickOffset`支持随机偏移，期望上消除卡顿，而不影响服主队区域存在时间/移动时长的配置设计
- 让区域功能延迟随机间隔，是基于区域功能/区域形状/区域移动时长/区域存在时长互相解耦的基础上实现的

区域配置的实际影响在`IZoneManager`的接口`randomizeZoneTickOffset`（预处理）中体现：

```java
package xiao.battleroyale.common.game.zone;

public class ZoneManager extends AbstractGameManager implements IZoneManager {
    public void randomizeZoneTickOffset() {
        // ...
        Supplier<Float> random = gameManager.getRandom();
        for (IGameZone gameZone : this.zoneData.getGameZonesList()) {
            if (gameZone.getTickOffset() < 0) {
                gameZone.setTickOffset((int) (random.get() * gameZone.getTickFrequency()));
            }
        }
    }
}
```

### 兼容技术栈

独立于原版功能对玩家/服主技术栈提出了要求：
- 易用性：指令应符合直觉（如`/battleroyale team join 数字`），无需文档也能猜测
- 兼容性：应兼容原版地图作者技术栈（如指令配队）
- 默认配置：需考虑误操作的后果（如默认不过滤非原版容器）
> 若选择不过滤，则自然体现出“支持任意模组的容器方块”，但生存服中误操作可能会造成不可逆的损失

若有阻碍，模组将难以实现取代“指令控制原版矩形边界+原版队伍”方案的目标，尽管在其他方面取得了对原版机制的胜利

### 增量式兼容

模组应能独立运行，小地图（JourneyMap）和倒地机制（PlayerRevive）不与核心机制强绑定
- 策略：无论联动模组支持单版本还是长期支持，都放在`./compat`下隔离，属于“增量式”联动（扁平化）
- 示例：`DefaultLootConfigGenerator`中检查 TaCZ 加载状态生成配置，去掉该部分不影响核心逻辑

```java
package xiao.battleroyale.config.common.loot.defaultconfigs;

public class DefaultLootConfigGenerator {

  public static void generateDefaultLootSpawnerConfig(String configDirPath) {
    DefaultLootSpawner.generateDefaultConfigs(configDirPath);
    // 增量逻辑：去掉也不影响，且只有检测到模组才执行
    if (BattleRoyale.getMcRegistry().isModLoaded(Tacz.get().getModId())) {
        TaczLootSpawner.generateDefaultConfigs(configDirPath);
        // ...
    }
  }
    
}
```

## 热插拔与权责对等

### 热插拔
> 支持扬弃

`IGameManager`持有的多个`IGameSubManager`均可替换：
- 只需替换`IGameProcessManager`即可更改游戏类型（切换游戏模式）
- 深度定制可自行实现一个新类来替换，可以在继承原有`IGameSubManager`后重载部分方法（去其糟泊），并复用原有实现（取其精华）
- 甚至可直接替换`IGameManager`和`IModConfigManager`，实现极致定制

### 权责对等原则
> 熵对等原则

熵上移：模组设计者承担架构复杂性（熵增），提供全能易用门面`IGameManager`解决外部认知混乱
- 权：扩展模组通过`BattleRoyale.getGameManager()`获得几乎全部控制权，无需 mixin
- 责：应自行测试应用场景，若非模组 Bug，不应强制要求模组为特定场景特调

熵不下移：开发者扩展模组构建上层应用，不应将熵下移至基础（本模组）
- 权：能方便地直接替换`ITeamManager`甚至`IGameManager`
- 责：一旦替换，扩展模组作者需自行处理边缘情况和潜在 Bug，核心模组不应对此负责

# English

> This document explains the core ideas and original intentions behind the development principles.

## Linear Entropy Increase
> Core Philosophy

Represent the overall project complexity as a function of updates using the linear equation $y = ax + b$, where the growth rate of complexity is the constant $a$:
- Pursuing Synchronicity: Use uniformity as the main body; realize the superposition of major entropy increases through a "shared identical structure" to prevent entropy from collapsing into the independent characteristics of individual features.
    - Consequently, the entropy increase of each unique feature is rendered as "minimally observable."
- Constant Structure Extraction: Target the continuously growing $O(n)$ complexity and extract universal structures that achieve $O(1)$ effects.
    - Thus, the addition of new features is merely a linear reuse of these structures.
    - Even if the constant structure itself is extremely complex, its reuse adds no additional architectural complexity; the workload depends solely on the incremental content of the feature.
- Suppressing Coupling Explosions: In traditional architectures, each added hierarchy level can result in up to $2^{n-1}$ coupling relationships among individuals, which is an NP problem.
> For example, the flattened design of this mod allows `IGameSubManager` to penetrate `IGameManager`:
> - It preserves the uniqueness of each level.
> - It avoids the cumulative entropy effects brought by deep nesting.
- Suppressing Observational Complexity: From the perspective of the observer effect, even if the actual complexity is $\text{Constant Structure} \times \text{Uniqueness}$:
    - When developers learn from or mimic existing features, the constant structure becomes the logical subject if it forms their "primary impression."
    - At this point, the perceived complexity brought by uniqueness is effectively suppressed (entropy moves upward to the developer's initial design expenditure) and tends toward "higher-order infinitesimals" in system evolution.

### Mode of Existen

#### When adding new features

- Implementing "locally necessary code" is the fastest path in the short term (e.g., using AI to generate scoreboard commands for simple mechanics):
    - For long-term maintenance, this belongs to high-entropy technical debt.
    - What maintains system vitality over the long run with minimal entropy increase must be "dead" structural constraints.
- Implementing full flattened integration into existing structures:
    - From a perspective where existing content is of $O(n)$ magnitude, a single un-flattened new feature is a negligible $O(1)$.
    - From the $O(1)$ perspective, all added uniqueness must be bound to the existing architecture, which slows development and increases complexity.
    - In other words, while thinking about feature implementation, one must forcibly consider how to decouple and integrate it.
    > - For example, an `IConfigSubManager` requires accompanying adaptations such as `BackupCommand` and `ConfigCommand`.
    > - From a micro perspective, $O(n)$ flattened integration slows down the $O(1)$ incremental development of the feature itself.
- Parallel Development Strategy:
    - Develop Demos rapidly and do not merge into the main branch until flattened adaptation is complete.
    - After flattening, merge into the main branch; incremental logic should not occupy the same line modifications during merging, thereby achieving line isolation.

#### Flattening for memorylessness
> The core of "Linearity"

When only one new feature is added, it is a negligible "new point" in terms of calculus (relative to existing scale):
- Amortize immediately to strive to avoid the technical debt caused by the accumulation of "multiple new features."

#### Existen after Existen

- Isolation: Early development of new features must follow existing mutual isolation, thereby achieving selective addition or timely removal.
- Demo Iteration: Demos can be implemented quickly, where uniqueness dominates and introduces significant additional entropy.
- Timely Collapse: Within the same development thread, the Demo must be cleaned up immediately upon completion to suppress uniqueness-driven entropy (e.g., unifying config items into the mod framework), eliminating its particularity to return to a constant state.

## Isomorphic Fractals & Flattening

### Easy-to-Use Facades

Visualize `IGameManager` and `IModConfigManager` as two large circular lids, supported by sub-managers (columns) like `ITeamManager` and `IGameProcessManager`:
- External View: Only see the lids (Facades) and the exposed sides of columns (APIs). External callers should not care about the internal implementation unless encountering bugs or missing documentation.
- Internal View: Implementation can be extremely complex, containing its own helper classes and logic loops, but remains transparent to the observer above.

Thus, the external environment only needs to obtain `IGameManager` via `BattleRoyale.getGameManager()`, without knowing the specific origin of each game mechanism.

#### Fractal Structure

This "Facade-Implementation" structure is fractal:
- `IGameManager` is the facade for all game subcomponents.
- `ITeamManager` is the facade for the team system. Although it "bloatedly" inherits multiple interfaces like `ITeamExternal` and `ITeamManagement`, it provides "one-stop service" relative to `IGameManager`.
> From an observer's perspective, the bloated `ITeamManager` is actually a mediator facade (interface carrier): serving `IGameManager` externally while receiving output from `ITeamManagement` internally. Its complexity lies in inherited interfaces while it only performs integration.
- Implementation: `TeamManager` has high "incremental information" (flattened and easy to penetrate) and low "hierarchical information" (low burden), delegating specific logic to protected classes like `TeamExternal` and `TeamManagement` in the same package.
> From a refactoring perspective, this "extracts" bloated implementations to other protected classes in the same package, saving `TeamManager` from becoming a massive "header file".

#### Isomorphic Fractals

If an extension mod replaces (Hot-swapping) a complex `ITeamManager`, from the `ITeamManager` perspective:
- The interfaces provided by `ITeamManager` serve as the ultimate task (similar to `IGameManager`). Internal complexity is irrelevant to the external environment as long as these interfaces are satisfied.
- This design follows isomorphic logic to avoid module coupling and penetration: the external environment only needs to query the method list of "team-related" within the two-layer depth of `IGameManager` aggregation (easy-to-use interface) → `ITeamManager` implementation.

When developing the complex internal division of `ITeamManager`, there is no need to worry about external calls:
- Satisfying the interface contract allows for creative implementation, greatly reducing development burden.
- Meanwhile, flattening makes it easier to see through the mod framework; top-level facades are recognized incrementally and optionally rather than through complex hierarchical structures.
> manager = ModId.get().get();
> manager.func();

#### Entropy Upward

- Reducing Cognitive Load:
  - Developers can quickly narrow the scope by knowing `BattleRoyale.getGameManager()` and `BattleRoyale.getModConfigManager()`.
  - Directly access all team features via `ITeamManager teamManager = IGameManager.getTeamManager()` (`ITeamManager` itself is a middle-man almighty easy-to-use facade).
  - No need to consult scattered classes; the IDE pops up all available items upon typing `teamManager.`, making the interface name list the documentation itself.
- Decoupling: As long as the interface signature remains unchanged, the internal implementation can be refactored, replaced, or hot-swapped without affecting upper-level calls.

## Independence

### Archive Isolation

Currently, the mod writes no data to the _Minecraft_ `Level.dat`:
- Avoiding Contamination: BattleRoyale games usually do not need to continue if interrupted; the mod should minimize complexity and resource usage.
- Data Security: Even if power-off recovery is needed, data should be stored outside the archive (under _./minecraft/battleroyale_).
- Management: Data outside `Level.dat` is easier for players/owners to view and clean without NBTExplorer.
- Decoupling: Avoiding `Level.dat` further decouples the mod from _Minecraft_.

Unless there is an extremely strong reason, writing to `Level.dat` is strictly prohibited.

### Platform Abstraction

The mod supports multiple versions from 1.20.1 to 1.21.1 and is easy to port. This is because core logic does not depend on Forge/NeoForge specific classes or _Minecraft_ limitations:
- Strategy: Platform-specific APIs (e.g., event registration, packet sending) are centralized in `forge-compat`/`neoforge-compat`, while `core` uses interface abstractions.
- Trade-offs: Long-term and frequently changing API differences are encapsulated; one-time version splits (e.g., 1.21 registry changes) are handled directly in branches.

### Config Decoupling

All configuration files are pure Java Json Input/Output:
- Reason: Configurations are unsuitable for in-game settings (e.g., zones, loot), and pure Json I/O relying only on Gson achieves universality across 1.20.1-1.21.1.
- Compatibility: Inapplicable NBT strings also follow Gson I/O, allowing a single 1.20.1 config update to sync across all versions.

## Compatibility & Consistency

### Low Coupling

`GamePlayer` and `GameTeam` are independent of vanilla _Minecraft_ teams:
- No Restrictions: Not affected by vanilla team mechanisms (e.g., forced mid-game leaves).
- Rich Features: The mod can implement richer features than vanilla teams.
- Isolation: Can run battle royale on survival servers without interfering with survival players (invisible zone rendering, independent damage judgment).

The loot refresh mechanism only depends on the block entity list maintained by _Minecraft_ for each chunk:
- Broad Compatibility: Does not depend on specific block entity functions; refreshes all items regardless of whether they implement the `Container` interface.
- Independent Operation: Can refresh vanilla chests or even furnaces without adding mod blocks.
- Macro Control: Precise control over refresh rates and chunk ranges to adapt to different TPS environments.
- Limitations: No "non-game self-refreshing" config yet, as the mod is positioned for minigames rather than exploration (e.g., Lootr), and there is no natural refresh design.
> Naturally generated loot chests (Lootr) already have a complete datapack/config process, no need to reinvent the wheel.

```java
package xiao.battleroyale.common.loot;

public class LootGenerator { 
    public static int refreshLootInChunk(LootContext lootContext) {
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (refreshLootObject(lootContext, blockEntity)) {
                refreshedCount++;
            }
        }
    }
}
```

#### Unobtrusive Execution

Difference between loot refresh in PUBGMC and Custom BattleRoyale:

| |PUBGMC|Custom BattleRoyale|
|---|---|---|
|Refresh Mode|Block entity self-update, fixed frequency check|Manually calculate chunks, batch refresh per `ServerTickEvent`|
|Performance|Periodic fluctuations|Stable limit on max refresh per tick|
|Computation|Check upon chunk load, refresh whole map|Refresh only around players, use separate thread for pre-computation|

- `GameLootManager` avoids the periodic lag found in PUBGMC through pre-computation and queue maintenance.

Zone function updates have similar issues:
- Ideally, the single-tick complexity for N zones updating sequentially across N ticks should be $O(1)$.
- Rounding update frequencies in config is convenient for calculation but leads to concentrated tick lag.
- The mod introduces `tickOffset` to support random offsets, eliminating lag without affecting the configuration design of zone duration or movement.
- Randomizing zone function delays is achieved based on the decoupling of zone function/shape/movement duration/existence duration.

The actual effect of zone configuration is reflected in the `randomizeZoneTickOffset` (pre-processing) interface of `IZoneManager`:

```java
package xiao.battleroyale.common.game.zone;

public class ZoneManager extends AbstractGameManager implements IZoneManager {
    public void randomizeZoneTickOffset() {
        // ...
        Supplier<Float> random = gameManager.getRandom();
        for (IGameZone gameZone : this.zoneData.getGameZonesList()) {
            if (gameZone.getTickOffset() < 0) {
                gameZone.setTickOffset((int) (random.get() * gameZone.getTickFrequency()));
            }
        }
    }
}
```

### Compatible Tech Stack

Independence from vanilla features imposes requirements on player/owner tech stacks:
- Ease of Use: Commands should be intuitive (e.g., `/battleroyale team join [number]`), allowing guesses without documentation.
- Compatibility: Should be compatible with vanilla map creator tech stacks (e.g., command-based teaming).
- Default Config: Must consider consequences of misoperation (e.g., not filtering non-vanilla containers by default).
> If choosing not to filter, it naturally reflects "support for any mod's container block," but misoperation in survival servers may cause irreversible loss.

If any link encounters obstacles, it will be difficult for the mod to replace the "command-controlled vanilla border + vanilla team" solution, despite victories over vanilla mechanisms in other aspects.

### Incremental Compatibility

The mod should operate independently; MiniMap (JourneyMap) and bleeding mechanism (PlayerRevive) are not hard-bound to core mechanisms:
- Strategy: Integration modules are isolated under `./compat` as "incremental" linkages (flattening), regardless of whether they support single or long-term versions.
- Example: `DefaultLootConfigGenerator` checks TaCZ loading status to generate configs without affecting core logic if removed.

```java
package xiao.battleroyale.config.common.loot.defaultconfigs;

public class DefaultLootConfigGenerator {

  public static void generateDefaultLootSpawnerConfig(String configDirPath) {
    DefaultLootSpawner.generateDefaultConfigs(configDirPath);
    // Incremental logic: removing it has no effect, and it only executes if the mod is detected
    if (BattleRoyale.getMcRegistry().isModLoaded(Tacz.get().getModId())) {
        TaczLootSpawner.generateDefaultConfigs(configDirPath);
        // ...
    }
  }
    
}
```

## Hot-swapping & Entropy Equality

### Hot-swapping
> Support for Aufheben (Sublation)

Multiple `IGameSubManager` instances held by `IGameManager` can be replaced:
- Change game types by replacing `IGameProcessManager` (switch game modes).
- Deep customization can be achieved by implementing a new class to replace a sub-manager, overriding specific methods (discarding the dross) while reusing existing logic (keeping the essence).
- Even `IGameManager` and `IModConfigManager` can be directly replaced for extreme customization.

### Entropy Equality Principle
> Entropy Equality

Entropy Upward: The mod designer assumes architectural complexity (Entropy Increase) and provides the all-in-one easy-to-use facade `IGameManager` to resolve external cognitive confusion:
- Power: Extension mods gain almost total control via `BattleRoyale.getGameManager()` without using Mixin.
- Responsibility: Developers must test their own scenarios; unless it is a mod bug, the mod should not be forced to adapt to specific scenarios.

Entropy stays with the extension: Extension developers building upper-level applications should not move entropy down to the base (this mod):
- Power: Can easily replace `ITeamManager` or even `IGameManager`.
- Responsibility: Once replaced, the extension author is responsible for handling edge cases and potential bugs; the core mod is not responsible for this.