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
