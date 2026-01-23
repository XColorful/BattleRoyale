# 跨版本模组开发

> **本模组及文档（Github Wiki）即可作为教程的实例示范**

不想看介绍？立即开始[部署环境](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment)😄

## 教程说明

这个教程旨在**简化模组开发入门**时不必要了解的内容，并让你写的模组能够**更好地移植**并**支持多版本/多平台**，而不是一个详细且完整的 _Minecraft_ 模组开发文档大全。
> 即使`平台抽象层`早已用在很多知名模组上，无奈“老师傅”们因种种原因没留下文档以传授该思想，因此在**模组开发教程**中并**不常见**
> - 对于模组开发入门：提供**多版本/多平台模组**开发的一个开发流程/框架，并**学习**结合本模组（作为**实例参考**）自学开发任何内容（物品/实体/机制/渲染……）的**方法论**
> - 对于有经验的开发者：可以考虑对已有模组项目封装全部/部分平台API（建立平台无关的接口）的方式，以减少移植版本/跨平台的阻力

### 开发方式对比

|开发方式|适用场景|前期投入|版本移植|后续更新|
|:--|:--|:--|:--|:--|
|基于`特定MC版本`开发|只专注于几个热门版本（如1.20.1/1.21.1）、项目已经成熟|只包含必要设计及实现，能**快速发布**第一个版本|代码里充满Forge/NeoForge类导入，移植版本时对任意兼容性变化都需要**修改所有调用**|对N个版本分别维护**N份代码**，需要**记住各版本API**使用方式|
|基于`平台抽象层`开发|现在或未来有移植计划（如1.20.1-最新）、尚未开发或希望如此|需理解`平台抽象层`，并额外封装Forge/NeoForge API（物品/实体注册、事件、网络通信等）|代码只包含平台无关的接口（interface）的导入，移植版本时提供了**做1处适配**即可**避免大幅修改核心代码**的可能性|**1份代码**直接应用于**多个版本**，几乎没有额外修改，只需记住封装的`平台无关接口`|

Forge/NeoForge提供的MDK、现有的模组开发教程大多都局限于**特定MC版本**，而本模组在 _0.4.0_ 创建了`平台抽象层`并**抽离**与Forge/NeoForge无关的**核心部分**，即可 **“一劳永逸”** 地享受上述`版本移植`及`后续更新`带来的**益处**。
> - 本教程致力于通过介绍自学模组开发的方法论+提供可参考的代码实例（本模组），以减少`前期投入`的学习压力和对实现方式的茫然，**凸显**基于`平台抽象层`开发的**益处**
> - 本模组就可作为一个`平台抽象层`的示范，并附有详细文档说明，作为新手入门时推荐效仿的一个参考

#### 开发策略举例

> 以下观点仅供参考，可用于确定自己模组开发的策略，而不是教条式地无脑建立`平台抽象层`：
- [自定义大逃杀](https://github.com/XColorful/BattleRoyale)（本模组）：支持多版本+多平台，移植前建立`平台抽象层`，后续更新**同时发布所有版本**（目前为10个）
- [JourneyMap](https://teamjm.github.io/journeymap-docs/latest/)（历史悠久）：支持多版本+多平台，移植时直接修改不兼容部分，由于新功能 **不向后更新（不更新Minecraft旧版本）** 而可能不必要建立`平台抽象层`
- [PUBGMC](https://github.com/Toma1O6/PUBGMC)：仅1.12.2（热门版本），作者**明确表示**无移植计划（1.12.2→1.20.1+移植成本较高，且时间久远已**没有兴趣**）
- [TaCZ](https://github.com/MCModderAnchor/TACZ)：仅1.20.1/1.21.1（热门版本），项目已成熟且移植成本较高而受限，需要等后续出现新的 _Minecraft_ 热门版本才**值得移植**

> 本模组为玩法机制类模组，其独特性质（核心小游戏逻辑几乎独立于 _Minecraft_ 和Forge/NeoForge）就决定了**大部分代码与平台无关**，天然适合同时支持多版本/多平台，并能快速跟上 _Minecraft_ 最新版本

### 平台抽象层

> 例如创建一个带有自定义功能的方块实体（_BlockEntity_）的过程为：
> 1. 调用Forge/NeoForge API注册到 _Minecraft_
> 2. （第一版模组）初始功能实现
> 3. 后续模组更新，对功能进行微调或重做等
> 
> 这个过程中只有 _第1步_ 是与平台相关且不超过几十行代码，其余为主要部分且在各版本功能相同，`平台抽象层`则用于在移植时**复用这类通用部分**以避免修改/尽可能保留大部分代码

将模组代码分为`core`、`forge-compat`、`neoforge-compat`三部分，分别对应项目根目录下三个文件夹：
- `core`：模组所有核心逻辑，只使用纯Java及 _Minecraft_ 原版API，不包含任何`net.minecraftforge`或`net.neoforged`等导入
- `forge-compat`：建立的`平台无关接口`的Forge实现，在Forge环境下编写
- `neoforge-compat`：建立的`平台无关接口`的NeoForge实现，在NeoForge环境下编写

与Forge/NeoForge MDK不同的是，本模组除了在项目根目录有一个 _build.gradle_，还有 _./core/build.gradle_，_./forge-compat/build.gradle_，_./neoforge-compat/build.gradle_
> - 这是标准的Gradle多项目，但实际上我也不知道gradle的用法，只需要参考别的开源模组/求助AI即可
> - 在已有**1个可用示范**（如本模组Github仓库）后，开发新模组时只需要**照搬前一个项目**里的这些文件并**修改相应参数**，而不需要**记住**或者**学会**

> 本教程旨在**简化模组开发入门**时不必要了解的内容，因此教程中会有多处“不必要了解”的提示

## 开发路线总览

**实践出真知，不要担心第一个模组不完美而迟迟不开始！**

先完成模组各个小部分积累经验，完成第一个可用版本，在后续需求与当前设计不完美的矛盾中，通过总览架构并借鉴他人前车之鉴，实现 _发现设计缺陷_ → _改进并积累经验_ → _发现设计缺陷_ → _更有经验地改进_ 的迭代循环，本模组的发展历程就是一个参考：

|阶段|描述|认识论意义|
|:--|:--|:--|
|起步阶段（_0.0.1_ - _0.0.4_）|[第一个PR](https://github.com/XColorful/BattleRoyale/pull/1)完成[物资刷新机制](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework#游戏物资刷新管理器)，随后实现大逃杀[配置读取](https://github.com/XColorful/BattleRoyale/wiki/Reload-command)/[指令](https://github.com/XColorful/BattleRoyale/wiki/Game-command#全部大逃杀游戏配置)/[队伍管理](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework#队伍管理器)原型，创造出一个功能简陋但可用的Alpha版|获得初步感性认识|
|完善阶段（_0.1.0_ - _0.1.5_）|基于初期经验，逐步补全游戏机制，如客户端渲染、[烟花区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#烟花区)、[状态效果](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#效果区)……|在实践中深化理解|
|优化/更新阶段（_0.2.0_ - _0.3.9_）|修复Bug、添加更多区域功能、优化物资刷新机制、重构[配置管理](https://github.com/XColorful/BattleRoyale/wiki/Mod-Config-Framework)、重构[游戏框架](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework)……|形成架构设计的理性认识|
|跨平台/多版本（_0.4.0_-最新）|在积累丰富经验后，系统性地引入`平台抽象层`，实现Forge/NeoForge多版本支持|用新认识指导平台抽象层实践|
> [PR历史](https://github.com/XColorful/BattleRoyale/pulls?&q=is%3Apr+is%3Aclosed)清晰地展示了这个演进过程：实践经验和技术质量通过**持续迭代**实现了**阶梯式提升**

作为我的第一个独立模组，从当前视角看早期架构确实很糟糕，但正是这些实践积累的经验，让我能够更有效地解决后续多版本移植的需求与现有设计局限之间的矛盾。

### ⭐学习阶段 (1️⃣/2)

> 对于有经验的开发者，可以跳过已了解的部分

|流程|操作概览|说明|
|:--|:--|:--|
|[部署环境](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment)|下载Java、Minecraft启动器、Forge、IntellijIDEA|对新手入门不必深入了解，专注于开发实践即可|
|[确定开发方向](https://github.com/XColorful/BattleRoyale/wiki/Determine-Direction)|确定模组开发内容（打算做新方块/物品/机制/其他？），编写模组文档，配置Gradle|避免盲目地开发，站在前人的肩膀上以简化起步流程|
|[完成最小可用模组](https://github.com/XColorful/BattleRoyale/wiki/Minimum-Viable-Mod)|完成模组入口|第一个可运行版本，即使没有实质功能，却已经成功一半|
|[完成一个功能](https://github.com/XColorful/BattleRoyale/wiki/Complete-a-Function)|创建一个方块/指令/物品/新机制|及时检验成果，并以此激励自己深入学习|
|[完成更多功能](https://github.com/XColorful/BattleRoyale/wiki/Complete-More-Functions)|完整剩余计划要完成的内容，涉及更多领域|丰富经验，提高开发熟练度|
|[整体架构分析](https://github.com/XColorful/BattleRoyale/wiki/Overall-Architecture-Analysis)|根据前几步积累的开发经验，形成一个整体系统认识|正式进入开发流程|

### 两种学习路线

> 即使建立`平台抽象层`，核心部分代码仍然要在某个 _Minecraft_ 版本上进行测试（例如本模组核心代码写在1.20.1forge），然后再应用到所有已支持版本（如1.20.1forge→……→1.21.1forge/neoforge→……→1.21.10neoforge）

之所以需要`平台抽象层`，是在了解到有些API（尤其是**注册相关API**）随版本经常变化，而提前为这些API创建`平台无关接口`，使得后续移植版本/新增功能需要调用该接口时，能够**只做1处兼容性适配**以避免大量修改核心代码

本教程不仅不与传统基于`特定MC版本`的开发教程冲突，反而**兼容所有该类教程**，并有两种开发方式：

|维度|后建立平台抽象层|先建立平台抽象层|
|:--|:--|:--|
|适合人群|已有项目/初学者|新项目/有经验者|
|学习曲线|渐进式|陡峭但高效|
|技术债务|需要一次性偿还|从一开始就可避免|
|心理压力|较小，可分步实施|较大，需要前瞻规划|
|长期收益|中等|极高|

#### 后建立平台抽象层

- 这种方式的实际学习顺序为：先学习`特定MC版本`的开发教程，后学习本教程（即完全兼容传统教程）
- 适用于已经完成项目/**熟悉Forge/NeoForge API调用后**，**在项目移植前**进行的准备工作
- 此时应有开发经验/重构经验来创建`平台无关接口`，以与核心代码解耦
- **缺点**：如果是第一个项目，则此时项目应已经成熟，创建`平台无关接口`需要立即承担`平台抽象层`的必要付出（一次性修改所有调用），期间不能进行其他更新以免冲突（潜在的阻力）

> 本模组正是在[0.4.0 Decouple mod from Forge API](https://github.com/XColorful/BattleRoyale/pull/42)后才能方便地移植多版本/多平台，而 _0.4.0_ 的更新仅仅是做了`平台抽象层`

#### 先建立平台抽象层

- 适用于开发第二个项目/已有可参考的`平台抽象层`实例，在项目开发初期便“走前人**已探明的路**”/自行实现“一劳永逸”
- **缺点**：对于第一个项目，需要额外学习`平台抽象层`；若没有已有的实例参考，则需要自行设计并创建`平台无关接口`

本模组可作为一个`平台抽象层`实例，但并未建立所有注册API的`平台无关接口`
- 本模组的扩展模组[自定义大逃杀扩展](https://github.com/XColorful/CBR-addon)，则充分利用了本模组已建立的`平台抽象层`并不需要再建立
> 不推荐将本模组作为你模组的前置模组（否则跟API模组无异/创造了新的耦合），如有必要请以[GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html)协议复制`平台抽象层`相关代码

### ⭐开发流程 (2️⃣/2)

||操作概览|说明|
|:--|:--|:--|
|修复Bug|查看日志⇋控制变量复现⇋ **排查问题（定位核心矛盾）** ⇋修复问题→完成修复|使代码更健壮，增强自身错误排查能力，同时避免再次踩坑|
|学习新领域|了解概念⇋ **寻找已有源码参考** ⇋形成自己的认识⇋实践检验|拓展技术栈，形成全面认识，利于开发更多联动及避免各领域间互相干扰|
|更新内容|确定需求⇋ **编写文档** ⇋代码实现⇋检验成果→完成更新|创造新内容，创新本质|
|重构优化|确定重构目标⇋确定重构范围⇋可行性分析⇋设计文档⇋重构代码⇋ **测试运行** →完成重构|可用于更方便地添加新功能/移植/优化性能/消除“屎山”/将新的架构思维进行实践检验|

让我们开始[部署环境](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment)吧😄

当你完成了第一个模组，不妨看看[写在最后](https://github.com/XColorful/BattleRoyale/wiki/Write-at-the-End)

# English

> **This Mod and its Documentation (GitHub Wiki) Serve as the Practical Instance for this Tutorial.**

Don't want to read the introduction? Start [Deploy Environment](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#English) now!😄

## Tutorial Overview

This tutorial aims to **streamline mod development entry** by excluding unnecessary details, enabling your mod to **port more effectively** and **support multiple versions/platforms**, rather than serving as a detailed and comprehensive _Minecraft_ mod development document.
> Although the `Platform Abstraction Layer (PAL)` is already employed by many well-known mods, the "veteran developers" have unfortunately not left sufficient documentation to pass on this methodology, making it **uncommon** in **mod development tutorials**.
> - For New Developers: Provides a development process/framework for **multi-version/multi-platform mod** creation, and teaches the **methodology** of self-learning any content (items/entities/mechanics/rendering...) using this mod as a **practical reference**.
> - For Experienced Developers: Consider encapsulating all or parts of the platform API (establishing `platform-independent interfaces`) in existing projects to reduce the friction of version porting and cross-platform shifts.
>     

### Development Strategy Comparison

|Development Method|Use Case|Initial Investment|Version Porting|Subsequent Updates|
|---|---|---|---|---|
|Based on `Specific MC Version`|Focuses only on a few popular versions (e.g., 1.20.1/1.21.1), project is already mature.|Only includes necessary design and implementation, allowing for **rapid release** of the first version.|Code is full of Forge/NeoForge class imports; porting requires **modifying all calls** for any compatibility change.|Maintaining **N copies of code** for N versions; requires **memorizing each version's API** usage.|
|Based on `Platform Abstraction Layer`|Has or plans for future porting (e.g., 1.20.1-latest), not yet developed or aiming for this.|Requires understanding the `PAL` and encapsulating Forge/NeoForge APIs (item/entity registration, events, networking).|Code only contains platform-independent interface imports; offers the possibility of making **one adaptation** to **avoid major core code changes**.|**1 copy of code** applies directly to **multiple versions** with minimal extra changes; only requires memorizing the encapsulated `platform-independent interfaces`.|

The MDKs provided by Forge/NeoForge and existing mod development tutorials are mostly confined to a **specific MC version**. However, this mod established a `Platform Abstraction Layer` in _0.4.0_ and **extracted** the **core logic** independent of Forge/NeoForge, allowing you to **"solve once and for all"** the **benefits** of `Version Porting` and `Subsequent Updates` mentioned above.
> - This tutorial aims to reduce the learning pressure of the `Initial Investment` and the uncertainty of implementation by introducing the self-learning methodology for mod development + providing reference code instances (this mod), thereby **highlighting the benefits** of `PAL`-based development.
> - This mod serves as a demonstration of the `Platform Abstraction Layer`, complete with detailed documentation, and is recommended as a reference for beginners to emulate.

#### Examples of Development Strategies

> The following points are for reference only, to help determine your mod's development strategy, not to blindly establish a `Platform Abstraction Layer`:
- [Custom BattleRoyale](https://github.com/XColorful/BattleRoyale) (This Mod): Supports multiple versions + multiple platforms; established `Platform Abstraction Layer` before porting; subsequent updates **release all versions simultaneously** (currently 10).
- [JourneyMap](https://teamjm.github.io/journeymap-docs/latest/): Supports multiple versions + multiple platforms; since new features are **not backported (do not update older Minecraft versions)**, they directly modify incompatible parts during porting, making a `Platform Abstraction Layer` potentially unnecessary for their specific workflow.
- [PUBGMC](https://github.com/Toma1O6/PUBGMC): Only 1.12.2 (popular version); the author **explicitly stated** no porting plans (the cost of porting 1.12.2 → 1.20.1+ is high, and they **lost interest** over time).
- [TaCZ](https://github.com/MCModderAnchor/TACZ): Only 1.20.1/1.21.1 (popular versions); the project is mature and porting costs are high and constrained; porting only becomes **worth the investment** when a new popular _Minecraft_ version emerges.

> The nature of this mod, which is a game mechanic type, dictates that its uniqueness (core minigame logic is largely independent of _Minecraft_ and Forge/NeoForge) makes it inherently suitable for supporting multiple versions/platforms simultaneously and quickly keeping up with the latest _Minecraft_ version.

### The Platform Abstraction Layer (PAL)

> For example, the process of creating a block with custom functionality Block Entity (_BlockEntity_) involves:
> 1. Calling the Forge/NeoForge API to register it with _Minecraft_.
> 2. (First Mod Version) Initial functionality implementation.
> 3. Subsequent mod updates, where functionality is tweaked or rewritten.
> 
> In this process, only _Step 1_ is platform-specific and amounts to less than a few dozen lines of code. The rest is the main bulk of the work and remains functionally identical across versions. The `Platform Abstraction Layer` is used to **reuse these common parts** during porting to avoid modification or to preserve most of the code.

The mod code is divided into three parts: `core`, `forge-compat`, and `neoforge-compat`, corresponding to three folders in the project root:
- `core`: All core mod logic, using only pure Java and _Minecraft_ vanilla API, **excluding** any `net.minecraftforge` or `net.neoforged` imports.
- `forge-compat`: The Forge implementation of the established `platform-independent interfaces`, coded for the Forge environment.
- `neoforge-compat`: The NeoForge implementation of the established `platform-independent interfaces`, coded for the NeoForge environment.

Unlike the Forge/NeoForge MDKs, this mod has a _build.gradle_ in the project root, as well as _./core/build.gradle_, _./forge-compat/build.gradle_, and _./neoforge-compat/build.gradle_:
> - This is a standard Gradle multi-project setup. You don't need to know the full usage of Gradle; simply reference other open-source mods/ask AI.
> - After having **one working example** (like this mod's GitHub repository), developing a new mod only requires **copying these files** from the previous project and **modifying the corresponding parameters**, without needing to **memorize** or **learn** the full system.

> This tutorial aims to **streamline mod development entry** by excluding unnecessary content, hence there will be multiple "unnecessary to understand" prompts throughout the guide.

## Development Route Overview

**Practice brings true knowledge; don't let the fear of imperfection stop you from starting!**

Accumulate experience by completing small parts of the mod first, finish the first usable version, and then, amidst the contradictions between subsequent requirements and current imperfect designs, achieve an iterative cycle of _Discovering Design Flaws_ → _Improving and Accumulating Experience_ → _Discovering Design Flaws_ → _Improving with More Experience_ by viewing the architecture as a whole and learning from others. The development history of this mod serves as a reference:

|Stage|Description|Epistemological Significance|
|---|---|---|
|Start-up Stage(_0.0.1_ - _0.0.4_)|[First PR](https://github.com/XColorful/BattleRoyale/pull/1) completed [Loot Generation Mechanism](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework#English), then implemented prototypes for [Config Reading](https://github.com/XColorful/BattleRoyale/wiki/Reload-command#English), [Commands](https://github.com/XColorful/BattleRoyale/wiki/Game-command#English), and [Team Management](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework#English), creating a functionally crude but usable Alpha version.|Gaining preliminary perceptual knowledge.|
|Perfecting Stage(_0.1.0_ - _0.1.5_)|Based on early experience, gradually fleshed out game mechanics, such as client rendering, [Firework zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Firework-zone), [Effect zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#Effect-zone), etc.|Deepening understanding through practice.|
|Optimization Stage(_0.2.0_ - _0.3.9_)|Fixed bugs, added more zone functions, optimized loot generation mechanisms, refactored [Config Management](https://github.com/XColorful/BattleRoyale/wiki/Mod-Config-Framework#English), refactored [Game Framework](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework#English), etc.|Forming rational knowledge of architectural design.|
|Cross-Platform Stage(_0.4.0_ - Latest)|After accumulating rich experience, systematically introduced the `Platform Abstraction Layer` to support multiple versions of Forge/NeoForge.|Guiding PAL practice with new knowledge.|

> The [PR History](https://github.com/XColorful/BattleRoyale/pulls?&q=is%3Apr+is%3Aclosed) clearly demonstrates this evolutionary process: practical experience and technical quality achieved a **step-wise elevation** through **continuous iteration**.

As my first independent mod, the early architecture looks terrible from the current perspective, but it was exactly the experience accumulated from these practices that allowed me to effectively solve the subsequent contradictions between multi-version porting requirements and existing design limitations.

### ⭐Learning Stages (1️⃣/2)

> Experienced developers can skip parts they already understand.

|Process|Operation Overview|Explanation|
|:--|:--|:--|
|[Deploy Environment](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#English)|Download Java, Minecraft Launcher, Forge, IntelliJ IDEA.|Beginners don't need deep understanding; focus on development practice.|
|[Determine Direction](https://github.com/XColorful/BattleRoyale/wiki/Determine-Direction#English)|Decide on mod content (New blocks/items/mechanics?), write mod documentation, configure Gradle.|Avoid blind development; stand on the shoulders of giants to simplify the start-up process.|
|[Minimum Viable Mod](https://github.com/XColorful/BattleRoyale/wiki/Minimum-Viable-Mod#English)|Complete the mod entry point.|The first runnable version; even without substantial functions, it's half the success.|
|[Complete a Function](https://github.com/XColorful/BattleRoyale/wiki/Complete-a-Function#English)|Create a block/command/item/new mechanism.|Verify results in time and use this to motivate deeper learning.|
|[Complete More Functions](https://github.com/XColorful/BattleRoyale/wiki/Complete-More-Functions#English)|Finish the remaining planned content, involving more fields.|Enrich experience and improve proficiency.|
|[Overall Architecture Analysis](https://github.com/XColorful/BattleRoyale/wiki/Overall-Architecture-Analysis#English)|Form a systematic understanding based on experience accumulated in previous steps.|Officially enter the development flow.|

### Two Learning Paths

> Even if a `Platform Abstraction Layer` is established, the core code still needs to be tested on a certain _Minecraft_ version (for example, the core code of this mod is written on 1.20.1 Forge) and then applied to all supported versions (e.g., 1.20.1 Forge → ... → 1.21.1 Forge/NeoForge → ... → 1.21.10 NeoForge).

The reason a `PAL` is needed is the realization that some APIs (especially **Registry APIs**) change frequently with versions. By creating `platform-independent interfaces` for these APIs in advance, subsequent porting or adding features only requires **one compatibility adaptation**, avoiding massive modification of the core code.

This tutorial not only does not conflict with traditional development tutorials based on a `Specific MC Version`, but implies **compatibility with all such tutorials**, offering two development approaches:

|Dimension|Establish PAL Later|Establish PAL First|
|:--|:--|:--|
|Target Audience|Existing Projects / Beginners|New Projects / Experienced Developers|
|Learning Curve|Progressive|Steep but Efficient|
|Technical Debt|Requires One-time Payoff|Avoidable from the Start|
|Psychological Pressure|Lower, step-by-step implementation|Higher, requires forward planning|
|Long-term Benefit|Medium|Very High|

#### Establish PAL Later (Post-Development)

- Actual Order: Learn from a `Specific MC Version` tutorial first, then learn from this tutorial (fully compatible with traditional tutorials).
- Applicable to: Preparation work **before project porting**, after the project is completed or after you are **familiar with Forge/NeoForge API calls**.
- Requirement: You should have development/refactoring experience to create `platform-independent interfaces` to decouple from the core code.
- **Disadvantage:** If this is your first project, it implies the project is already mature. Creating `platform-independent interfaces` requires bearing the necessary cost of the `PAL` immediately (modifying all calls at once), and other updates cannot be performed during this period to avoid conflicts (potential resistance).

> This mod was only able to easily port to multiple versions/platforms after [0.4.0 Decouple mod from Forge API](https://github.com/XColorful/BattleRoyale/pull/42), where the update of _0.4.0_ was merely the implementation of the `Platform Abstraction Layer`.

#### Establish PAL First (Pre-Development)

- Applicable to: Developing a second project or having an existing `PAL` reference instance available. "Walk the path **already explored** by predecessors" at the beginning of the project development / implement "solve once and for all" on your own.
- **Disadvantage:** For the first project, additional learning of the `PAL` concept is required; if there is no existing instance for reference, you need to design and create `platform-independent interfaces` yourself.

This mod can serve as a `PAL` instance, but it has not established `platform-independent interfaces` for all registry APIs.
- The extension mod of this mod, [Custom BattleRoyale Addon](https://github.com/XColorful/CBR-addon), fully utilizes the `PAL` established by this mod and does not need to rebuild it.
> Note: It is **not recommended** to use this mod as a dependency (pre-requisite) for your mod (otherwise it is no different from an API mod/creates new coupling). If necessary, please copy the relevant `Platform Abstraction Layer` code under the [GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html) license.

### ⭐Development Flow (2️⃣/2)

||Operation Overview|Explanation|
|---|---|---|
|Bug Fixes|View Logs ⇋ Control Variable Reproduction ⇋ **Troubleshoot (Locate Core Contradiction)** ⇋ Fix Issue → Complete|Makes code robust, enhances troubleshooting skills, and avoids repeating mistakes.|
|Learn New Field|Understand Concepts ⇋ **Find Existing Source Code References** ⇋ Form Own Understanding ⇋ Practical Verification|Expand tech stack, form comprehensive understanding, beneficial for developing linkages and avoiding interference between fields.|
|Update Content|Determine Requirements ⇋ **Write Documentation** ⇋ Implement Code ⇋ Verify Results → Complete|Create new content; the essence of innovation.|
|Refactor & Optimize|Set Target ⇋ Define Scope ⇋ Feasibility Analysis ⇋ Design Docs ⇋ Refactor Code ⇋ **Test Run** → Complete|Used for easier addition of new features/porting/performance optimization/removing "spaghetti code"/verifying new architectural thinking in practice.|

Let's start [Deploy Environment](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#English)😄

Once you've completed your first mod, you might want to take a look at [Write at the End](https://github.com/XColorful/BattleRoyale/wiki/Write-at-the-End#English).