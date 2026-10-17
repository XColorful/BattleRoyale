> #62 提示词

# fork3

```
IMessage最初是从BattleRoyale抄过来的，CGC用的跟BattleRoyale相同的结构和worktree，现在用相同规则在BattleRoyale修改：
- BattleRoyale没有handshake注册，属于CGC专属新增的部分不需要同步到BattleRoyale
- 先处理forge侧，neoforge侧稍后
```

# 

先在 BattleRoyale 各 worktree 中核对 IMessage 结构与消息类分布。

BattleRoyale 的结构更简单（只有 4 个消息、无 handshake）；各分支的 core 内容完全一致。先在主 worktree 改 core。

BattleRoyale forge 侧完成。改动与 CGC 相同：旧带 T 签名保留为抛异常的 deprecated default，无 T 重载单向转发；`BattleRoyale` 本体消息只实现无 T 版本。BattleRoyale 没有 handshake，相关部分未同步。

## 本次修改

| worktree | 改动 | 编译 |
|---|---|---|
| BattleRoyale (core1.20.1) | [IMessage.java](core/src/main/java/xiao/battleroyale/api/network/message/IMessage.java)、4 个消息类、[ForgeNetworkAdapter.java](forge-compat/src/main/java/xiao/battleroyale/compat/forge/network/ForgeNetworkAdapter.java) | BUILD SUCCESSFUL |
| BattleRoyale-1.20.2 | ForgeNetworkAdapter 2 处调用 | BUILD SUCCESSFUL（临时套用 core 补丁验证后已撤回） |
| BattleRoyale-1.21.1forge | ForgeNetworkAdapter 2 处调用 | BUILD SUCCESSFUL（同上） |

- core 补丁已用 `git apply --check` 验证可干净应用到 1.20.2 / 1.20.4 / 1.21.1forge / 1.21.1neoforge。
- 1.20.2 与 1.20.4 的 forge 适配器逐字节相同，1.20.2 的补丁在 1.20.4 上 `git apply --check` 通过。
- 1.21.1forge 因 `PacketFlow`/`messageBuilder` 写法不同，补丁无法直接套用，因此单独修改。
- 验证编译时在 1.20.2/1.21.1forge 临时套用过 core 补丁，编译后已 `git apply -R` 撤回；两个 worktree 现在只保留适配器改动。

## Cherry-pick 表

| 分支 | core（IMessage + 4 消息） | forge 适配器 | neoforge 适配器 |
|---|---|---|---|
| BattleRoyale（core1.20.1） | 已改（首版） | 已改（SimpleChannel） | — |
| BattleRoyale-1.20.2 | 复用 BattleRoyale | 已改（ChannelBuilder，首版） | 稍后 |
| BattleRoyale-1.20.4 | 复用 BattleRoyale | 复用 1.20.2 | 稍后 |
| BattleRoyale-1.21.1forge | 复用 BattleRoyale | 已改（PacketFlow，需单独改） | — |
| BattleRoyale-1.21.1neoforge | 复用 BattleRoyale | — | 稍后 |

NeoForge 侧待办（已核对调用点）：
- 1.20.2：2 处（`messageInstance`）
- 1.20.4：2 处（`this.message` + `Runnable::run`）
- 1.21.1neoforge：2 处（调用文本与 1.20.4 相同，但文件其余部分不同）

均未提交。

# 

```
现在合并到1.20.2-1.21.1forge要处理Merge冲突，你之前在forge的修改会临时被清掉，现在处理1.20.2，1.20.4，1.21.1的Merge，neoforge稍后
```

# 

先检查三个 worktree 的合并状态和全部冲突文件。

三个 worktree 的冲突只在 ForgeNetworkAdapter。保留各分支自己的 ChannelBuilder/PacketFlow 实现，并在其上重新套用无 T 调用。

冲突已解决并暂存，开始编译三个分支。

三个分支的 Merge 冲突已解决并暂存，未提交。

## 处理结果

冲突只在 `ForgeNetworkAdapter.java`：核心改动已由 merge 自动合入，但主分支带入的是旧的 `SimpleChannel` 实现，与各分支自己的 `ChannelBuilder`/`PacketFlow` 实现冲突。

解决方式：**保留各分支 HEAD 的注册结构**，只重新套用无 T 调用：

| 分支 | 保留的注册结构 | 重新套用的改动 |
|---|---|---|
| 1.20.2 | `<T>messageBuilder(clazz, id, forgeDirection)` | `encode(buffer)`、`handle(context::enqueueWork)` |
| 1.20.4 | 同上（与 1.20.2 逐字节相同） | 同上 |
| 1.21.1forge | `messageBuilder(clazz, id).direction(forgeFlow)` | 同上 |

验证：
- 三个 worktree 均无剩余冲突，适配器相对各自 `HEAD` 的 diff 只有这两行。
- merge 带入的 `IMessage.java` 和 4 个消息类与 `BattleRoyale` 主分支逐字节一致。
- 三个分支 `:core:compileJava :forge-compat:compileJava` 均 **BUILD SUCCESSFUL**。

三个 worktree 目前仍处于 merge 进行中（已 `git add`，未执行 merge commit），可以直接 review 后各自提交。neoforge 侧未触碰。

需要我代为提交这三个 merge commit 的话告诉我。

# 

```
现在开始处理neoforge侧
```

# 

先核对三个 neoforge worktree 的 core 状态与适配器调用点。

BattleRoyale neoforge 侧完成。

## 修改内容

| worktree | 改动 | 编译 |
|---|---|---|
| 1.20.2 | NeoNetworkAdapter 2 处（`messageInstance`） | BUILD SUCCESSFUL |
| 1.20.4 | NeoNetworkAdapter 2 处（`this.message` + `Runnable::run`） | BUILD SUCCESSFUL |
| 1.21.1neoforge | NeoNetworkAdapter 2 处（调用文本同 1.20.4，但上下文不同） | BUILD SUCCESSFUL |

三处的无 T 调用：
- `encode(message, buffer)` → `encode(buffer)`
- `handle(message, handler)` → `handle(handler)`

1.20.4 的补丁无法直接应用到 1.21.1neoforge（1.20.4 是 `workHandler().execute(...)`，1.21.1 是 `context.enqueueWork(...)`，补丁上下文不匹配），因此 1.21.1neoforge 作为必要分支单独修改。

## 完整 Cherry-pick 表（BattleRoyale）

| 分支 | core（IMessage + 4 消息） | forge 适配器 | neoforge 适配器 |
|---|---|---|---|
| BattleRoyale（core1.20.1） | 首版（已提交） | 首版（已提交） | — |
| BattleRoyale-1.20.2 | 复用 main（已合并） | 首版（已提交） | 已改 |
| BattleRoyale-1.20.4 | 复用 main（已合并） | 复用 1.20.2（已合并） | 已改（与 1.20.2 结构不同） |
| BattleRoyale-1.21.1forge | 复用 main（已合并） | 单独改（已提交） | — |
| BattleRoyale-1.21.1neoforge | 复用 main（已存在） | — | 已改（与 1.20.4 上下文不同） |

三个 neoforge 适配器改动均未提交。主 worktree 当前只有 `docs/deprecated/prompt/issue/#62 IMessage refactor.md` 的修改，未触碰。
