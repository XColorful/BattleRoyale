[English](#English)

## 游戏规则管理器

#### 游戏规则管理器启动流程

- initGameConfig：读取游戏规则配置，包含MC原版规则（_/gamerule_）、大逃杀规则、其他规则设置
- initGame：应用部分游戏规则
- startGame：应用其余游戏规则，如设置玩家游戏模式

### 游戏规则属性

#### 默认游戏规则

> 如果需要替换`游戏规则管理器`并不需要所有玩家统一的游戏模式，可忽略

[![IGameruleManager](/docs/api/game/gamerule/IGameruleManager.md)](/docs/api/game/gamerule/IGameruleManager.md)

# English

## Gamerule Manager

#### Gamerule Manager Startup Flow

- initGameConfig: Reads game rule configurations, including MC vanilla rules (_/gamerule_), BattleRoyale rules, and other rule settings.
- initGame: Applies some game rules.
- startGame: Applies the remaining game rules, such as setting player game modes.

### Game Rule Property

#### Default Game Rule

> Can be ignored if replacing the `Gamerule Manager` and not requiring a uniform game mode for all players.

[![IGameruleManager](/docs/api/game/gamerule/IGameruleManager.md)](/docs/api/game/gamerule/IGameruleManager.md)