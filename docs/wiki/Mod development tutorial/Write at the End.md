# 写在最后

本教程尝试将我制作本模组的实践经验结合`马克思主义认识论`，致力于打造一个真正能**起到学习用途**且有**指导意义**的教程

因此，本模组及其文档、开发者文档、模组开发教程，不仅仅是一个模组，也作为现代在社区中如何学习并良性循环推动社区发展的一个参考思路，为技术教育提供了一种新的范式——**哲学思维指导下的实践型学习**，这种模式值得在各个技术领域推广：
- “先文档后实践”的开发方式使开发方向不迷茫，天然消除了因懒惰因素导致的文档缺失
- [PR历史](https://github.com/XColorful/BattleRoyale/pulls?&q=is%3Apr+is%3Aclosed)是教程成果的展示，其历史演变正是认识规律的体现
- 先学会的成为“老师傅”，提供实例参考的同时也提醒避免踩坑、将“各路师傅”们由于繁忙等原因而尚未传授的经验公有化，同时极力避免“留一手”的**防御性文档（技术封锁）开历史倒车**，让后人更好地学习，推动社区发展
- 如有其他见解或发现不足，可以提Issue或PR，欢迎共同改进

### 方法论在技术教育的适用性

#### 解决学习心理障碍

- “必须先学完所有知识 → 才能开始实践”：永远觉得准备不足，迟迟不开始
- “实践出真知 → 在实践中学习 → 通过矛盾推动进步”：立即开始，在行动中成长

#### 符合发展规律

`马克思主义认识论`实际上描述了**人类学习任何新技能的自然过程**：
- 从具体操作中获得感性认识
- 通过反思形成理性认识
- 用新认识指导更高级的实践
- 在矛盾中突破认知局限

#### 培养终身学习能力

- 技能型开发者：知识会过时
- 方法论型开发者：掌握适应变化的能力

#### 提供心理支持框架

当你遇到困难时，记住：
- **这是正常的**：所有开发者都经历过
- **这是进步的标志**：矛盾的出现说明你在成长  
- **这是学习的机会**：通过解决矛盾积累经验
- **这是质变的前奏**：量变积累终将引发飞跃

### 方法论具体实施建议

#### 在教程中明确体现哲学思维

- **实践**第一原则：编码优于空想，运行胜于完美；复杂概念需配有可运行的代码示例
- 矛盾驱动原则：遇到问题是进步的机会；教授如何把错误转化为经验
- 螺旋上升原则：学习是迭代过程，不是直线前进；鼓励重构和改进，不追求一次完美

#### 用方法论指导内容组织

第一循环：感性认识阶段
- 实践：创建第一个简单功能
- 认识：理解基本工作机制
- 矛盾：发现设计局限
- 提升：学习优化方法

第二循环：理性认识阶段  
- 实践：应用设计模式
- 认识：形成架构思维
- 矛盾：面对复杂需求
- 提升：掌握抽象层设计

第三循环：创造性实践阶段
- 实践：自主设计功能
- 认识：形成个人开发哲学
- 矛盾：解决独特问题
- 提升：贡献社区生态

### 历史必然性分析

> 如果你能看到最后，不妨看看以下关于模组开发技术演变的深度复盘，或许能为你解答“为什么必须这样做”的疑惑……

#### 单版本开发的本质缺陷：“手工作坊”式的局限性

- 现象：基于特定MC版本开发
- 本质：这是一种**强耦合**的开发模式，业务逻辑（大逃杀机制）与底层实现（Forge API）死死纠缠在一起。
- 历史局限：在 _Minecraft_ 早期（如1.7.10，1.12.2时代），版本更新慢，平台单一（主要是Forge），这种“手工作坊”模式效率最高
- 崩溃点：随着1.13+的扁平化更新，Mojang更新频率加快，以及Fabric/NeoForge的出现，环境变得极度不稳定，“手工作坊”为了适应新环境，必须把整个房子拆了重建（修改所有调用）
- 结果：如[PUBGMC](https://github.com/Toma1O6/PUBGMC)、[TaCZ](https://github.com/MCModderAnchor/TACZ)等模组的困境——因维护成本过高而被迫“断更”或“阉割功能”

#### 平台抽象层的必然性：工业化/工程化的觉醒

`平台抽象层`及类似实现方式代表了模组开发从 **“手工作坊”** 向 **“现代软件工程”** 的进化，这符合**辩证法中的“否定之否定”规律**：
- 肯定阶段（1.7.10-1.12.2）：直接调用API，简单快捷
- 否定阶段（1.13-1.19）：API频繁变动，多平台分裂，开发者痛苦不堪，大量模组停更，这是矛盾爆发的阶段
- 否定之否定阶段（1.20+，本教程）：引入**抽象层**，不再依赖特定 API，而是依赖“接口”，这是对早期简单模式的扬弃，达到了更高维度的简单

为什么说是必然的？
- 熵增定律：软件系统总是趋向于混乱（熵增），如果不引入抽象层来隔离变化，系统的混乱度（维护成本）最终会超过开发者的承受极限
- 分工的演化：抽象层使得“写游戏逻辑”和“适配底层API”变成了两个可以解耦的工作，正如现代工业流水线，`平台抽象层`是模组开发的“标准化零件”

#### 其他视角

##### 从技术经济学角度

维护成本 = f(版本差异, 平台分裂, 时间)
- 传统方式：成本呈指数增长
- 抽象层方式：成本呈线性增长
- 临界点：成本曲线交叉 → 必然选择抽象层

##### 技术债务的经济学模型

- 传统开发的技术债务累积：技术债务 = Σ(版本差异成本 × 平台适配成本 × 时间衰减因子)
- `平台抽象层`的债务控制：抽象层开发成本 + Σ(微小适配成本)
- 投资回报临界点：通常在支持**第3个版本时开始净收益**
> 即使本模组打算只支持1.20.1 + 1.21.1 + 最新（1.21.10），从1.20.1→1.21.1的移植中就包含了1.20.2，1.20.4的变化，尤其是在1.21.1→1.21.10（就本模组的移植而言应进一步分为1.21.1-1.21.4、1.21.6-1.21.10），所以仍然应该建立`平台抽象层`

##### 从社区生态学角度

- 环境压力：_Minecraft_ 快速迭代
- 选择压力：**用户期望**持续更新
- 进化响应：`平台抽象层`成为生存策略
- 适者生存：采用抽象层的模组持续繁荣
- 淘汰风险：坚持传统方式的模组**逐渐消失**
> 我曾想在1.21.11看到经典骑兵（矛）vs“现代骑兵”（[TaCZ](https://github.com/MCModderAnchor/TACZ)），但遗憾的是，在缺乏抽象层架构的情况下，跨越二十个版本的移植需要大量重构（近似于重写），这往往让即使最有热情的开发者也感到无能为力
> 
> 至少未来如果有同样**极其优秀的模组**的话，希望能在早期就能助力提高其跨版本的能力

##### 开源社区的发展阶段论

第一阶段：个人英雄主义
- 特征：个人开发者主导
- 模式：手工作坊式开发
- 局限：知识传承困难

第二阶段：社区协作
- 特征：多人协作开发
- 模式：经验共享但缺乏系统化
- 局限：“老师傅”经验流失

第三阶段：工业化体系
- 特征：方法论指导实践
- 模式：平台抽象层+文档驱动
- 优势：可持续的知识积累

## 结语：未来的展望

`平台抽象层`是否是 _Minecraft_ 模组开发的终极形态？

用`辩证唯物主义`的视角看，不是。
> **否定之否定是永恒的**，`平台抽象层`是对“手工作坊（直接调用 API）”的否定，但`平台抽象层`本身也有缺陷：它增加了架构的复杂度，增加了前期投入（虽然长期看是划算的）

### 分析框架的永恒价值

即使`平台抽象层`不是终极形态，本教程中仍然存在永恒价值，即便这可能不是我第一个发现的

#### 方法论的价值超越技术

传授的不是具体技术，而是思维方法：
- 从具体到抽象的认识能力
- 在矛盾中寻找解决方案的能力
- 预见技术发展趋势的能力

#### 哲学思维的普适性

`马克思主义`分析方法适用于：
- 任何快速迭代的技术领域
- 任何面临兼容性挑战的系统
- 任何需要长期维护的软件项目

#### 教育范式的创新

“哲学指导实践”教育模式：技术教育 + 哲学思维 + 心理建设
- 这种组合具有持久的生命力

### 我们的使命

1. 掌握当下最优解：深入理解和应用`平台抽象层`
2. 保持开放心态：准备迎接新的技术革命  
3. 传承方法论：将哲学思维**传递给更多开发者**
4. 推动社区进步：用**知识共享**对抗技术封锁
  
### 未来的召唤

如果说`平台抽象层`是 **“工业化（机械化）”** 阶段，那么下一个阶段可能是 **“智能化”** 或 **“标准化”** 阶段：
- 标准化（官方收编）：类似于 _Minecraft_ Bedrock版的Add-on API，如果Java版未来出现官方的、极其稳定的、向后兼容的API（虽然目前看遥遥无期），那么`平台抽象层`就会完成历史使命，被官方标准取代。
- 智能化（AI 填缝）：既然`forge-compat`和`neoforge-compat`只是在做翻译工作，未来也许根本不需要人写，AI 可以根据 `core` 的接口和目标版本的映射表，自动生成兼容层代码。那时的开发者，只需写`core`，兼容层由 AI 实时编译生成。

当AI编程、可视化开发、元宇宙模组等新技术出现时，我们今天建立的`平台抽象层`思维将转化为：
- 架构设计能力
- 系统思维能力  
- 适应变化能力
- 创新解决问题的能力

这些能力，才是真正**永恒的价值**

# English

This tutorial attempts to combine my practical experience in making this mod with `Marxist Epistemology`, dedicating itself to creating a tutorial that serves a **genuine learning purpose** and has **guiding significance**.

Therefore, this mod and its documentation, developer docs, and mod development tutorial are not just a mod, but also a reference approach for how to learn and promote community development in a virtuous cycle in the modern community, providing a new paradigm for technical education—**practice-oriented learning guided by philosophical thinking**. This model is worth promoting in various technical fields:
- The "Documentation First, Practice Later" approach ensures direction isn't lost and naturally eliminates documentation gaps caused by laziness.
- The [PR History](https://github.com/XColorful/BattleRoyale/pulls?&q=is%3Apr+is%3Aclosed) is a display of the tutorial's results; its historical evolution is the embodiment of the laws of cognition.
- Those who learn first become "veteran masters", providing practical references while warning against pitfalls. It publicizes the experience that "various masters" haven't passed on due to busyness, and vigorously avoids **"defensive documentation" (technical gatekeeping) that turns back the clock of history**, allowing successors to learn better and driving community progress.
- If you have other insights or find deficiencies, please submit an Issue or PR; joint improvement is welcome.

### Applicability of Methodology in Technical Education

#### Solving Psychological Barriers to Learning

- "Must learn all knowledge first → Then start practice": Always feeling underprepared, delaying the start.
- "Practice brings true knowledge → Learn in practice → Progress through contradictions": Start immediately, grow in action.

#### Conforming to Laws of Development

`Marxist Epistemology` actually describes the **natural process of human learning of any new skill**:
- Gaining perceptual knowledge from concrete operations.
- Forming rational knowledge through reflection.
- Guiding higher-level practice with new knowledge.
- Breaking through cognitive limitations amidst contradictions.

#### Cultivating Lifelong Learning Ability

- Skill-based developers: Knowledge becomes obsolete.
- Methodology-based developers: Master the ability to adapt to change.

#### Providing a Psychological Support Framework

When you encounter difficulties, remember:
- **This is normal**: All developers have been there.
- **This is a sign of progress**: The appearance of contradictions means you are growing.
- **This is an opportunity to learn**: Accumulate experience by resolving contradictions.
- **This is the prelude to qualitative change**: Quantitative accumulation will eventually trigger a leap.

### Specific Implementation Suggestions for Methodology

#### Explicitly Embody Philosophical Thinking in the Tutorial

- **Practice** First Principle: Coding is better than dreaming; running is better than perfection; complex concepts must be accompanied by runnable code examples.
- Contradiction-Driven Principle: Encountering problems is an opportunity for progress; teach how to turn errors into experience.
- Spiral Ascent Principle: Learning is an iterative process, not a straight line; encourage refactoring and improvement, do not pursue perfection at once.

#### Organize Content Guided by Methodology

First Cycle: Perceptual Knowledge Stage
- Practice: Create the first simple function.
- Cognition: Understand basic working mechanisms.
- Contradiction: Discover design limitations.
- Elevation: Learn optimization methods.

Second Cycle: Rational Knowledge Stage
- Practice: Apply design patterns.
- Cognition: Form architectural thinking.
- Contradiction: Face complex requirements.
- Elevation: Master abstraction layer design.

Third Cycle: Creative Practice Stage
- Practice: Independently design functions.
- Cognition: Form personal development philosophy.
- Contradiction: Solve unique problems.
- Elevation: Contribute to the community ecosystem.

### Historical Inevitability Analysis

> If you have read this far, you might want to look at the following in-depth review of the evolution of mod development technology, which may answer the doubt of "why it must be done this way"...

#### The Essential Defect of Single-Version Development: The Limitations of "Artisanal Workshops"

- Phenomenon: Development based on a specific MC version.
- Essence: This is a **tightly coupled** development mode where business logic (Battle Royale rules) is inextricably untwined with the underlying implementation (Forge API).
- Historical Limitation: In the early days of _Minecraft_ (e.g., 1.7.10, 1.12.2 eras), version updates were slow, and the platform was singular (mainly Forge), making this "artisanal workshop" mode the most efficient. 
- Collapse Point: With the flattening updates of 1.13+, the acceleration of Mojang's update frequency, and the emergence of Fabric/NeoForge, the environment became extremely unstable. To adapt to the new environment, the "workshop" had to tear down the whole house and rebuild (modify all calls).
- Result: The dilemma of mods like [PUBGMC](https://github.com/Toma1O6/PUBGMC) and [TaCZ](https://github.com/MCModderAnchor/TACZ) — forced to "stop updating" or "castrate features" due to excessive maintenance costs.

#### The Inevitability of the Platform Abstraction Layer: The Awakening of Industrialization/Engineering

The `Platform Abstraction Layer` and similar implementations represent the evolution of mod development from **"Artisanal Workshops"** to **"Modern Software Engineering"**, conforming to the **"Negation of the Negation" law in dialectics**:
- Affirmation Stage (1.7.10 - 1.12.2): Direct API calls, simple and fast.
- Negation Stage (1.13 - 1.19): Frequent API changes, platform fragmentation, developer misery, massive mod stagnation. This is the stage of contradiction explosion.
- Negation of Negation Stage (1.20+, This Tutorial): Introducing the **Abstraction Layer**. No longer relying on specific APIs, but on "Interfaces". This is the sublation of the early simple mode, achieving simplicity in a higher dimension.

Why is it inevitable?
- Law of Entropy Increase: Software systems always tend towards chaos (entropy increase). If an abstraction layer is not introduced to isolate changes, the system's chaos (maintenance cost) will eventually exceed the developer's tolerance limit.
- Evolution of Division of Labor: The abstraction layer decouples "writing game logic" and "adapting underlying APIs" into two separate tasks. Just like a modern industrial assembly line, the `Platform Abstraction Layer` is the "standardized part" of mod development.

#### Other Perspectives

##### From the Perspective of Technical Economics

Maintenance Cost = f(Version Difference, Platform Fragmentation, Time)
- Traditional Method: Cost grows exponentially.
- Abstraction Layer Method: Cost grows linearly.
- Critical Point: Cost curves cross → Inevitable choice of Abstraction Layer.

##### Economic Model of Technical Debt

- Accumulation of Technical Debt in Traditional Development: Technical Debt = Σ(Version Difference Cost × Platform Adaptation Cost × Time Decay Factor)
- Debt Control of `PAL`: Abstraction Layer Development Cost + Σ(Minor Adaptation Cost)
- ROI Critical Point: Usually starts to **yield net benefits when supporting the 3rd version**.
> Even if this mod intends to support only 1.20.1 + 1.21.1 + Latest (1.21.10), the port from 1.20.1 → 1.21.1 involves changes in 1.20.2 and 1.20.4. Especially for 1.21.1 → 1.21.10 (which for this mod should be further divided into 1.21.1-1.21.4, 1.21.6-1.21.10), a `Platform Abstraction Layer` should still be established.

##### From the Perspective of Community Ecology

- Environmental Pressure: Rapid iteration of _Minecraft_.
- Selection Pressure: **User expectation** for continuous updates.
- Evolutionary Response: `PAL` becomes a survival strategy.
- Survival of the Fittest: Mods adopting abstraction layers continue to prosper.
- Elimination Risk: Mods persisting with traditional methods **gradually disappear**.
> I once hoped to see the cross-era duel of Classic Cavalry (Spear) vs "Modern Cavalry" ([TaCZ](https://github.com/MCModderAnchor/TACZ)) in 1.21.11. Regrettably, in the absence of an abstraction layer architecture, porting across twenty versions requires massive refactoring (akin to a rewrite), which often leaves even the most enthusiastic developers feeling powerless.
> 
> At least if there are similarly **extremely excellent mods** in the future, I hope to help improve their cross-version capabilities in the early stages.

##### Developmental Stage Theory of Open Source Communities

Phase 1: Individual Heroism
- Feature: Dominated by individual developers.
- Mode: Artisanal workshop development.
- Limitation: Difficult inheritance of knowledge.

Phase 2: Community Collaboration
- Feature: Multi-person collaborative development.
- Mode: Experience sharing but lack of systemization.
- Limitation: Loss of "veteran master" experience.

Phase 3: Industrialized System
- Feature: Methodology guiding practice.
- Mode: Platform Abstraction Layer + Documentation Drive.
- Advantage: Sustainable knowledge accumulation.

## Epilogue: Outlook for the Future

Is the `Platform Abstraction Layer` the ultimate form of _Minecraft_ mod development?

From the perspective of `Dialectical Materialism`, no.
> **The negation of the negation is eternal.** The `Platform Abstraction Layer` is the negation of the "Artisanal Workshop (Direct API Call)", but the `PAL` itself has defects: it increases architectural complexity and initial investment (though cost-effective in the long run).

### The Eternal Value of the Analytical Framework

Even if the `PAL` is not the ultimate form, there is eternal value in this tutorial, even if I may not be the first to discover it.

#### Methodology Transcendence over Technology

What is taught is not specific technology, but thinking methods:
- The ability to recognize from concrete to abstract.
- The ability to find solutions within contradictions.
- The ability to foresee technological development trends.

#### Universality of Philosophical Thinking

`Marxist` analytical methods apply to:
- Any rapidly iterating technical field.
- Any system facing compatibility challenges.
- Any software project requiring long-term maintenance.

#### Innovation in Educational Paradigm

"Philosophy Guiding Practice" Education Model: Technical Education + Philosophical Thinking + Psychological Construction.
- This combination has enduring vitality.

### Our Mission

1. Master the current optimal solution: Deeply understand and apply the `Platform Abstraction Layer`.
2. Maintain an open mind: Be ready to welcome new technological revolutions.
3. Pass on the methodology: Pass philosophical thinking **to more developers**.
4. Promote community progress: Use **knowledge sharing** to counter technical gatekeeping.

### The Call of the Future

If the `Platform Abstraction Layer` is the **"Industrialization (Mechanization)"** stage, then the next stage might be the **"Intelligent"** or **"Standardization"** stage:
- Standardization (Official Adoption): Similar to the Add-on API of _Minecraft_ Bedrock Edition. If the Java Edition introduces an official, extremely stable, backward-compatible API in the future (though it seems distant), the `PAL` will complete its historical mission and be replaced by official standards.
- Intelligent (AI Filling): Since `forge-compat` and `neoforge-compat` are just doing translation work, perhaps humans won't need to write them in the future. AI could automatically generate compatibility layer code based on the `core` interface and the mapping table of the target version. Developers at that time would only need to write the `core`, and the compatibility layer would be compiled and generated by AI in real-time.

When new technologies like AI programming, visual development, and metaverse mods emerge, the `Platform Abstraction Layer` thinking we establish today will transform into:
- Architectural design ability.
- Systems thinking ability.
- Ability to adapt to change.
- Ability to solve problems innovatively.

These abilities are the truly **eternal values**.