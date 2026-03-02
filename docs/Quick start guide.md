[English](#English)

# 快速入门

通过几个关键问题快速掌握自定义大逃杀的开发思想
> 同时也可引导 AI Agent 根据文档去浏览代码相关内容，快速理解项目以高效辅助开发

## 模组开发
> 可深度阅读：[设计哲学](./architecture/design/design-philosophy.md)和[开发准则](./architecture/design/development-principles.md)

### 重复劳动

为什么 _./compat/forge/event/events_ 下有大量同构的事件类（11个事件 × 5个优先级）？
- 生成效率：通过基础模板进行批量生成，其开发复杂度趋近于 $O(n)$
- 低心智负担：得益于清晰的同构逻辑，重复性工作当然由 AI 快速产出

在 AI 辅助开发的背景下，如有逻辑简单、结构清晰（但手动编写费力）的方案，不妨权衡其可读性与易维护性
> 注：现已重构成每个事件由单一 Manager 类内部持有5个优先级代理的结构，兼顾了文件整洁与 AI 批量生成的效率

### 照葫芦画瓢

模组坚持“扁平化增量”而尽力避免“层级结构”

得益于顶级门面（IGameManager、IModConfigManager）的扁平化设计，当模组需要添加同类的新功能/词条时：
- 解耦协作：各模块增量更新互不影响，天然支持团队/人机并行开发
- 无感复用：更新时仅需复用同级包下的已有实现，无需增加额外的架构记忆储备，自然实现统一风格
- 开放机制：模组内新功能添加到枚举，同时开放事件类词条供扩展模组监听（而不使用独享的注册机制）

因此，现有代码即为最直接的开发参考模板

## 模组设计
> 可快速浏览[架构总览](./architecture/Home.md)

### 架构线性化
> $O(1) \times O(n)$

本模组追求架构线性化：
- 对于 $n$ 次 $O(1)$ 脑力操作产生的 $O(n)$ 时间复杂度，只需高计算速度；若互相解耦，则还可并行化
- 低熵开发：通过 1 个基础模板复用 $n$ 次，将架构锁定为扁平结构；代码本身成为“自说明”的文档，添加新功能时直接参考现有实现
- 重构优势：由于 $n$ 个功能共享同一个 $O(1)$ 的结构，底层重构只需重新设计 $O(1)$ 量级的结构，其工程复杂度是线性的而不是指数级的
- 对抗热寂：相比熵值随层级深度呈指数增长的传统架构，本架构力求规避大型项目后期因“架构热死”而无法学透/修改、只能背负“屎山”负重的困局
> 注：AI 可作为“高级正则替换”来利用已有模板实现增量更新；对于同样的工程量，若能摊平成 $O(n)$，则是长期维护的最优解

### 同构分形
> 低结构复杂度

模组中的顶级门面（`IGameManager`、`IModConfigManager`）都遵循同构且分形的结构：
- 模组外部只需要知道`IGameManager`这一个获取游戏API的全能易用门面
- 在模组内部开发，`IGameManager`与其子管理器都遵循`IGameSubManager`，并且如`ITeamManager`也与其同样是“队伍API”的全能易用门面提供低熵而复杂分工封装到内部

### 独立的游戏机制
> 跨版本之魂

为什么用独立的`GamePlayer`、`GameTeam`而不用原版队伍？
- 低熵认知：这使得在游戏业务逻辑中，无需考虑玩家中途退出队伍/离开游戏而额外造成的数据维护/潜在问题，更无需考虑 _Minecraft_ 版本变化
- 非侵入式兼容：模组机制与原版功能不互斥，可与原版功能（如生存服）并行运行而不冲突
- 跨版本一致性：得益于与原版隔离，核心代码在 1.20.1-1.21.1 版本间保持高度通用与一致性，从而本质上只维护 1 份代码

### 排列组合的配置词条
> 组合以导向线性增量

为什么区域配置做成功能与形状分离的结构？配置文件太臃肿了？
- 如果只需“简单的缩圈”，（地图作者技术栈）用原版边界可平替模组区域
- 组合灵活性：将“功能”与“空间判定（形状）”解耦，可以排列组合创建多样化的游戏区域（如圆形安全区、星形无敌区）
- 熵上移开发：将复杂度（熵）上移至架构层，并提供开箱即用的配置示例，大幅降低服主的配置难度与学习成本

### 用实用代替传统
> 复用功能的可能性空间

为什么不在 JourneyMap 上添加一个指向圈中心的功能？以及区域外填充？
- 直击本质：解决“我在不在圈里”和“哪里是中心”的最快方式是直接在世界里看到
- 视觉导航：通过`区域特殊词条`扩展客户端区域渲染，添加从玩家脚底指向到圈中心的半透明长条指示，根据所处圈内外的位置更改颜色
- 玩家环顾四周即可快速、直观获取方向与圈内状态，且不依赖小地图

### 倒地流血机制
> 联动方式

模组怎么实现倒地次数以及倒地持续扣血？
- 当游戏玩家触发死亡事件，检查事件是否被（PlayerRevive）取消来判定倒地状态
- 状态追踪：利用 PlayerRevive 模组的 API 读取倒地状态，并在每 tick 更新状态及执行扣血逻辑
- 当前仅联动 PlayerRevive，提供玩家熟悉的倒地机制

同时也可以不安装 PlayerRevive（则倒地直接死亡），模组核心机制不与联动模组强耦合

### 物资刷新机制
> 均摊开销

为何物资刷新不依赖方块实体`BlockEntity） tick 方法？
- 全局调度：获取玩家位置并计算待刷新区块队列，每 tick 分批调用`LootGenerator`刷新（_Minecraft_ 维护了区块内方块实体`BlockEntity` Map，快速遍历）
- 宏观调控：分批处理机制允许根据服务端 TPS 人为控制刷新速率，避免“盲目生产”的性能瓶颈与资源浪费
- 通用性：不依赖具体方块实体`BlockEntity`而均可参与刷新（含黑白名单过滤），具有极高的通用性和兼容性
- 同时，仅处理玩家周围一定范围的区块，避免了高模拟距离下的算力浪费

### 出生点贴脸问题
> 具体需求

传统随机出生点易导致玩家扎堆，本模组用什么方案？
- 模组为此添加了三个[均匀分布算法](/docs/architecture/algorithm/distribution.md#均匀分布)：黄金螺旋分布、双圆心网格分布、网格采样
- 当采样数量略大于玩家人数时，即实现大致均匀的分布
- 如果不需要随机性，模组也支持固定的出生点位列表

# English

Quickly master the development philosophy of Custom BattleRoyale through several key questions.
> This can also guide AI Agents to browse code-related content based on the documentation, quickly understanding the project for efficient auxiliary development.

## Mod Development
> Further Reading: [Design Philosophy](./architecture/design/design-philosophy.md#English) and [Development Principles](./architecture/design/development-principles.md#English)

### Repetitive labor

Why are there a large number of isomorphic event classes (11 events × 5 priorities) under _./compat/forge/event/events_?
- Generation efficiency: By generating in batches through a base template, the development complexity approaches $O(n)$.
- Low cognitive burden: Thanks to the clear isomorphic logic, repetitive work is naturally produced quickly by AI.

In the context of AI-assisted development, if there is a solution that is logically simple and structurally clear (but tedious to write manually), it is worth weighing its readability and ease of maintenance.
> Note: Now refactored into a structure where each event is managed by a single Manager class holding five internal priority proxies, balancing file cleanliness with the efficiency of AI batch generation.

### Pattern replication

The mod adheres to "flat increments" and tries its best to avoid "hierarchical structures".

Thanks to the flat design of the top-level facades (`IGameManager`, `IModConfigManager`), when the mod needs to add new features/entries of the same type:
- Decoupled collaboration: Incremental updates of each module do not affect each other, naturally supporting parallel development between teams or humans and AI.
- Seamless reuse: When updating, you only need to reuse existing implementations under the same package, naturally achieving a unified style without adding extra architectural memory overhead.
- Open mechanism: New features within the mod are added to enums, while event-based entries are open for expansion mods to listen to (instead of using an exclusive registration mechanism).

Therefore, the existing code serves as the most direct reference template for development.

## Mod Design
> Quick overview: [Architecture Overview](./architecture/Home.md#English)

### Architectural linearization
> $O(1) \times O(n)$

The mod pursues architectural linearization:
- For $O(n)$ time complexity generated by $n$ times $O(1)$ cognitive operations, only high computational speed is required; if decoupled, parallelization is also possible.
- Low-entropy development: By reusing a single base template $n$ times, the architecture is locked into a flat structure; the code itself becomes "self-documenting," and adding new features only requires referring to existing implementations.
- Refactoring leverage: Since $n$ features share the same $O(1)$ structure, the underlying refactoring process only requires redesigning structures at the $O(1)$ scale; its engineering complexity is linear rather than exponential.
- Anti-Heat Death: Compared to traditional architectures where entropy increases exponentially with hierarchical depth, this architecture strives to avoid the "Architectural Heat Death" trap in late-stage large projects—where the system becomes too complex to learn or modify, leaving developers burdened with "legacy debt."
> Note: AI can act as "advanced regex replacement" to implement incremental updates using existing templates; for the same workload, flattening it into $O(n)$ is the optimal solution for long-term maintenance.

### Isomorphic fractal
> Low structural complexity

The top-level facades in the mod (`IGameManager`, `IModConfigManager`) all follow an isomorphic and fractal structure:
* External Perspective: Only needs to know `IGameManager`, an all-in-one easy-to-use facade for obtaining the Game API.
* Internal Perspective: Within the mod, `IGameManager` and its sub-managers all follow `IGameSubManager`. Furthermore, `ITeamManager` is also an all-in-one "Team API" facade, providing a low-entropy interface while encapsulating complex internal logic.

### Independent game mechanics
> Soul of Cross-Version Compatibility

Why use independent `GamePlayer` and `GameTeam` instead of vanilla teams?
- Low-entropy cognition: In game business logic, there is no need to consider the data maintenance/potential issues caused by players leaving teams/games midway, let alone _Minecraft_ version changes.
- Non-intrusive compatibility: Mod mechanics do not conflict with vanilla features and can run in parallel with vanilla functions (such as survival servers) without conflict.
- Cross-version consistency: Thanks to isolation from vanilla, the core code remains highly universal and consistent across versions 1.20.1-1.21.1, essentially maintaining only 1 set of code.

### Combinatorial config entries
> Composition leading to linear increments

Why is the zone configuration made into a structure where function and shape are separated? Isn't the configuration file too bloated?
- If you only need a "simple world border," (for the mapmaker's tech stack) vanilla borders can replace mod zones.
- Combinatorial flexibility: Decoupling "Function" from "Spatial Judgment (Shape)" allows for the creation of diverse game zones (e.g., circular safe zones, star-shaped invincibility zones).
- Entropy-upward development: Complexity (entropy) is moved up to the architectural layer, and out-of-the-box configuration examples are provided, greatly reducing the configuration difficulty for server owners.

### Pragmatism over tradition
> Possibility space for feature reuse

Why not add a feature on JourneyMap that points to the center of the circle? Or external filling?
- Direct essence: The fastest way to solve "Am I in the circle?" and "Where is the center?" is to see it directly in the world.
- Visual navigation: By extending client-side zone rendering through `Zone special entry`, a translucent rectangular indicator pointing from the player's feet to the center is added, changing color based on the player's position inside or outside the circle.
- Players can quickly and intuitively obtain direction and in-circle status just by looking around, without relying on a mini-map.

### Downed & bleed mechanism
> Integration strategy

How does the mod implement downed counts and continuous downed bleed damage?
- When a player triggers a death event, the downed state is determined by checking if the event was canceled (by PlayerRevive).
- Status Tracking: Use the PlayerRevive mod API to read the downed status, and update the status and execute bleed logic every `tick`.
- Currently only linked with PlayerRevive to provide a familiar downed mechanism for players.

At the same time, PlayerRevive does not have to be installed (in which case players die directly), as the core mechanics of the mod are not coupled with linked mods.

### Loot generation mechanism
> Amortized overhead

Why does loot generation not depend on the `BlockEntity` tick method?
- Global scheduling: Obtain player positions and calculate the queue of chunks to be refreshed, calling LootGenerator in batches every tick (_Minecraft_ maintains a Map of `BlockEntity` within chunks for rapid traversal).
- Macro regulation: The batch processing mechanism allows for manual control of the refresh rate based on server TPS, avoiding performance bottlenecks and resource waste from "blind production".
- Universality: Does not depend on specific `BlockEntity` and can participate in refreshing (includes whitelist/blacklist filtering), offering high universality and compatibility.
- Also, only chunks within a certain range around players are processed, avoiding computational waste under high simulation distances.

### Spawn crowding
> Specific requirements

Traditional random spawn points easily lead to player clustering. What solution does this mod use?
- The mod has added three [Uniform Distribution algorithms](/docs/architecture/algorithm/distribution.md#English) for this purpose: Golden Spiral, Dual-Center Grid, and Grid Sampling.
- When the sample size is slightly larger than the number of players, a roughly uniform distribution is achieved.
- If randomness is not required, the mod also supports fixed spawn point lists.