[English](#English)

# 注册指令
_/battleroyale register manager [protocol]_

## 注册管理器
_/battleroyale register manager [protocol]_

- protocol："命名空间" + ":" + "管理器名称"
- 对于本模组指令，"battleroyale"和"cbr"均可作为命名空间

### 注册大逃杀游戏管理器
_/battleroyale register manager battleroyale:GameManager_

将[游戏管理器](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/common/game/game-manager.md)替换为大逃杀游戏管理器
- 所有[游戏子管理器](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/common/game/game-framework.md#游戏子管理器)也一并注册

### 注册死斗模式游戏进程管理器
_/battleroyale register manager battleroyale:DMGameProcessManager_

将[游戏进程管理器](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/common/game/process/battleroyale/br-game-process-manager.md)替换为死斗模式游戏进程管理器

# English
_/battleroyale register manager [protocol]_

## Register manager
_/battleroyale register manager [protocol]_

- protocol: "namespace" + ":" + "manager name"
- For this mod's commands, both "battleroyale" and "cbr" can be used as namespaces.

### Register BattleRoyale GameManager
_/battleroyale register manager battleroyale:GameManager_

Replace the [game manager](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/common/game/game-manager.md#English) with the BattleRoyale game manager
- All [game sub-managers](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/common/game/game-framework.md#English) are also registered

### Register DeathMatch GameProcessManager
_/battleroyale register manager battleroyale:DMGameProcessManager_

Replace the [game process manager](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/common/game/process/battleroyale/br-game-process-manager.md#English) with the DeathMatch game process manager