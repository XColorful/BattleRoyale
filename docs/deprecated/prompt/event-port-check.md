# 

```
背景：最初BattleRoyale的forge-compat直接复制到CGC，然后CGC有更新而BattleRoyale没更新：
- 有修复一些事件的客户端拦截判定
- CGC是core和client隔离的架构，xiao.battleroyale.compat.forge.event被拆成了dev.xcolorful.customgun.forgeclient.event和dev.xcolorful.customgun.forge.event

现在需要检查BattleRoyale每个forge事件类在CGC是否有更新，如果有则把CGC的修改应用回BattleRoyale上
```

# 

I'll compare the forge event classes between both projects. Let me locate them first.

Now let me see the actual diffs for each shared class, starting with the event managers.

The diff direction needs care — let me trace CGC's own history since the copy to isolate actual CGC updates.

Let me do a stricter semantic comparison (strip comments/whitespace) across all shared event classes to make sure nothing is missed.

我已完成逐类对比（BR 的 33 个 forge 事件类 + 30 个事件管理器 vs CGC 1.20.1 HEAD，2026-10-19）。结论和你的预期有出入，先汇报事实，再确认移植范围。

**对比结果**（已忽略包名/import/空白差异）：

| 类别 | 结果 |
|---|---|
| 27 个类（含全部 `events/*EventManager`、`AbstractEventCommon`、Living/Respawn/ServerTick 等事件类） | **完全一致**，无任何更新 |
| 5 个交互事件（EntityInteract / EntityInteractSpecific / LeftClickBlock / RightClickBlock / RightClickItem） | 仅命名/工具差异：CGC 用 `getLogicalSide()`+`McSideHelper.convert()`，BR 用 `getMcSide()`，**语义等价**；CGC 用 `Vec3Utils.getCenter(pos)` 包装（CGC 核心有这个 helper，BR 没有），1.20.1 行为无差别 |
| `ForgeRenderGuiEvent` | CGC 在 "Add prepare render gui event" 中**新增** `getPartialTick()`（并同步加到 `IRenderGuiEvent`），BR 的 API 从未有过；属功能新增而非修复 |
| LivingHeal / LivingUseTotem / ItemTooltip / SubmitCustomGeometry | 仅 `getType()` 换行格式差异 |
| `ForgeEventRegister` | **唯一实质行为差异**：CGC 有 `EventType.isClientSideOnly` + 客户端注册委托（即你说的"客户端拦截判定"）；BR 没有 flag，客户端/渲染管理器全在公共 switch 里 |

补充：CGC 的 "Add logical side only to forge event"（`ILogicalSideOnly`）只加在 `EntityJoinLevelEvent`、`TagsUpdatedEvent` 上，这两个事件类型 BR 根本没有，所以没有对应类可同步。

也就是说：**共享类里没有找到你描述的"逐事件客户端拦截修复"**；能对应的只有 `ForgeEventRegister` 的客户端路由守卫，而它和 CGC 的 core/client 拆分绑在一起。

好的，不做任何修改。最终结论：

**BR 的 forge 事件类与 CGC 在功能上等价，无需回移。**

- 33 个事件类 + 30 个事件管理器中，27 个完全一致；5 个交互事件仅 `getMcSide()` 与 `getLogicalSide()` 的 API 命名差异（语义相同）及 `Vec3Utils.getCenter` 包装差异；4 个仅格式差异。
- CGC 的"客户端拦截判定"实际是 `ForgeEventRegister` 的 `isClientSideOnly` 路由，与 core/client 架构拆分绑定；按你的决定不移植。
- `ForgeRenderGuiEvent.getPartialTick()` 属 CGC 新增 API，非修复，不移植。
- CGC 的 `ILogicalSideOnly` 改动只涉及 BR 没有的 `EntityJoinLevelEvent`/`TagsUpdatedEvent`，无对应类可同步。

工作区未产生代码改动，仅保留原有的 [docs/deprecated/prompt/#63 event-port.md](docs/deprecated/prompt/#63%20event-port.md) 暂存文件。
